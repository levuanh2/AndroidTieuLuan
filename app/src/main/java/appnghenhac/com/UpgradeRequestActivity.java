package appnghenhac.com;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class UpgradeRequestActivity extends AppCompatActivity {

    private Button btnSendRequest;
    private TextView txtStatus;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_request);

        btnSendRequest = findViewById(R.id.btnSendRequest);
        txtStatus = findViewById(R.id.txtStatus);
        dbHelper = new DatabaseHelper(this);

        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userRole = dbHelper.getUserRole(userEmail);
        if (userRole.equals("admin")) {
            Toast.makeText(this, "Admin không cần nâng cấp tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (userRole.equals("PREMIUM")) {
            Toast.makeText(this, "Tài khoản của bạn đã là PREMIUM!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRequestStatus(userEmail);

        btnSendRequest.setOnClickListener(v -> {
            try {
                boolean success = dbHelper.sendUpgradeRequest(userEmail);
                if (success) {
                    Toast.makeText(this, "Yêu cầu nâng cấp đã được gửi!", Toast.LENGTH_SHORT).show();
                    loadRequestStatus(userEmail);
                } else {
                    String status = dbHelper.getUpgradeRequestStatus(userEmail);
                    if (status != null) {
                        Toast.makeText(this, "Yêu cầu đã tồn tại với trạng thái: " + status, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Lỗi khi gửi yêu cầu nâng cấp!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }

    private void loadRequestStatus(String userEmail) {
        String status = dbHelper.getUpgradeRequestStatus(userEmail);
        Log.d("UpgradeRequestActivity", "Request Status: " + status);
        if (status != null) {
            txtStatus.setText("Trạng thái yêu cầu: " + status);
            btnSendRequest.setEnabled(false);
            if (status.equals("pending")) {
                txtStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
            } else if (status.equals("approved")) {
                txtStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            } else if (status.equals("rejected")) {
                txtStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            }
        } else {
            txtStatus.setText("Trạng thái yêu cầu: Chưa gửi yêu cầu.");
            txtStatus.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            btnSendRequest.setEnabled(true);
        }
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_email", null);
    }
}