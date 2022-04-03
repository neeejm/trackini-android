package me.neeejm.trackini.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.neeejm.trackini.R;
import me.neeejm.trackini.beans.Position;

public class PositionAdpater extends RecyclerView.Adapter<PositionAdpater.PositionViewHolder>  {

    private List<Position> positions;
    private Context context;
    private final LayoutInflater inflater;

    public PositionAdpater(Context context, List<Position>  positions) {
        this.positions = positions;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }


        @NonNull
        @Override
        public PositionAdpater.PositionViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
            View contactView = inflater.inflate(R.layout.item_position, parent, false);
            return new PositionViewHolder(contactView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder (@NonNull PositionViewHolder holder,int position){
          Position pos = positions.get(position);
            holder.latitude.setText(pos.getLatitude()+"");
            holder.longitude.setText(pos.getLongitude()+"");
            holder.date.setText(pos.getDate()+"");
        }

        @Override
        public int getItemCount () {
            return positions.size();
        }

    public static class PositionViewHolder extends RecyclerView.ViewHolder {
        TextView longitude, latitude, date;

        public PositionViewHolder(@NonNull View itemView) {
            super(itemView);

            longitude = itemView.findViewById(R.id.label_longitude);
            latitude = itemView.findViewById(R.id.label_latitude);
            date = itemView.findViewById(R.id.label_date);
        }
    }
}
