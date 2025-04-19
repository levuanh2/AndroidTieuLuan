package appnghenhac.com;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;

public class UpgradeRequestListActivity extends AppCompatActivity {

    private ListView listViewRequests;
    private UpgradeRequestAdapterActivity requestAdapter;
    private ArrayList<User> requestList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_request_list);

        dbHelper = new DatabaseHelper(this);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Kiểm tra quyền admin
        String userEmail = getCurrentUserEmail();
        String userRole = dbHelper.getUserRole(userEmail);
        if (!userRole.equals("admin")) {
            Toast.makeText(this, "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo ListView
        listViewRequests = findViewById(R.id.listViewRequests);

        // Tải danh sách yêu cầu nâng cấp
        requestList = dbHelper.getAllUsersWithUpgradeRequest();
        requestAdapter = new UpgradeRequestAdapterActivity(this, requestList);
        listViewRequests.setAdapter(requestAdapter);

        // Hiển thị thông báo nếu danh sách rỗng
        if (requestList.isEmpty()) {
            Toast.makeText(this, "Không có yêu cầu nâng cấp nào.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_email", null);
    }
}