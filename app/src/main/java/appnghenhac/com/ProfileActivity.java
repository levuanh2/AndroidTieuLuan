package appnghenhac.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        // Lấy thông tin người dùng hiện tại
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Hiển thị thông tin người dùng
        TextView tvName = findViewById(R.id.textViewName);
        TextView tvRole = findViewById(R.id.textViewRole);
        Cursor cursor = dbHelper.getReadableDatabase().query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COLUMN_FULLNAME, DatabaseHelper.COLUMN_ROLE},
                DatabaseHelper.COLUMN_EMAIL + "=?", new String[]{userEmail}, null, null, null);
        if (cursor.moveToFirst()) {
            tvName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULLNAME)));
            tvRole.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE)));
        }
        cursor.close();

        // Hiển thị nút truy cập AdminActivity cho admin
        String userRole = dbHelper.getUserRole(userEmail);
        Button upgradeButton = findViewById(R.id.upgrade_button);
        if (userRole.equals("admin")) {
            upgradeButton.setText("Quản trị viên");
            upgradeButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, AdminActivity.class);
                startActivity(intent);
            });
        } else {
            upgradeButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, UpgradeRequestActivity.class);
                startActivity(intent);
            });
        }

        // Khởi tạo BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    return true;
                } else if (itemId == R.id.nav_library) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_discover) {
                    Intent intent = new Intent(ProfileActivity.this, DiscoverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_chartmusic) {
                    Intent intent = new Intent(ProfileActivity.this, ChartMusicActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
        }

        // Xử lý các mục menu
        LinearLayout musicPlayerItem = findViewById(R.id.music_player_item);
        if (musicPlayerItem != null) {
            musicPlayerItem.setOnClickListener(v -> {
                // TODO: Mở hoạt động trình phát nhạc
                Toast.makeText(this, "Mở trình phát nhạc", Toast.LENGTH_SHORT).show();
            });
        }

        LinearLayout themeItem = findViewById(R.id.theme_item);
        if (themeItem != null) {
            themeItem.setOnClickListener(v -> {
                // TODO: Mở hoạt động giao diện chủ đề
                Toast.makeText(this, "Mở giao diện chủ đề", Toast.LENGTH_SHORT).show();
            });
        }

        LinearLayout downloadItem = findViewById(R.id.download_item);
        if (downloadItem != null) {
            downloadItem.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, DownloadActivity.class);
                startActivity(intent);
            });
        }

        LinearLayout libraryItem = findViewById(R.id.library_item);
        if (libraryItem != null) {
            libraryItem.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        }

        LinearLayout notificationsItem = findViewById(R.id.notifications_item);
        if (notificationsItem != null) {
            notificationsItem.setOnClickListener(v -> {
                // TODO: Mở hoạt động thông báo
                Toast.makeText(this, "Mở thông báo", Toast.LENGTH_SHORT).show();
            });
        }

        LinearLayout otherItem = findViewById(R.id.other_item);
        if (otherItem != null) {
            otherItem.setOnClickListener(v -> {
                // TODO: Mở hoạt động khác
                Toast.makeText(this, "Mở mục khác", Toast.LENGTH_SHORT).show();
            });
        }

        // Xử lý sự kiện đăng xuất
        LinearLayout logoutLayout = findViewById(R.id.logoutLayout);
        if (logoutLayout != null) {
            logoutLayout.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            prefs.edit().clear().apply();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Không", null)
                        .show();
            });
        }
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_email", null);
    }
}