package appnghenhac.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class AdminActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                Intent intent = new Intent(AdminActivity.this, UserManagemantActivity.class);
                startActivity(intent);
            });
        }
    }
}