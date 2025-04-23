package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FavoriteActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Khởi tạo BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Kiểm tra null để tránh crash
        if (bottomNavigationView != null) {
            // Đặt mục "Upload" làm mục được chọn (nếu có ID tương ứng trong menu)
            // Ví dụ: bottomNavigationView.setSelectedItemId(R.id.nav_upload);

            // Thiết lập sự kiện chọn mục trong BottomNavigationView
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_library) {
                    // Chuyển sang MainActivity
                    Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_discover) {
                    // Chuyển sang DiscoverActivity
                    Intent intent = new Intent(FavoriteActivity.this, DiscoverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_chartmusic) {
                    // Chuyển sang ChartMusicActivity
                    Intent intent = new Intent(FavoriteActivity.this, ChartMusicActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Chuyển sang ProfileActivity
                    Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
        } else {
            // Xử lý trường hợp không tìm thấy BottomNavigationView
            // Có thể thêm log hoặc thông báo lỗi tại đây nếu cần
        }
    }
}