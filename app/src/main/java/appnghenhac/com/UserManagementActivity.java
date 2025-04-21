package appnghenhac.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class UserManagementActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        dbHelper = new DatabaseHelper(this);

        // Kiểm tra quyền admin
        String userEmail = getCurrentUserEmail();
        String userRole = dbHelper.getUserRole(userEmail);
        if (!userRole.equals("admin")) {
            Toast.makeText(this, "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // CardView for User List
        CardView cardUserList = findViewById(R.id.card_user_list);
        if (cardUserList != null) {
            cardUserList.setOnClickListener(v -> {
                Toast.makeText(this, "Mở danh sách người dùng", Toast.LENGTH_SHORT).show();
            });
        }

        // CardView for Role Management
        CardView cardRoleManagement = findViewById(R.id.card_role_management);
        if (cardRoleManagement != null) {
            cardRoleManagement.setOnClickListener(v -> {
                Intent intent = new Intent(UserManagementActivity.this, UpgradeRequestListActivity.class);
                startActivity(intent);
            });
        }
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_email", null);
    }
}