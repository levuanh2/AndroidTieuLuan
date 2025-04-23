package appnghenhac.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import appnghenhac.com.R;
import appnghenhac.com.model.Song;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ChartViewHolder> {
    public interface OnItemClickListener {
        void onClick(Song song);
    }

    private Context context;
    private List<Song> songs;
    private OnItemClickListener listener;

    public ChartAdapter(Context ctx, List<Song> list, OnItemClickListener l) {
        this.context = ctx;
        this.songs = list;
        this.listener = l;
    }

    @NonNull
    @Override
    public ChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_chart_song, parent, false);
        return new ChartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        Glide.with(context).load(song.getCoverUri()).into(holder.ivCover);
        holder.itemView.setOnClickListener(v -> listener.onClick(song));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class ChartViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvTitle, tvArtist;
        ImageView ivCover, ivOptions;

        ChartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            ivOptions = itemView.findViewById(R.id.ivOptions);
        }
    }
}