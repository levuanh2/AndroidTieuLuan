package appnghenhac.com.admin_activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import appnghenhac.com.R;
import appnghenhac.com.adapter.SongAdapter;
import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.model.Song;

public class SongManagementActivity extends AppCompatActivity {

    private RecyclerView rvSongs;
    private Uri selectedCoverUri;
    private SongAdapter songAdapter;
    private DatabaseHelper dbHelper;
    private EditText edtTitle, edtAlbum;
    private Spinner spinnerArtist, spinnerGenre;
    private ImageView ivCover;
    private Button btnAdd, btnSelectCover, btnSelectAudio;
    private TextView tvSelectedAudio;
    private Uri selectedAudioUri;
    private ArrayAdapter<String> artistAdapter, genreAdapter;
    private List<String> artistsList, genresList;

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
        spinnerArtist = findViewById(R.id.spinner_song_artist);
        edtAlbum = findViewById(R.id.edt_song_album);
        spinnerGenre = findViewById(R.id.spinner_song_genre); // Thay đổi từ EditText sang Spinner
        ivCover = findViewById(R.id.iv_form_song_cover);
        tvSelectedAudio = findViewById(R.id.tv_selected_audio);
        btnAdd = findViewById(R.id.btn_add_song);
        btnSelectCover = findViewById(R.id.btn_select_cover);
        btnSelectAudio = findViewById(R.id.btn_select_audio);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Thiết lập Spinner cho Artist
        artistsList = new ArrayList<>(dbHelper.getAllArtists());
        artistsList.add("Thêm nghệ sĩ mới...");
        artistAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, artistsList);
        artistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArtist.setAdapter(artistAdapter);

        // Thiết lập Spinner cho Genre
        genresList = new ArrayList<>(dbHelper.getAllGenres());
        genresList.add("Thêm thể loại mới...");
        genreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genresList);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

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
            String artist = spinnerArtist.getSelectedItem().toString();
            String album = edtAlbum.getText().toString().trim();
            String genre = spinnerGenre.getSelectedItem().toString();

            // Kiểm tra nếu chọn "Thêm nghệ sĩ mới..."
            if (artist.equals("Thêm nghệ sĩ mới...")) {
                showAddArtistDialog();
                return;
            }

            // Kiểm tra nếu chọn "Thêm thể loại mới..."
            if (genre.equals("Thêm thể loại mới...")) {
                showAddGenreDialog();
                return;
            }

            if (title.isEmpty() || artist.isEmpty() || selectedAudioUri == null) {
                Toast.makeText(SongManagementActivity.this, "Vui lòng nhập đầy đủ thông tin và chọn file nhạc", Toast.LENGTH_SHORT).show();
                return;
            }

            String coverPath = selectedCoverUri != null ? selectedCoverUri.toString() : null;
            Song newSong = new Song(title, artist, album, genre, selectedAudioUri.toString(), coverPath);
            boolean inserted = dbHelper.insertSong(this, newSong);
            if (inserted) {
                Toast.makeText(SongManagementActivity.this, "Đã thêm bài hát", Toast.LENGTH_SHORT).show();
                List<Song> updatedList = dbHelper.getAllSongs();
                songAdapter.setSongs(updatedList);
                songAdapter.notifyDataSetChanged();
                // Reset form
                edtTitle.setText("");
                spinnerArtist.setSelection(0);
                spinnerGenre.setSelection(0);
                edtAlbum.setText("");
                selectedAudioUri = null;
                selectedCoverUri = null;
                tvSelectedAudio.setText("Chưa chọn file âm thanh");
                ivCover.setImageResource(R.drawable.default_cover);
            } else {
                Toast.makeText(SongManagementActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddArtistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm nghệ sĩ mới");

        final EditText input = new EditText(this);
        input.setHint("Nhập tên nghệ sĩ");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newArtistName = input.getText().toString().trim();
            if (newArtistName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean inserted = dbHelper.insertArtist(newArtistName);
            if (inserted) {
                Toast.makeText(this, "Đã thêm nghệ sĩ", Toast.LENGTH_SHORT).show();
                artistsList.clear();
                artistsList.addAll(dbHelper.getAllArtists());
                artistsList.add("Thêm nghệ sĩ mới...");
                artistAdapter.notifyDataSetChanged();
                spinnerArtist.setSelection(artistsList.indexOf(newArtistName));
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showAddGenreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm thể loại mới");

        final EditText input = new EditText(this);
        input.setHint("Nhập tên thể loại");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newGenreName = input.getText().toString().trim();
            if (newGenreName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thể loại", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean inserted = dbHelper.insertGenre(newGenreName);
            if (inserted) {
                Toast.makeText(this, "Đã thêm thể loại", Toast.LENGTH_SHORT).show();
                genresList.clear();
                genresList.addAll(dbHelper.getAllGenres());
                genresList.add("Thêm thể loại mới...");
                genreAdapter.notifyDataSetChanged();
                spinnerGenre.setSelection(genresList.indexOf(newGenreName));
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
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