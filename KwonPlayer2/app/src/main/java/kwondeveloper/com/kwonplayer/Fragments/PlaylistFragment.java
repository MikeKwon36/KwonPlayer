package kwondeveloper.com.kwonplayer.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collections;

import kwondeveloper.com.kwonplayer.AnalyticsApplication;
import kwondeveloper.com.kwonplayer.MainActivity;
import kwondeveloper.com.kwonplayer.Playlists.AllSongsOnDevice;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.Playlists.Playlist;
import kwondeveloper.com.kwonplayer.Playlists.PlaylistDBhelper;
import kwondeveloper.com.kwonplayer.R;
import kwondeveloper.com.kwonplayer.SupportClasses.SimpleItemTouchHelperCallback;

public class PlaylistFragment extends Fragment {
    public static PlaylistRecyclerAdapter mPlaylistAdapter;
    public static EditText mPlaylistTitle;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private static Context mContext;
    private static PlaylistFragmentInteractionListener mListener;
    private FloatingActionButton mFAB;
    private ItemTouchHelper mItemTouchHelper;
    private Tracker mTracker;
    private static PlaylistDBhelper mDBhelper;
    private static ArrayList<Integer> mSelectedItems;
    private static String[] mPlaylistsDisplayed;

    public PlaylistFragment() {}

    public static PlaylistFragment newInstance() {
        PlaylistFragment fragment = new PlaylistFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.playlistFragmentRecycler);
        mFAB = (FloatingActionButton) view.findViewById(R.id.playListFragmentFAB);
        mPlaylistTitle = (EditText) view.findViewById(R.id.playlistTitle);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPlaylistAdapter = new PlaylistRecyclerAdapter(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist());
        mRecyclerView.setAdapter(mPlaylistAdapter);

