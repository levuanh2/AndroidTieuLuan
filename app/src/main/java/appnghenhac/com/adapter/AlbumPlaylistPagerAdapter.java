package appnghenhac.com.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import appnghenhac.com.fragment.AlbumFragment;
import appnghenhac.com.fragment.PlaylistFragment;

public class AlbumPlaylistPagerAdapter extends FragmentStateAdapter {

    public AlbumPlaylistPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new AlbumFragment() : new PlaylistFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // Albums and Playlists
    }
}