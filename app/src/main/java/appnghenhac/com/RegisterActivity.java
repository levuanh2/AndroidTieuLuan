package appnghenhac.com;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng layout đăng ký đã tạo (activity_register.xml)
        setContentView(R.layout.activity_register);

        // Tham chiếu đến TextView "Đã có tài khoản? Đăng nhập" trong layout
        TextView tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        // Khi nhấn vào TextView này, kết thúc RegisterActivity và quay lại LoginActivity
        tvLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
