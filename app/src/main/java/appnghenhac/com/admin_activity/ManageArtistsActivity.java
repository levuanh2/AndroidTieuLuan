package appnghenhac.com.admin_activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import appnghenhac.com.R;
import appnghenhac.com.model.DatabaseHelper;

public class ManageArtistsActivity extends AppCompatActivity {

    private EditText etArtistName;
    private Button btnAdd, btnEdit, btnDelete;
    private ListView lvArtists;
    private DatabaseHelper dbHelper;
    private List<String> artistsList;
    private ArrayAdapter<String> artistsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_artists);

        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các view
        etArtistName = findViewById(R.id.et_artist_name);
        btnAdd = findViewById(R.id.btn_add);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        lvArtists = findViewById(R.id.lv_artists);

        // Thiết lập ListView với layout tùy chỉnh
        artistsList = dbHelper.getAllArtists();
        artistsAdapter = new ArrayAdapter<String>(this, R.layout.item_artist, R.id.tv_artist_name, artistsList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvArtistName = view.findViewById(R.id.tv_artist_name);
                ImageView ivActionIcon = view.findViewById(R.id.iv_action_icon);
                tvArtistName.setText(artistsList.get(position));

                // Xử lý nhấn vào biểu tượng hành động
                ivActionIcon.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(ManageArtistsActivity.this, ivActionIcon);
                    popupMenu.getMenu().add("Sửa");
                    popupMenu.getMenu().add("Xóa");
                    popupMenu.setOnMenuItemClickListener(item -> {
                        String selectedArtist = artistsList.get(position);
                        if (item.getTitle().equals("Sửa")) {
                            showEditArtistDialog(selectedArtist, position);
                        } else if (item.getTitle().equals("Xóa")) {
                            showDeleteArtistConfirmation(selectedArtist, position);
                        }
                        return true;
                    });
                    popupMenu.show();
                });

                return view;
            }
        };
        lvArtists.setAdapter(artistsAdapter);

        // Xử lý chọn nghệ sĩ từ ListView
        lvArtists.setOnItemClickListener((parent, view, position, id) -> {
            String selectedArtist = artistsList.get(position);
            etArtistName.setText(selectedArtist);
        });

        // Xử lý nút Thêm
        btnAdd.setOnClickListener(v -> {
            String artistName = etArtistName.getText().toString().trim();
            if (artistName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean inserted = dbHelper.insertArtist(artistName);
            if (inserted) {
                Toast.makeText(this, "Đã thêm nghệ sĩ", Toast.LENGTH_SHORT).show();
                artistsList.clear();
                artistsList.addAll(dbHelper.getAllArtists());
                artistsAdapter.notifyDataSetChanged();
                etArtistName.setText("");
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Sửa
        btnEdit.setOnClickListener(v -> {
            String oldArtistName = etArtistName.getText().toString().trim();
            if (oldArtistName.isEmpty() || !artistsList.contains(oldArtistName)) {
                Toast.makeText(this, "Vui lòng chọn nghệ sĩ để sửa", Toast.LENGTH_SHORT).show();
                return;
            }
            showEditArtistDialog(oldArtistName, artistsList.indexOf(oldArtistName));
        });

        // Xử lý nút Xóa
        btnDelete.setOnClickListener(v -> {
            String artistName = etArtistName.getText().toString().trim();
            if (artistName.isEmpty() || !artistsList.contains(artistName)) {
                Toast.makeText(this, "Vui lòng chọn nghệ sĩ để xóa", Toast.LENGTH_SHORT).show();
                return;
            }
            showDeleteArtistConfirmation(artistName, artistsList.indexOf(artistName));
        });
    }

    private void showEditArtistDialog(String artistName, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa nghệ sĩ");

        final EditText input = new EditText(this);
        input.setText(artistName);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newArtistName = input.getText().toString().trim();
            if (newArtistName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean updated = dbHelper.updateArtist(artistName, newArtistName);
            if (updated) {
                Toast.makeText(this, "Đã sửa nghệ sĩ", Toast.LENGTH_SHORT).show();
                artistsList.set(position, newArtistName);
                artistsAdapter.notifyDataSetChanged();
                etArtistName.setText("");
            } else {
                Toast.makeText(this, "Sửa thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteArtistConfirmation(String artistName, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa nghệ sĩ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteArtist(artistName);
                    if (deleted) {
                        Toast.makeText(this, "Đã xóa nghệ sĩ", Toast.LENGTH_SHORT).show();
                        artistsList.remove(position);
                        artistsAdapter.notifyDataSetChanged();
                        etArtistName.setText("");
                    } else {
                        Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}