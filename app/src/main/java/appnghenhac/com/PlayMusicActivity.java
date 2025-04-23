package appnghenhac.com;

import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.model.Song;

public class PlayMusicActivity extends AppCompatActivity {

    private static final String TAG = "PlayMusicActivity";
    private ImageButton backButton;
    private ImageView artistImage;
    private TextView songTitle, artistName, currentTime, totalTime;
    private SeekBar seekBar;
    private ImageButton playPauseButton, previousButton;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ view
        backButton      = findViewById(R.id.back_button);
        artistImage     = findViewById(R.id.artist_image);
        songTitle       = findViewById(R.id.song_title);
        artistName      = findViewById(R.id.artist_name);
        seekBar         = findViewById(R.id.seek_bar);
        currentTime     = findViewById(R.id.current_time);
        totalTime       = findViewById(R.id.total_time);
        playPauseButton = findViewById(R.id.play_pause_button);
        previousButton  = findViewById(R.id.previous_button);

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        Song song = (Song) intent.getSerializableExtra("selected_song");
        String title, artist, coverUri, audioUri;
        if (song != null) {
            title    = song.getTitle();
            artist   = song.getArtist();
            coverUri = song.getCoverUri();
            audioUri = song.getAudioUri();
        } else {
            // Fallback khi truyền extras riêng lẻ
            title    = intent.getStringExtra("title");
            artist   = intent.getStringExtra("artist");
            coverUri = intent.getStringExtra("imageResId");
            audioUri = intent.getStringExtra("audioResId");
        }

        // Hiển thị thông tin
        if (title != null)  songTitle.setText(title);
        if (artist != null) artistName.setText(artist);

        // Load ảnh bìa bài hát
        if (coverUri != null && !coverUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(coverUri);
                Glide.with(this).load(uri).into(artistImage);
            } catch (Exception e) {
                try {
                    int resId = Integer.parseInt(coverUri);
                    artistImage.setImageResource(resId);
                } catch (NumberFormatException ex) {
                    artistImage.setImageResource(R.drawable.default_cover);
                }
            }
        } else {
            artistImage.setImageResource(R.drawable.default_cover);
        }

        // Khởi tạo MediaPlayer
        if (audioUri == null || audioUri.isEmpty()) {
            Toast.makeText(this, "Không có nguồn nhạc cho bài hát này", Toast.LENGTH_SHORT).show();
            mediaPlayer = null;
        } else {
            try {
                Uri uri = Uri.parse(audioUri);
                mediaPlayer = MediaPlayer.create(this, uri);
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "MediaPlayer error. what=" + what + " extra=" + extra);
                    Toast.makeText(this, "Không thể phát bài hát này", Toast.LENGTH_SHORT).show();
                    return true;
                });
            } catch (Exception e) {
                Log.e(TAG, "Lỗi tạo MediaPlayer cho uri: " + audioUri, e);
                Toast.makeText(this, "Không thể phát bài hát này", Toast.LENGTH_SHORT).show();
                mediaPlayer = null;
            }
        }

        if (mediaPlayer == null) {
            // Không có mediaPlayer hợp lệ
            playPauseButton.setEnabled(false);
            return;
        }

        // Thiết lập SeekBar và tổng thời gian
        int duration = mediaPlayer.getDuration();
        if (duration > 0) {
            seekBar.setMax(duration);
            totalTime.setText(formatTime(duration));
        } else {
            seekBar.setMax(0);
            totalTime.setText("0:00");
        }

        // Cập nhật SeekBar khi tương tác
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(formatTime(progress));
                }
            }
            @Override public void onStartTrackingTouch(SeekBar sb) { }
            @Override public void onStopTrackingTouch(SeekBar sb) { }
        });

        // Play/Pause
        playPauseButton.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();

                playPauseButton.setImageResource(R.drawable.ic_stop_music);
                updateSeekBar();
                // Tăng lượt nghe trong SQLite
                if (song != null) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("UPDATE Songs SET playCount = playCount + 1 WHERE id = ?", new Object[]{song.getId()});
                    Log.d(TAG, "Đã tăng lượt nghe cho bài: " + song.getTitle());
                }
            } else {
                mediaPlayer.pause();
                playPauseButton.setImageResource(R.drawable.ic_play);
            }
        });

        // Nút Previous: quay lại đầu bài
        previousButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);
                currentTime.setText(formatTime(0));
            }
        });
    }

    private void updateSeekBar() {
        if (mediaPlayer == null) return;
        int pos = mediaPlayer.getCurrentPosition();
        seekBar.setProgress(pos);
        currentTime.setText(formatTime(pos));
        if (mediaPlayer.isPlaying()) {
            handler.postDelayed(this::updateSeekBar, 500);
        }
    }

    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


