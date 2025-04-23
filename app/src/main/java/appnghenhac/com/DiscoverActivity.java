package appnghenhac.com;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView; // Sửa import
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import appnghenhac.com.adapter.SongAdapter;
import appnghenhac.com.adapter.SongAdapterHorizontal;
import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.model.Song;
import appnghenhac.com.util.SearchHandler;

public class DiscoverActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewSuggestions, recyclerViewLatest, recyclerViewFeatured;
    private SongAdapter songAdapterLatest, songAdapterFeatured;
    private SongAdapterHorizontal songAdapterSuggestions;
    private DatabaseHelper dbHelper;

    private static final int LIMIT_SONGS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_discover);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_discover) {
                    return true;
                } else if (itemId == R.id.nav_library) {
                    startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return true;
                } else if (itemId == R.id.nav_chartmusic) {
                    startActivity(new Intent(this, ChartMusicActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return true;
                }
                return false;
            });
        }

        dbHelper = new DatabaseHelper(this);

        // Gợi Ý Cho Bạn - sử dụng SongAdapterHorizontal
        recyclerViewSuggestions = findViewById(R.id.recyclerViewSuggestions);
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Xử lý khi click vào bài hát
        songAdapterSuggestions = new SongAdapterHorizontal(this, this::openPlayMusicActivity);

        // Set the suggested songs (filtered by limitList)
        songAdapterSuggestions.setSongs(limitList(dbHelper.getSuggestedSongs()));

        // Set the adapter to the RecyclerView
        recyclerViewSuggestions.setAdapter(songAdapterSuggestions);

        // Mới Cập Nhật
        recyclerViewLatest = findViewById(R.id.recyclerViewLatest);
        recyclerViewLatest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        songAdapterLatest = new SongAdapter(this, limitList(dbHelper.getLatestSongs()), this::openPlayMusicActivity);
        recyclerViewLatest.setAdapter(songAdapterLatest);

        // Nghệ Thuật Ngày
        recyclerViewFeatured = findViewById(R.id.recyclerViewFeatured);
        recyclerViewFeatured.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        songAdapterFeatured = new SongAdapter(this, limitList(dbHelper.getFeaturedSongs()));
        recyclerViewFeatured.setAdapter(songAdapterFeatured);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        android.view.MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Gọi SearchHandler để xử lý sự kiện tìm kiếm
        SearchHandler.setupSearchView(this, searchView);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_microphone) {
            Toast.makeText(this, "Microphone clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPlayMusicActivity(Song song) {
        Intent intent = new Intent(this, PlayMusicActivity.class);
        intent.putExtra("selected_song", song); // truyền nguyên object Song
        startActivity(intent);
    }

    private List<Song> limitList(List<Song> songs) {
        return songs != null && songs.size() > LIMIT_SONGS
                ? songs.subList(0, LIMIT_SONGS)
                : songs;
    }
}