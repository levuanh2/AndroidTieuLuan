package appnghenhac.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context context;
    private List<Song> songs;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }
    public void setSongs(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged(); // Cập nhật dữ liệu của adapter
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Song song = songs.get(position);
        if (song.getCoverUri() != null && !song.getCoverUri().isEmpty()) {
            Glide.with(context)
                    .load(song.getCoverUri())
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.default_cover);
        }
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        holder.tvAlbum.setText(song.getAlbum());
        holder.tvGenre.setText(song.getGenre());
    }
    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvArtist, tvAlbum, tvGenre;
        public ImageView ivCover;

        public SongViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtist = itemView.findViewById(R.id.tv_song_artist);
            tvAlbum = itemView.findViewById(R.id.tv_song_album);
            tvGenre = itemView.findViewById(R.id.tv_song_genre);
            ivCover = itemView.findViewById(R.id.iv_song_cover);
        }
    }
}
