package appnghenhac.com;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hiển thị layout admin (activity_admin.xml)
        setContentView(R.layout.activity_admin);

        // Khởi tạo Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
}
