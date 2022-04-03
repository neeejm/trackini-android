package me.neeejm.trackini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.List;

import me.neeejm.trackini.adapters.PositionAdpater;
import me.neeejm.trackini.beans.Position;

public class PositionActivity extends AppCompatActivity {
    private PositionAdpater positionAdpater;
    private ConstraintLayout layout;
    private RecyclerView rvPositon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        this.rvPositon = findViewById(R.id.rv_position);
        this.layout = findViewById(R.id.position_activity_layout);
        rvPositon.setLayoutManager(new LinearLayoutManager(this));
        List<Position> positions = (List<Position>) this.getIntent().getSerializableExtra("positions");
        this.positionAdpater = new PositionAdpater(this, positions);
        rvPositon.setAdapter(positionAdpater);
    }
}