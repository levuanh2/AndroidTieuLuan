package appnghenhac.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // Kiểm tra quyền admin
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userRole = new DatabaseHelper(this).getUserRole(userEmail);
        if (!userRole.equals("admin")) {
            Toast.makeText(this, "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập sự kiện click cho các CardView
        // Bài hát
        CardView cardSong = findViewById(R.id.card_bai_hat);
        cardSong.setOnClickListener(v -> {
            // TODO: Điều hướng đến màn hình quản lý bài hát nếu cần
            Intent intent = new Intent(ContentActivity.this, SongManagementActivity.class);
            startActivity(intent);
        });

        // Album/Playlist
        CardView cardAlbumPlaylist = findViewById(R.id.card_album_playlist);
        cardAlbumPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(ContentActivity.this, AlbumPlaylistActivity.class);
            startActivity(intent);
        });

        // Nghệ sĩ
        CardView cardArtist = findViewById(R.id.card_nghe_si);
        cardArtist.setOnClickListener(v -> {
            Intent intent = new Intent(ContentActivity.this, ManageArtistsActivity.class);
            startActivity(intent);
        });

        // Thể loại
        CardView cardGenre = findViewById(R.id.card_the_loai);
        cardGenre.setOnClickListener(v -> {
            Intent intent = new Intent(ContentActivity.this, ManageGenresActivity.class);
            startActivity(intent);
        });
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_email", null);
    }
}