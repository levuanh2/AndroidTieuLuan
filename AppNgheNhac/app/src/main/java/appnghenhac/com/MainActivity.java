package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Kiểm tra null để tránh crash
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_library); // Đặt mục "Thư viện" được chọn

            // Thiết lập sự kiện chọn mục trong BottomNavigationView
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_library) {
                    // Đã ở MainActivity, không làm gì cả
                    return true;
                } else if (itemId == R.id.nav_discover) {
                    // Chuyển sang DiscoverActivity
                    Intent intent = new Intent(MainActivity.this, DiscoverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_chartmusic) {
                    // Chuyển sang ChartMusicActivity
                    Intent intent = new Intent(MainActivity.this, ChartMusicActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Chuyển sang ChartMusicActivity
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
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

        // Tìm ImageView của biểu tượng Upload
        ImageView uploadIcon = findViewById(R.id.upload_icon);

        // Thiết lập sự kiện click
        uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến UploadActivity
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        // Tìm ImageView của biểu tượng Favorite
        ImageView favoriteIcon = findViewById(R.id.favorite_icon);

        // Thiết lập sự kiện click
        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến FavoriteActivity
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        // Tìm ImageView của biểu tượng Download
        ImageView downloadIcon = findViewById(R.id.download_icon);

        // Thiết lập sự kiện click
        downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến DownloadActivity
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });


    }
}