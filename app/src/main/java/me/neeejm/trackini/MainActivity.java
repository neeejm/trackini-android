package me.neeejm.trackini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.neeejm.trackini.beans.Position;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int count = 0;
    private static final int PERMISSION_ID = 3;
    private Button trackini, map, stop, posAct;
    private LinearLayout layout;
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView msg, pos;
    private View popupView;
    private final String URL = "http://192.168.1.102:8080/positions/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackini = findViewById(R.id.btn_trackini);
        map = findViewById(R.id.btn_show);
        posAct = findViewById(R.id.btn_pos);
        trackini.setOnClickListener(this);
        map.setOnClickListener(this);
        posAct.setOnClickListener(this);
        this.layout = findViewById(R.id.main_activity_layout);
        LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.position_popup, null);
        stop = popupView.findViewById(R.id.btn_stop);
        pos = popupView.findViewById(R.id.label_position);
        msg = popupView.findViewById(R.id.label_msg);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onClick(View view) {
        if (view == trackini) {
            getLastLocation();
            pos.setText("Latitude " + "x.x" + " et longitude " + "y.y");
            msg.setText("waiting for position...");
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            stop.setOnClickListener(v -> {
                count = 0;
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                popupWindow.dismiss();
            });
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
            dimBehind(popupWindow);
        }

        if (view == map) {
            new FindPositionTask(1).execute(this);
        }

        if (view == posAct) {
            new FindPositionTask(2).execute(this);
        }
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        requestNewLocationData();
                    }
                });
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(0);
//        mLocationRequest.setNumUpdates(5);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            pos.setText("Latitude " + mLastLocation.getLatitude() + " et longitude " + mLastLocation.getLongitude());
            msg.setText("new position " + ++count);
            JSONObject object = new JSONObject();

            Position p = new Position(mLastLocation.getLongitude(), mLastLocation.getLatitude(), new Date());
            try {
                //input your API parameters
                object.put("longitude", p.getLongitude());
                object.put("latitude", p.getLatitude());
                object.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Enter the correct url for your api service site
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, object,
                    response -> {
                        Log.d("api", "create: position");
                    }
                    , error -> {
                Log.e("api", String.valueOf(error));
            });
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(jsonObjectRequest);
            Log.i("GPS", "Latitude " + mLastLocation.getLatitude() + " et longitude " + mLastLocation.getLongitude());
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private class FindPositionTask extends AsyncTask<Activity, Integer, Integer> {
        private int to;
        public FindPositionTask(int to) {
            //to == which activity to go to
            // 1 -> maps else rv
            this.to = to;
        }
        @Override
        protected Integer doInBackground(Activity... activities) {
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    response -> {
                        Log.i("api", "findAll: " + response);

                        Type listType = new TypeToken<List<Position>>() {}.getType();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm");
                        Gson gson = gsonBuilder.create();

                        List<Position> positions = gson.fromJson(String.valueOf(response), listType);
                        Intent intent;
                        if (to == 1)
                            intent = new Intent(MainActivity.this, MapsActivity.class);
                        else
                            intent = new Intent(MainActivity.this, PositionActivity.class);
                        intent.putExtra("positions", (Serializable) positions);
                        startActivity(intent);
                    }
                    , error -> {
                Log.e("api", String.valueOf(error));
            });
            RequestQueue requestQueue = Volley.newRequestQueue(activities[0]);
            requestQueue.add(jsonObjectRequest);
            return null;
        }
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }
}