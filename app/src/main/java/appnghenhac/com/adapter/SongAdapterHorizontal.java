package appnghenhac.com.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import appnghenhac.com.R;
import appnghenhac.com.model.DatabaseHelper;
import appnghenhac.com.model.Song;

public class SongAdapterHorizontal extends RecyclerView.Adapter<SongAdapterHorizontal.SongViewHolder> {
    private final Context context;
    private List<Song> songList;
    private final OnItemClickListener listener;
    private DatabaseHelper dbHelper;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public SongAdapterHorizontal(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.dbHelper = new DatabaseHelper(context);
        this.songList = new ArrayList<>();
        loadSongsFromDatabase(); // Load songs initially
    }

    // Method to load songs from the database
    private void loadSongsFromDatabase() {
        songList = dbHelper.getAllSongs(); // Assuming getAllSongs() returns List<Song>
        notifyDataSetChanged(); // Notify adapter to refresh the view
    }

    // Method to update song list and refresh UI
    public void setSongs(List<Song> songs) {
        this.songList = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songName.setText(song.getTitle());

        String coverUri = song.getCoverUri();
        Log.d("SongAdapterHorizontal", "coverUri: " + coverUri);

        if (coverUri != null && !coverUri.trim().isEmpty()) {
            try {
                Glide.with(context)
                        .load(Uri.parse(coverUri))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_refresh) // Icon for loading
                        .error(R.drawable.default_cover)    // Default image on error
                        .into(holder.songImage);
            } catch (Exception e) {
                Log.e("GlideError", "Glide load failed for song: " + song.getTitle(), e);
                holder.songImage.setImageResource(R.drawable.default_cover);
            }
        } else {
            holder.songImage.setImageResource(R.drawable.default_cover);
        }

        holder.cardSong.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        CardView cardSong;
        ImageView songImage;
        TextView songName;

        public SongViewHolder(View itemView) {
            super(itemView);
            cardSong = itemView.findViewById(R.id.cardSong);
            songImage = itemView.findViewById(R.id.songImage);
            songName = itemView.findViewById(R.id.songName);
        }
    }
}