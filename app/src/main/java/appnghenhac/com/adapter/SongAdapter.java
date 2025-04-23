package appnghenhac.com.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import appnghenhac.com.PlayMusicActivity;
import appnghenhac.com.R;
import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.model.Song;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context context;
    private List<Song> songs;
    private DatabaseHelper dbHelper;
    private OnSongClickListener onSongClickListener;
    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
        this.dbHelper = new DatabaseHelper(context);
    }
    // Constructor mới (3 tham số) để truyền listener
    public SongAdapter(Context context, List<Song> songs, OnSongClickListener listener) {
        this.context = context;
        this.songs = songs;
        this.dbHelper = new DatabaseHelper(context);
        this.onSongClickListener = listener;
    }

    // Interface callback
    public interface OnSongClickListener {
        void onSongClick(Song song);
    }
    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        holder.tvAlbum.setText(song.getAlbum());
        holder.tvGenre.setText(song.getGenre());

        if (song.getCoverUri() != null && !song.getCoverUri().isEmpty()) {
            Glide.with(context)
                    .load(song.getCoverUri())
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.default_cover);
        }

        // Xử lý nhấn vào biểu tượng hành động
        holder.ivActionIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.ivActionIcon);
            popupMenu.getMenu().add("Sửa");
            popupMenu.getMenu().add("Xóa");
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Sửa")) {
                    showEditDialog(song, position);
                } else if (item.getTitle().equals("Xóa")) {
                    showDeleteConfirmation(song, position);
                }
                return true;
            });
            popupMenu.show();
        });
        // Xử lý nhấn vào item để mở PlayMusicActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayMusicActivity.class);
            intent.putExtra("title", song.getTitle());
            intent.putExtra("artist", song.getArtist());
            intent.putExtra("imageResId", song.getCoverUri()); // nếu là int
            intent.putExtra("audioResId", song.getAudioUri()); // nếu là int
            context.startActivity(intent);
        });
    }

    private void showEditDialog(Song song, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_edit_song);

        EditText edtTitle = dialog.findViewById(R.id.edt_edit_song_title);
        Spinner spinnerArtist = dialog.findViewById(R.id.spinner_edit_song_artist);
        ImageView ivArtistActionIcon = dialog.findViewById(R.id.iv_artist_action_icon);
        EditText edtAlbum = dialog.findViewById(R.id.edt_edit_song_album);
        Spinner spinnerGenre = dialog.findViewById(R.id.spinner_edit_song_genre);
        ImageView ivGenreActionIcon = dialog.findViewById(R.id.iv_genre_action_icon);
        Button btnSave = dialog.findViewById(R.id.btn_save_edit);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_edit);

        // Thiết lập Spinner cho Artist
        List<String> artists = new ArrayList<>(dbHelper.getAllArtists());
        artists.add("Thêm nghệ sĩ mới...");
        ArrayAdapter<String> artistAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, artists);
        artistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArtist.setAdapter(artistAdapter);
        int artistPosition = artists.indexOf(song.getArtist());
        if (artistPosition >= 0) {
            spinnerArtist.setSelection(artistPosition);
        }

        // Thiết lập Spinner cho Genre
        List<String> genres = new ArrayList<>(dbHelper.getAllGenres());
        genres.add("Thêm thể loại mới...");
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, genres);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);
        int genrePosition = genres.indexOf(song.getGenre());
        if (genrePosition >= 0) {
            spinnerGenre.setSelection(genrePosition);
        }

        // Điền thông tin bài hát hiện tại
        edtTitle.setText(song.getTitle());
        edtAlbum.setText(song.getAlbum());

        // Xử lý nhấn vào biểu tượng hành động cho Artist
        ivArtistActionIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, ivArtistActionIcon);
            popupMenu.getMenu().add("Sửa");
            popupMenu.getMenu().add("Xóa");
            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedArtist = spinnerArtist.getSelectedItem().toString();
                if (selectedArtist.equals("Thêm nghệ sĩ mới...")) {
                    Toast.makeText(context, "Vui lòng chọn một nghệ sĩ để sửa hoặc xóa", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (item.getTitle().equals("Sửa")) {
                    showEditArtistDialog(song, position, selectedArtist);
                } else if (item.getTitle().equals("Xóa")) {
                    showDeleteArtistConfirmation(song, position, selectedArtist);
                }
                return true;
            });
            popupMenu.show();
        });

        // Xử lý nhấn vào biểu tượng hành động cho Genre
        ivGenreActionIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, ivGenreActionIcon);
            popupMenu.getMenu().add("Sửa");
            popupMenu.getMenu().add("Xóa");
            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedGenre = spinnerGenre.getSelectedItem().toString();
                if (selectedGenre.equals("Thêm thể loại mới...")) {
                    Toast.makeText(context, "Vui lòng chọn một thể loại để sửa hoặc xóa", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (item.getTitle().equals("Sửa")) {
                    showEditGenreDialog(song, position, selectedGenre);
                } else if (item.getTitle().equals("Xóa")) {
                    showDeleteGenreConfirmation(song, position, selectedGenre);
                }
                return true;
            });
            popupMenu.show();
        });

        btnSave.setOnClickListener(v -> {
            String newTitle = edtTitle.getText().toString().trim();
            String newArtist = spinnerArtist.getSelectedItem().toString();
            String newAlbum = edtAlbum.getText().toString().trim();
            String newGenre = spinnerGenre.getSelectedItem().toString();

            // Kiểm tra nếu chọn "Thêm nghệ sĩ mới..."
            if (newArtist.equals("Thêm nghệ sĩ mới...")) {
                dialog.dismiss();
                showAddArtistDialog(song, position);
                return;
            }

            // Kiểm tra nếu chọn "Thêm thể loại mới..."
            if (newGenre.equals("Thêm thể loại mới...")) {
                dialog.dismiss();
                showAddGenreDialog(song, position);
                return;
            }

            if (newTitle.isEmpty() || newArtist.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập đầy đủ tiêu đề và nghệ sĩ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật thông tin bài hát
            song.setTitle(newTitle);
            song.setArtist(newArtist);
            song.setAlbum(newAlbum);
            song.setGenre(newGenre);

            boolean updated = dbHelper.updateSong(song);
            if (updated) {
                Toast.makeText(context, "Đã cập nhật bài hát", Toast.LENGTH_SHORT).show();
                notifyItemChanged(position);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showAddArtistDialog(Song song, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thêm nghệ sĩ mới");

        final EditText input = new EditText(context);
        input.setHint("Nhập tên nghệ sĩ");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newArtistName = input.getText().toString().trim();
            if (newArtistName.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean inserted = dbHelper.insertArtist(newArtistName);
            if (inserted) {
                Toast.makeText(context, "Đã thêm nghệ sĩ", Toast.LENGTH_SHORT).show();
                showEditDialog(song, position); // Mở lại dialog chỉnh sửa
            } else {
                Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> showEditDialog(song, position));
        builder.show();
    }

    private void showAddGenreDialog(Song song, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thêm thể loại mới");

        final EditText input = new EditText(context);
        input.setHint("Nhập tên thể loại");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newGenreName = input.getText().toString().trim();
            if (newGenreName.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập tên thể loại", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean inserted = dbHelper.insertGenre(newGenreName);
            if (inserted) {
                Toast.makeText(context, "Đã thêm thể loại", Toast.LENGTH_SHORT).show();
                showEditDialog(song, position); // Mở lại dialog chỉnh sửa
            } else {
                Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> showEditDialog(song, position));
        builder.show();
    }

    private void showEditArtistDialog(Song song, int position, String oldArtistName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa nghệ sĩ");

        final EditText input = new EditText(context);
        input.setText(oldArtistName);
        input.setHint("Nhập tên nghệ sĩ mới");
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newArtistName = input.getText().toString().trim();
            if (newArtistName.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newArtistName.equals(oldArtistName)) {
                Toast.makeText(context, "Tên nghệ sĩ không thay đổi", Toast.LENGTH_SHORT).show();
                showEditDialog(song, position);
                return;
            }
            boolean updated = dbHelper.updateArtist(oldArtistName, newArtistName);
            if (updated) {
                Toast.makeText(context, "Đã sửa nghệ sĩ", Toast.LENGTH_SHORT).show();
                showEditDialog(song, position); // Mở lại dialog chỉnh sửa
            } else {
                Toast.makeText(context, "Sửa thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> showEditDialog(song, position));
        builder.show();
    }

    private void showDeleteArtistConfirmation(Song song, int position, String artistName) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa nghệ sĩ này? Các bài hát của nghệ sĩ này sẽ được chuyển thành 'Unknown Artist'.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteArtist(artistName);
                    if (deleted) {
                        Toast.makeText(context, "Đã xóa nghệ sĩ", Toast.LENGTH_SHORT).show();
                        showEditDialog(song, position); // Mở lại dialog chỉnh sửa
                    } else {
                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> showEditDialog(song, position))
                .show();
    }

    private void showEditGenreDialog(Song song, int position, String oldGenreName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa thể loại");

        final EditText input = new EditText(context);
        input.setText(oldGenreName);
        input.setHint("Nhập tên thể loại mới");
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newGenreName = input.getText().toString().trim();
            if (newGenreName.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập tên thể loại", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newGenreName.equals(oldGenreName)) {
                Toast.makeText(context, "Tên thể loại không thay đổi", Toast.LENGTH_SHORT).show();
                showEditDialog(song, position);
                return;
            }
            boolean updated = dbHelper.updateGenre(oldGenreName, newGenreName);
            if (updated) {
                Toast.makeText(context, "Đã sửa thể loại", Toast.LENGTH_SHORT).show();
                showEditDialog(song, position); // Mở lại dialog chỉnh sửa
            } else {
                Toast.makeText(context, "Sửa thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> showEditDialog(song, position));
        builder.show();
    }

    private void showDeleteGenreConfirmation(Song song, int position, String genreName) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa thể loại này? Các bài hát thuộc thể loại này sẽ được chuyển thành 'Unknown Genre'.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteGenre(genreName);
                    if (deleted) {
                        Toast.makeText(context, "Đã xóa thể loại", Toast.LENGTH_SHORT).show();
                        showEditDialog(song, position); // Mở lại dialog chỉnh sửa
                    } else {
                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> showEditDialog(song, position))
                .show();
    }

    private void showDeleteConfirmation(Song song, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài hát này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteSong(song.getId());
                    if (deleted) {
                        songs.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, songs.size());
                        Toast.makeText(context, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover, ivActionIcon;
        TextView tvTitle, tvArtist, tvAlbum, tvGenre;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_song_cover);
            tvTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtist = itemView.findViewById(R.id.tv_song_artist);
            tvAlbum = itemView.findViewById(R.id.tv_song_album);
            tvGenre = itemView.findViewById(R.id.tv_song_genre);
            ivActionIcon = itemView.findViewById(R.id.iv_action_icon);
        }
    }

}