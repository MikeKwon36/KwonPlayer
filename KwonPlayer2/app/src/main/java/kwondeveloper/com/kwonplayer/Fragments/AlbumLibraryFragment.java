package kwondeveloper.com.kwonplayer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kwondeveloper.com.kwonplayer.R;

public class AlbumLibraryFragment extends Fragment {
    private static Context mContext;
    private static ViewPager mViewPager;
    private static AlbumLibraryPagerAdapter mAdapter;
    private static TabLayout mTabLayout;
    private static int mTabPosition;

    public AlbumLibraryFragment() {
    }

    public static AlbumLibraryFragment newInstance() {
        AlbumLibraryFragment fragment = new AlbumLibraryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_library, container, false);

        //Instantiate newsFragment's tabLayout, adapter, and pager
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabLayout.addTab(mTabLayout.newTab().setText(mContext.getString(R.string.AlbumLibraryFragment_AlbumTab)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mContext.getString(R.string.AlbumLibraryFragment_ArtistTab)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mContext.getString(R.string.AlbumLibraryFragment_GenreTab)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mContext.getString(R.string.AlbumLibraryFragment_PlaylistTab)));
        mAdapter = new AlbumLibraryPagerAdapter(getChildFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setHorizontalScrollBarEnabled(true);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabPosition = tab.getPosition();
                mViewPager.setCurrentItem(mTabPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        mViewPager.setCurrentItem(mTabPosition);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
