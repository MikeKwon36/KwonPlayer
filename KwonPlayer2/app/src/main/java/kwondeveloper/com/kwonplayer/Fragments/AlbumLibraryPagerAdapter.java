package kwondeveloper.com.kwonplayer.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kwondeveloper.com.kwonplayer.Fragments.SubFragments.AlbumFragment;
import kwondeveloper.com.kwonplayer.Fragments.SubFragments.AndroidPlaylistsFragment;
import kwondeveloper.com.kwonplayer.Fragments.SubFragments.ArtistFragment;
import kwondeveloper.com.kwonplayer.Fragments.SubFragments.GenreFragment;

public class AlbumLibraryPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public AlbumLibraryPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AlbumFragment albumFragment = AlbumFragment.newInstance();
                return albumFragment;
            case 1:
                ArtistFragment artistFragment = ArtistFragment.newInstance();
                return artistFragment;
            case 2:
                GenreFragment genreFragment = GenreFragment.newInstance();
                return genreFragment;
            case 3:
                AndroidPlaylistsFragment androidPlaylistsFragment = AndroidPlaylistsFragment.newInstance();
                return androidPlaylistsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}