        //functionality to allow swipeToDismiss and drag&drop functionality in RecyclerView
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mPlaylistAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        Log.d("***PlaylistFragOnCreate", "Playlist fragment UI member variables instantiated");

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlaylistDialog();
            }
        });

        Log.d("***PlaylistFragOnCreate", "Playlist fragment FAB & EditText listeners set");

        return view;
    }

    public static void onSongSelectedInCurrentPlaylist() {
        if (mListener != null) {
            mListener.onPlaylistFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mDBhelper = PlaylistDBhelper.getInstance(mContext);
        if (context instanceof PlaylistFragmentInteractionListener) {
            mListener = (PlaylistFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
        Log.d("***PlaylistFragOnAttach", "mContext/mListener instantiated");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        //Store any changes made to CurrentPlaylist title in a temporary variable
        MainActivity.mPlaylistTitleHolder = mPlaylistTitle.getText().toString();
        Log.d("***PlaylistFragDialog", mPlaylistTitle.getText().toString() + " is the new playlist title");
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        //Google Analytics
        ConnectivityManager mgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if(info != null && info.isConnected()){
            AnalyticsApplication application = (AnalyticsApplication) mContext.getApplicationContext();
            mTracker = application.getDefaultTracker();
            mTracker.setScreenName(getResources().getString(R.string.analytics_playlist_fragment));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

        mPlaylistTitle.setText(CurrentPlaylist.getPlaylistTitle());
    }

    public interface PlaylistFragmentInteractionListener {
        void onPlaylistFragmentInteraction();
    }

    //method to launch the playlist dialog & save current playlist, with all logic connecting the database & CurrentPlaylist arrayList
    public static void launchPlaylistDialog(){
        Toast.makeText(mContext, mContext.getResources().getString(R.string.PlaylistFragment_playlist_saved), Toast.LENGTH_SHORT).show();

        //Save any song changes made to CurrentPlaylist in the the playlist database
        if(mPlaylistTitle.getText()==null){
            CurrentPlaylist.setPlaylistTitle(mContext.getString(R.string.PlaylistFragment_playlist_title_missing));
        } else {
            mDBhelper.updatePlaylistInDB(CurrentPlaylist.getPlaylist(), mPlaylistTitle.getText().toString());
        }
        Log.d("***PlaylistFragDialog", mPlaylistTitle.getText().toString() + " is the new playlist title");

        // arraylists to track the selected items
        if(mSelectedItems == null){mSelectedItems = new ArrayList<>();
        } else {mSelectedItems.clear();}
        mPlaylistsDisplayed = mDBhelper.listAllPlaylistTitles();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(mContext.getResources().getString(R.string.PlaylistFragment_Dialog_Title));
        dialog.setMultiChoiceItems(mPlaylistsDisplayed, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    mSelectedItems.add(indexSelected);
                    Log.d("***PlaylistFragViewlist", "playlist selected at index = " + indexSelected);
                } else if (mSelectedItems.contains(indexSelected)) {
                    // if the item is already in the array, remove it
                    mSelectedItems.remove(Integer.valueOf(indexSelected));
                    Log.d("***PlaylistFragViewlist", "playlist unselected at index = " + indexSelected);
                }
            }
        });
        dialog.setPositiveButton(mContext.getString(R.string.PlaylistFragment_Dialog_View_Button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //ID playlist selected and display on screen
                if (mSelectedItems.size() == 1) {
                    mDBhelper.setCurrentPlaylist(mPlaylistsDisplayed[mSelectedItems.get(0)]);
                } else {
                    Toast.makeText(mContext, R.string.PlaylistFragment_Dialog_selection_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNeutralButton(mContext.getString(R.string.PlaylistFragment_Dialog_New_Button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Create a new playlist object and add to temporary playlistsDisplayed array and
                // replace current playlist with it
                Log.d("***PlaylistFragNewlist", "# of playlists prior = " + mDBhelper.listAllPlaylistTitles().length);
                Playlist newPlaylist = new Playlist();
                CurrentPlaylist.replacePlaylist(newPlaylist);
                mPlaylistAdapter.notifyDataSetChanged();
                mPlaylistTitle.setText(CurrentPlaylist.getPlaylistTitle());
                Toast.makeText(mContext, mContext.getString(R.string.PlaylistFragment_Dialog_New_Button_Toast), Toast.LENGTH_SHORT).show();
                Log.d("***PlaylistFragNewlist", "# of playlists will change when songs add to new playlist = " + mDBhelper.listAllPlaylistTitles().length);
            }
        });
        dialog.setNegativeButton(mContext.getString(R.string.PlaylistFragment_Dialog_Delete_Button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //delete selected playlists from highest index to lowest index (to prevent
                // changing which playlists the indexes point to) and adjust mNameOfCurrentPlaylistOnScreen
                // to account for deleted indices that were below it
                Log.d("***PlaylistFragDelete", "old playlist count = " + mDBhelper.listAllPlaylistTitles().length);
                if (mSelectedItems.size() > 0) {
                    Collections.sort(mSelectedItems);
                    String[] listsToDelete = new String[mSelectedItems.size()];
                    for (int i = mSelectedItems.size() - 1; i >= 0; i--) {
                        if (CurrentPlaylist.getPlaylistTitle().equals(mPlaylistsDisplayed[mSelectedItems.get(i)])) {
                            CurrentPlaylist.replacePlaylist(AllSongsOnDevice.getAllSongs(), mContext.getResources().getString(R.string.PlaylistFragment_Playlist_DefaultTitle));
                            mPlaylistAdapter.notifyDataSetChanged();
                            mPlaylistTitle.setText(CurrentPlaylist.getPlaylistTitle());
                            Log.d("***PlaylistFragDelete", "Current Playlist updated to = " + CurrentPlaylist.getPlaylistTitle());
                        }
                        listsToDelete[i] = mPlaylistsDisplayed[mSelectedItems.get(i)];
                    }
                    mDBhelper.deletePlaylist(listsToDelete);
                    Toast.makeText(mContext, R.string.PlaylistFragment_Dialog_Delete, Toast.LENGTH_SHORT).show();
                }
                Log.d("***PlaylistFragDelete", "new playlist count = " + mDBhelper.listAllPlaylistTitles().length);
            }
        });
        dialog.create().show();
    }
}
