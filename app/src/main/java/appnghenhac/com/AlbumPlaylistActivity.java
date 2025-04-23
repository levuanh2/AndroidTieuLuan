package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import appnghenhac.com.adapter.AlbumPlaylistPagerAdapter;
import appnghenhac.com.admin_activity.AddEditAlbumActivity;
import appnghenhac.com.admin_activity.AddEditPlaylistActivity;

public class AlbumPlaylistActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_album_playlist);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading layout: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize views
        try {
            tabLayout = findViewById(R.id.tab_layout);
            viewPager = findViewById(R.id.view_pager);
            btnAdd = findViewById(R.id.btn_add);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set up ViewPager2 with adapter
        try {
            AlbumPlaylistPagerAdapter adapter = new AlbumPlaylistPagerAdapter(this);
            viewPager.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error setting ViewPager adapter: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Connect TabLayout with ViewPager2
        try {
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                tab.setText(position == 0 ? "Albums" : "Playlists");
            }).attach();
        } catch (Exception e) {
            Toast.makeText(this, "Error attaching TabLayout: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Add button click listener
        btnAdd.setOnClickListener(v -> {
            int currentTab = viewPager.getCurrentItem();
            Intent intent;
            try {
                if (currentTab == 0) {
                    intent = new Intent(AlbumPlaylistActivity.this, AddEditAlbumActivity.class);
                } else {
                    intent = new Intent(AlbumPlaylistActivity.this, AddEditPlaylistActivity.class);
                }
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error starting activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}