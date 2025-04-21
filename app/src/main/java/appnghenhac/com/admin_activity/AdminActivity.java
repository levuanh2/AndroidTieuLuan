package appnghenhac.com.admin_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import appnghenhac.com.ContentActivity;
import appnghenhac.com.LoginActivity;
import appnghenhac.com.R;
import appnghenhac.com.model.DatabaseHelper;

public class AdminActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView logoutIcon;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DatabaseHelper(this);

        // Kiểm tra quyền admin
        String userEmail = getCurrentUserEmail();

        if (userEmail == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        String userRole = dbHelper.getUserRole(userEmail);
        if (userRole == null || !userRole.equals("admin")) {
            Toast.makeText(this, "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Hiển thị thông tin admin
        TextView tvAdminName = findViewById(R.id.tv_admin_name);
        TextView tvAdminEmail = findViewById(R.id.tv_admin_email);
        Cursor cursor = dbHelper.getReadableDatabase().query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COLUMN_FULLNAME, DatabaseHelper.COLUMN_EMAIL},
                DatabaseHelper.COLUMN_EMAIL + "=?", new String[]{userEmail}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            tvAdminName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULLNAME)));
            tvAdminEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
            cursor.close();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        }

        // Xử lý đăng xuất
        logoutIcon = findViewById(R.id.logout_icon);
        if (logoutIcon != null) {
            logoutIcon.setOnClickListener(v -> {
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                prefs.edit().clear().apply();
                redirectToLogin();
            });
        }

        // CardView for Content
        CardView cardContent = findViewById(R.id.card_content);
        if (cardContent != null) {
            cardContent.setOnClickListener(v -> {
                Intent intent = new Intent(AdminActivity.this, ContentActivity.class);
                startActivity(intent);
            });
        }

        // CardView for Role Management
        CardView cardRoleManagement = findViewById(R.id.card_role_management);
        if (cardRoleManagement != null) {
            cardRoleManagement.setOnClickListener(v -> {
                Intent intent = new Intent(AdminActivity.this, UserManagementActivity.class);
                startActivity(intent);
            });
        }
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_email", null);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
