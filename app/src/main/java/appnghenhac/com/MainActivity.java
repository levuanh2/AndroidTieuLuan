package appnghenhac.com;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView rvFunctions, rvRecentSongs;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra trạng thái đăng nhập
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        // Thiết lập Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_library);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_library) {
                    return true;
                } else if (itemId == R.id.nav_discover) {
                    Intent intent = new Intent(MainActivity.this, DiscoverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                } else if (itemId == R.id.nav_chartmusic) {
                    Intent intent = new Intent(MainActivity.this, ChartMusicActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
                return false;
            });
        } else {
            Toast.makeText(this, "Không tìm thấy thanh điều hướng!", Toast.LENGTH_SHORT).show();
        }

        // Thiết lập RecyclerView cho danh sách chức năng
        rvFunctions = findViewById(R.id.rvFunctions);
        rvFunctions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<FunctionItem> functionList = new ArrayList<>();
        functionList.add(new FunctionItem("Yêu thích", R.drawable.ic_favorite, "#1E88E5", FavoriteActivity.class));
        functionList.add(new FunctionItem("Đã tải", R.drawable.ic_download, "#43A047", DownloadActivity.class));
        functionList.add(new FunctionItem("Upload", R.drawable.ic_upload, "#F4511E", UploadActivity.class));
        functionList.add(new FunctionItem("Nghệ sĩ", R.drawable.ic_artist_music, "#7B1FA2", ArtistActivity.class));
        FunctionAdapter functionAdapter = new FunctionAdapter(functionList);
        rvFunctions.setAdapter(functionAdapter);

        // Thiết lập RecyclerView cho "Nghe gần đây"
        rvRecentSongs = findViewById(R.id.rvRecentSongs);
        rvRecentSongs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Song> recentSongs = new ArrayList<>();
         // TODO: Lấy dữ liệu động từ DatabaseHelper
        SongAdapter songAdapter = new SongAdapter(this, recentSongs);
        rvRecentSongs.setAdapter(songAdapter);

        // Thiết lập Playlist/Album
        findViewById(R.id.playlistCard).setOnClickListener(v -> {
            // TODO: Mở PlaylistActivity
            Toast.makeText(this, "Mở Playlist", Toast.LENGTH_SHORT).show();
        });
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_email", null);
    }
}

// Lớp mô hình cho chức năng
class FunctionItem {
    String name;
    int iconResId;
    String backgroundColor;
    Class<?> targetActivity;

    FunctionItem(String name, int iconResId, String backgroundColor, Class<?> targetActivity) {
        this.name = name;
        this.iconResId = iconResId;
        this.backgroundColor = backgroundColor;
        this.targetActivity = targetActivity;
    }
}

// Adapter cho danh sách chức năng
class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {
    private List<FunctionItem> functionList;

    FunctionAdapter(List<FunctionItem> functionList) {
        this.functionList = functionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FunctionItem item = functionList.get(position);
        holder.functionName.setText(item.name);
        holder.functionIcon.setImageResource(item.iconResId);
        holder.cardFunction.setCardBackgroundColor(Color.parseColor(item.backgroundColor));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), item.targetActivity);
            holder.itemView.getContext().startActivity(intent);
            ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public int getItemCount() {
        return functionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView functionIcon;
        TextView functionName;
        CardView cardFunction;

        ViewHolder(View itemView) {
            super(itemView);
            functionIcon = itemView.findViewById(R.id.functionIcon);
            functionName = itemView.findViewById(R.id.functionName);
            cardFunction = itemView.findViewById(R.id.cardFunction);
        }
    }
}