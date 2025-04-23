package appnghenhac.com.admin_activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import appnghenhac.com.R;
import appnghenhac.com.model.DatabaseHelper;

public class ManageGenresActivity extends AppCompatActivity {

    private TextInputEditText etGenreName;
    private TextInputLayout tilGenreName;
    private Button btnAdd, btnEdit, btnDelete;
    private ListView lvGenres;
    private DatabaseHelper dbHelper;
    private List<String> genresList;
    private ArrayAdapter<String> genresAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_genres);

        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các view
        tilGenreName = findViewById(R.id.til_genre_name);
        etGenreName = findViewById(R.id.et_genre_name);
        btnAdd = findViewById(R.id.btn_add);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        lvGenres = findViewById(R.id.lv_genres);

        // Khởi tạo danh sách thể loại
        genresList = dbHelper.getAllGenres();
        if (genresList == null || genresList.isEmpty()) {
            Toast.makeText(this, "Không có thể loại nào để hiển thị", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("ManageGenresActivity", "Danh sách thể loại ban đầu: " + genresList.toString());
        }

        // Thiết lập ListView với layout tùy chỉnh
        genresAdapter = new ArrayAdapter<String>(this, R.layout.item_genre, R.id.tv_genre_name, genresList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvGenreName = view.findViewById(R.id.tv_genre_name);
                ImageView ivActionIcon = view.findViewById(R.id.iv_action_icon);
                tvGenreName.setText(genresList.get(position));

                // Xử lý nhấn vào biểu tượng hành động
                ivActionIcon.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(ManageGenresActivity.this, ivActionIcon);
                    popupMenu.getMenu().add("Sửa");
                    popupMenu.getMenu().add("Xóa");
                    popupMenu.setOnMenuItemClickListener(item -> {
                        String selectedGenre = genresList.get(position);
                        if (item.getTitle().equals("Sửa")) {
                            showEditGenreDialog(selectedGenre, position);
                        } else if (item.getTitle().equals("Xóa")) {
                            showDeleteGenreConfirmation(selectedGenre, position);
                        }
                        return true;
                    });
                    popupMenu.show();
                });

                return view;
            }
        };
        lvGenres.setAdapter(genresAdapter);

        // Xử lý chọn thể loại từ ListView
        lvGenres.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGenre = genresList.get(position);
            etGenreName.setText(selectedGenre);
            Log.d("ManageGenresActivity", "Đã chọn thể loại: " + selectedGenre);
        });

        // Xử lý nút Thêm
        btnAdd.setOnClickListener(v -> {
            // Lấy dữ liệu từ TextInputEditText và làm sạch
            if (etGenreName == null) {
                Log.e("ManageGenresActivity", "TextInputEditText etGenreName là null");
                Toast.makeText(this, "Lỗi giao diện, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                return;
            }

            String genreName = etGenreName.getText() != null ? etGenreName.getText().toString().trim() : "";
            Log.d("ManageGenresActivity", "Tên thể loại nhập vào: " + genreName);

            if (genreName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thể loại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm thể loại vào cơ sở dữ liệu
            boolean inserted = dbHelper.insertGenre(genreName);
            if (inserted) {
                Toast.makeText(this, "Đã thêm thể loại: " + genreName, Toast.LENGTH_SHORT).show();
                // Cập nhật lại danh sách thể loại
                genresList.clear();
                genresList.addAll(dbHelper.getAllGenres());
                genresAdapter.notifyDataSetChanged();
                etGenreName.setText(""); // Xóa trường nhập liệu
                Log.d("ManageGenresActivity", "Danh sách thể loại sau khi thêm: " + genresList.toString());
            } else {
                // Kiểm tra lý do thất bại
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM Genres WHERE name = ?", new String[]{genreName});
                if (cursor.getCount() > 0) {
                    Toast.makeText(this, "Thể loại '" + genreName + "' đã tồn tại", Toast.LENGTH_SHORT).show();
                    Log.w("ManageGenresActivity", "Thể loại '" + genreName + "' đã tồn tại trong cơ sở dữ liệu");
                } else {
                    Toast.makeText(this, "Thêm thể loại thất bại, vui lòng kiểm tra log", Toast.LENGTH_SHORT).show();
                    Log.e("ManageGenresActivity", "Thêm thể loại '" + genreName + "' thất bại, không rõ nguyên nhân");
                }
                cursor.close();
                db.close();
            }
        });

        // Xử lý nút Sửa
        btnEdit.setOnClickListener(v -> {
            String oldGenreName = etGenreName.getText() != null ? etGenreName.getText().toString().trim() : "";
            if (oldGenreName.isEmpty() || !genresList.contains(oldGenreName)) {
                Toast.makeText(this, "Vui lòng chọn thể loại để sửa", Toast.LENGTH_SHORT).show();
                return;
            }
            showEditGenreDialog(oldGenreName, genresList.indexOf(oldGenreName));
        });

        // Xử lý nút Xóa
        btnDelete.setOnClickListener(v -> {
            String genreName = etGenreName.getText() != null ? etGenreName.getText().toString().trim() : "";
            if (genreName.isEmpty() || !genresList.contains(genreName)) {
                Toast.makeText(this, "Vui lòng chọn thể loại để xóa", Toast.LENGTH_SHORT).show();
                return;
            }
            showDeleteGenreConfirmation(genreName, genresList.indexOf(genreName));
        });
    }

    private void showEditGenreDialog(String genreName, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa thể loại");

        final TextInputEditText input = new TextInputEditText(this);
        input.setText(genreName);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newGenreName = input.getText() != null ? input.getText().toString().trim() : "";
            if (newGenreName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thể loại", Toast.LENGTH_SHORT).show();
                return;
            }
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM Genres WHERE name = ? AND name != ?", new String[]{newGenreName, genreName});
            if (cursor.getCount() > 0) {
                Toast.makeText(this, "Thể loại '" + newGenreName + "' đã tồn tại", Toast.LENGTH_SHORT).show();
                cursor.close();
                db.close();
                return;
            }
            cursor.close();
            db.close();

            boolean updated = dbHelper.updateGenre(genreName, newGenreName);
            if (updated) {
                Toast.makeText(this, "Đã sửa thể loại thành: " + newGenreName, Toast.LENGTH_SHORT).show();
                genresList.set(position, newGenreName);
                genresAdapter.notifyDataSetChanged();
                etGenreName.setText("");
            } else {
                Toast.makeText(this, "Sửa thể loại thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteGenreConfirmation(String genreName, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa thể loại '" + genreName + "'? Các bài hát thuộc thể loại này sẽ được chuyển thành 'Unknown Genre'.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteGenre(genreName);
                    if (deleted) {
                        Toast.makeText(this, "Đã xóa thể loại: " + genreName, Toast.LENGTH_SHORT).show();
                        genresList.remove(position);
                        genresAdapter.notifyDataSetChanged();
                        etGenreName.setText("");
                    } else {
                        Toast.makeText(this, "Xóa thể loại thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}