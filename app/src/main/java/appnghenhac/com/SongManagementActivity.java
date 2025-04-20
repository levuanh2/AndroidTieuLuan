package appnghenhac.com;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SongManagementActivity extends AppCompatActivity {

    private RecyclerView rvSongs;
    private Uri selectedCoverUri;
    private SongAdapter songAdapter;
    private DatabaseHelper dbHelper;
    private EditText edtTitle, edtArtist, edtAlbum, edtGenre;
    private ImageView ivCover;
    private Button btnAdd, btnSelectCover, btnSelectAudio;
    private TextView tvSelectedAudio;
    private Uri selectedAudioUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_management);

        // Kiểm tra và yêu cầu quyền READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        // Khởi tạo các view
        rvSongs = findViewById(R.id.rv_songs);
        edtTitle = findViewById(R.id.edt_song_title);
        edtArtist = findViewById(R.id.edt_song_artist);
        edtAlbum = findViewById(R.id.edt_song_album);
        edtGenre = findViewById(R.id.edt_song_genre);
        ivCover = findViewById(R.id.iv_form_song_cover);
        tvSelectedAudio = findViewById(R.id.tv_selected_audio);
        btnAdd = findViewById(R.id.btn_add_song);
        btnSelectCover = findViewById(R.id.btn_select_cover);
        btnSelectAudio = findViewById(R.id.btn_select_audio);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        List<Song> allSongs = dbHelper.getAllSongs();
        songAdapter = new SongAdapter(this, allSongs);
        rvSongs.setAdapter(songAdapter);

        // Bộ chọn file âm thanh
        ActivityResultLauncher<Intent> audioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            selectedAudioUri = uri;
                            // Lưu quyền truy cập lâu dài
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            String fileName = uri.getLastPathSegment();
                            tvSelectedAudio.setText("Đã chọn: " + fileName);
                            Toast.makeText(this, "Đã chọn file: " + fileName, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnSelectAudio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("audio/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            audioPickerLauncher.launch(intent);
        });

        // Bộ chọn ảnh bìa
        ActivityResultLauncher<String> coverPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedCoverUri = uri;
                        Glide.with(this)
                                .load(selectedCoverUri)
                                .into(ivCover);
                    }
                });

        btnSelectCover.setOnClickListener(v -> {
            coverPickerLauncher.launch("image/*");
        });

        // Sự kiện thêm bài hát
        btnAdd.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String artist = edtArtist.getText().toString().trim();
            String album = edtAlbum.getText().toString().trim();
            String genre = edtGenre.getText().toString().trim();

            if (title.isEmpty() || artist.isEmpty() || selectedAudioUri == null) {
                Toast.makeText(SongManagementActivity.this, "Vui lòng nhập đầy đủ thông tin và chọn file nhạc", Toast.LENGTH_SHORT).show();
                return;
            }

            String coverPath = selectedCoverUri != null ? selectedCoverUri.toString() : null;
            Song newSong = new Song(title, artist, album, genre, selectedAudioUri.toString(), coverPath);
            boolean inserted = dbHelper.insertSong(this, newSong); // Truyền Context
            if (inserted) {
                Toast.makeText(SongManagementActivity.this, "Đã thêm bài hát", Toast.LENGTH_SHORT).show();
                List<Song> updatedList = dbHelper.getAllSongs();
                songAdapter.setSongs(updatedList);
                songAdapter.notifyDataSetChanged();
                // Reset form
                edtTitle.setText("");
                edtArtist.setText("");
                edtAlbum.setText("");
                edtGenre.setText("");
                selectedAudioUri = null;
                selectedCoverUri = null;
                tvSelectedAudio.setText("Chưa chọn file âm thanh");
                ivCover.setImageResource(R.drawable.default_cover);
            } else {
                Toast.makeText(SongManagementActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền truy cập bộ nhớ được cấp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }
}