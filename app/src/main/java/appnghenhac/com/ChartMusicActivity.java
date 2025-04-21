package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChartMusicActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_music); // Layout XML cho Chart Music

        // Khởi tạo BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationChart);

        // Kiểm tra null để tránh crash
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_chartmusic); // Đặt mục "Chart Music" được chọn

            // Thiết lập sự kiện chọn mục trong BottomNavigationView
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_chartmusic) {
                    // Đã ở ChartMusicActivity, không làm gì cả
                    return true;
                } else if (itemId == R.id.nav_library) {
                    // Chuyển sang MainActivity
                    Intent intent = new Intent(ChartMusicActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_discover) {
                    // Chuyển sang DiscoverActivity
                    Intent intent = new Intent(ChartMusicActivity.this, DiscoverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Chuyển sang ChartMusicActivity
                    Intent intent = new Intent(ChartMusicActivity.this, ProfileActivity.class);
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