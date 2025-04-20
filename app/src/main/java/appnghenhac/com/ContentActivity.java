package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // Initialize CardView
        CardView cardAlbumPlaylist = findViewById(R.id.card_album_playlist);
        CardView cardGenre = findViewById(R.id.card_the_loai);

        // Set click listener to navigate to AlbumPlaylistActivity
        cardAlbumPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(ContentActivity.this, AlbumPlaylistActivity.class);
            startActivity(intent);
        });

        // Navigate to GenreActivity
        cardGenre.setOnClickListener(v -> {
            Intent intent = new Intent(ContentActivity.this, GenreActivity.class);
            startActivity(intent);
        });
    }
}