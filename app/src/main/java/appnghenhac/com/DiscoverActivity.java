package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DiscoverActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // Khởi tạo BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Kiểm tra null để tránh crash
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_discover); // Đặt mục "Khám phá" được chọn

            // Thiết lập sự kiện chọn mục trong BottomNavigationView
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_discover) {
                    // Đã ở DiscoverActivity, không làm gì cả
                    return true;
                } else if (itemId == R.id.nav_library) {
                    // Chuyển về MainActivity
                    Intent intent = new Intent(DiscoverActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_chartmusic) {
                    // Chuyển sang ChartMusicActivity
                    Intent intent = new Intent(DiscoverActivity.this, ChartMusicActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Chuyển sang ChartMusicActivity
                    Intent intent = new Intent(DiscoverActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }

                return false;
            });
        } else {
            // Xử lý trường hợp không tìm thấy BottomNavigationView
            // Có thể thêm log hoặc thông báo lỗi tại đây
        }
    }
}