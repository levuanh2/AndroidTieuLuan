package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Layout XML cho Profile

        // Khởi tạo BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Kiểm tra null để tránh crash
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Đặt mục "Profile" được chọn

            // Thiết lập sự kiện chọn mục trong BottomNavigationView
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    // Đã ở ProfileActivity, không làm gì cả
                    return true;
                } else if (itemId == R.id.nav_library) {
                    // Chuyển sang MainActivity
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_discover) {
                    // Chuyển sang DiscoverActivity
                    Intent intent = new Intent(ProfileActivity.this, DiscoverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_chartmusic) {
                    // Chuyển sang ChartMusicActivity
                    Intent intent = new Intent(ProfileActivity.this, ChartMusicActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
        } else {
            // Xử lý trường hợp không tìm thấy BottomNavigationView
        }

        // Xử lý sự kiện đăng xuất
        LinearLayout logoutLayout = findViewById(R.id.logoutLayout);
        if (logoutLayout != null) {
            logoutLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Thực hiện đăng xuất và chuyển về LoginActivity
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    // Sử dụng cờ để xóa các Activity trong ngăn xếp và mở LoginActivity mới
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
