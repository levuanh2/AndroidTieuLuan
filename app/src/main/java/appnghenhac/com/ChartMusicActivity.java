package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import appnghenhac.com.adapter.ChartAdapter;
import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.model.Song;

public class ChartMusicActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ChartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_music);

        // BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationChart);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_chartmusic);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_chartmusic) {
                    return true;
                } else if (id == R.id.nav_library) {
                    startActivity(new Intent(this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return true;
                } else if (id == R.id.nav_discover) {
                    startActivity(new Intent(this, DiscoverActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return true;
                }
                return false;
            });
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewChart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load data from DB
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Song> songs = dbHelper.getSongsSortedByPlayCount();
        Log.d("ChartMusic", "Loaded " + songs.size() + " songs");
        // Adapter setup with click listener
        adapter = new ChartAdapter(this, songs, song -> {
            // Handle item click: e.g., play song or open detail
            Intent playIntent = new Intent(this, PlayMusicActivity.class);
            playIntent.putExtra("songId", song.getId());
            startActivity(playIntent);
        });
        recyclerView.setAdapter(adapter);
    }
}
