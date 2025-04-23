package appnghenhac.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import com.google.android.gms.tasks.Task;
import appnghenhac.com.admin_activity.AdminActivity;
import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.utils.PasswordUtils;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText edtEmail, edtPassword;
    Button btnLogin;
    TextView tvRegister, tvForgot;
    ImageButton btnGoogleLogin, btnPhoneLogin;

    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // Kiểm tra trạng thái đã đăng nhập
        String userEmail = getCurrentUserEmail();
        if (userEmail != null) {
            String role = db.getUserRole(userEmail);
            if (role != null) {
                if (role.equals("admin")) {
                    startActivity(new Intent(this, AdminActivity.class));
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
                finish();
            }
        }

        // Ánh xạ view
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgot = findViewById(R.id.tvForgot);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnPhoneLogin = findViewById(R.id.btnPhoneLogin);

        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        tvForgot.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show());

        // Thiết lập Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Đặt trong strings.xml
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());

    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        String role = db.checkLogin(email, hashedPassword);

        if (role != null) {
            saveUserEmail(email);

            Toast.makeText(this, "Đăng nhập thành công với quyền: " + role, Toast.LENGTH_SHORT).show();
            Intent intent = role.equals("admin")
                    ? new Intent(this, AdminActivity.class)
                    : new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserEmail(String email) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().putString("user_email", email).apply();
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_email", null);
    }

    // Đăng nhập Google
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String email = user.getEmail();

                        if (!db.isUserExists(email)) {
                            db.insertUser(email, "", "user", "");
                        }

                        saveUserEmail(email);

                        String role = db.getUserRole(email);
                        Intent intent = role.equals("admin")
                                ? new Intent(this, AdminActivity.class)
                                : new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Xác thực Google thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
