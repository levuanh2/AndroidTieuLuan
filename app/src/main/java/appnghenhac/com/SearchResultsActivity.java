package appnghenhac.com;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import appnghenhac.com.adapter.SongAdapter;
import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.model.Song;
import appnghenhac.com.utils.SearchUtil;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSearchResults;
    private SongAdapter songAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        songAdapter = new SongAdapter(this, null);
        recyclerViewSearchResults.setAdapter(songAdapter);

        dbHelper = new DatabaseHelper(this);

        String query = getIntent().getStringExtra("search_query");
        if (query != null) {
            List<Song> allSongs = dbHelper.getAllSongs();
            List<Song> filteredSongs = SearchUtil.filterSongs(allSongs, query);
            songAdapter.setSongs(filteredSongs);
        }
    }
}