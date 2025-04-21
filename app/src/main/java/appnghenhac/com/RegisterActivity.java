package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.utils.PasswordUtils;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView tvLoginRedirect;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        db = new DatabaseHelper(this);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLoginRedirect.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void registerUser() {
        String fullname = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.checkUserExists(email)) {
            Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        boolean insertSuccess = db.insertUser(fullname, email, hashedPassword, "user");

        if (insertSuccess) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
