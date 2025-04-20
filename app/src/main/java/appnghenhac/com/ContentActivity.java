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

        // Thiết lập sự kiện click cho Bài hát
        CardView cardSong = findViewById(R.id.card_bai_hat);
        if (cardSong != null) {
            cardSong.setOnClickListener(v -> {
                Intent intent = new Intent(ContentActivity.this, SongManagementActivity.class);
                startActivity(intent);
            });
        }

        // Có thể thêm sự kiện cho các thẻ khác nếu cần
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_email", null);
    }
}