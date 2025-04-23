package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import appnghenhac.com.adapter.ChartAdapter;
import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.model.Song;

public class ChartMusicActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewChart;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_music);

        // BottomNavigationView setup
        bottomNavigationView = findViewById(R.id.bottomNavigationChart);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_chartmusic);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_chartmusic) {
                    return true;
                }
                Intent intent;
                if (id == R.id.nav_library) {
                    intent = new Intent(this, MainActivity.class);
                } else if (id == R.id.nav_discover) {
                    intent = new Intent(this, DiscoverActivity.class);
                } else {
                    intent = new Intent(this, ProfileActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            });
        }

        // RecyclerView for top chart songs
        dbHelper = new DatabaseHelper(this);
        recyclerViewChart = findViewById(R.id.recyclerViewChart);
        recyclerViewChart.setLayoutManager(new LinearLayoutManager(this));

        List<Song> chartSongs = dbHelper.getSongsSortedByPlayCount();
        ChartAdapter adapter = new ChartAdapter(this, chartSongs, this::openPlayMusicActivity);
        recyclerViewChart.setAdapter(adapter);
    }

    private void openPlayMusicActivity(Song song) {
        Intent intent = new Intent(this, PlayMusicActivity.class);
        intent.putExtra("selected_song", song);
        startActivity(intent);
    }
}