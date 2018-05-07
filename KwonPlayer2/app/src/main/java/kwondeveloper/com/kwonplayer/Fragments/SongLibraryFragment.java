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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import kwondeveloper.com.kwonplayer.AnalyticsApplication;
import kwondeveloper.com.kwonplayer.Playlists.AllSongsOnDevice;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.Playlists.Playlist;
import kwondeveloper.com.kwonplayer.Playlists.PlaylistDBhelper;
import kwondeveloper.com.kwonplayer.R;

public class SongLibraryFragment extends Fragment {
    public static SongLibraryRecyclerAdapter mLibraryAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFAB;
    private PlaylistDBhelper mDBhelper;
    private Context mContext;
    private static LibraryFragmentInteractionListener mListener;
    private Tracker mTracker;

    public SongLibraryFragment() {}

    public static SongLibraryFragment newInstance() {
        SongLibraryFragment fragment = new SongLibraryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_library, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.folderFragmentRecycler);
        mFAB = (FloatingActionButton) view.findViewById(R.id.folderFragmentFAB);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLibraryAdapter = new SongLibraryRecyclerAdapter(AllSongsOnDevice.getAllSongs());
        mRecyclerView.setAdapter(mLibraryAdapter);
        mDBhelper = PlaylistDBhelper.getInstance(mContext);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlaylistDialog();
            }
        });

        Log.d("***LibraryFragOnCreate", "instantiates device library UI member variables");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof LibraryFragmentInteractionListener) {
            mListener = (LibraryFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }

        Log.d("***LibraryFragOnAttach", "instantiates mContext, mListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
            mTracker.setScreenName(getResources().getString(R.string.analytics_device_songs_fragment));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public static void onSongSelected () {
        if (mListener != null) {
            mListener.onLibraryFragmentInteraction();
        }
    }

    public interface LibraryFragmentInteractionListener {
        void onLibraryFragmentInteraction();
    }

    private void launchPlaylistDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(mContext.getResources().getString(R.string.SongLibraryFragment_Dialog_Title));
        dialog.setPositiveButton(mContext.getResources().getString(R.string.SongLibraryFragment_Dialog_YES), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Create a new playlist object and add to temporary playlistsDisplayed array and
                // replace current playlist with it
                Log.d("***LibraryFragNewlist", "# of playlists prior = " + mDBhelper.listAllPlaylistTitles().length);
                Playlist newPlaylist = new Playlist();
                newPlaylist.setPlaylistTitle(mContext.getResources().getString(R.string.PlaylistFragment_Playlist_DefaultTitle));
                newPlaylist.addPlaylistToPlaylist(AllSongsOnDevice.getAllSongs());
                Toast.makeText(mContext, mContext.getString(R.string.SongLibraryFragment_Dialog_PlaylistAdded), Toast.LENGTH_SHORT).show();
                mDBhelper.updatePlaylistInDB(CurrentPlaylist.getPlaylist(), mContext.getResources().getString(R.string.PlaylistFragment_Playlist_DefaultTitle));
                Log.d("***LibraryFragNewlist", "# of playlists updated to = " + mDBhelper.listAllPlaylistTitles().length);
            }
        });
        dialog.setNegativeButton(mContext.getResources().getString(R.string.SongLibraryFragment_Dialog_NO), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.create().show();
    }


    //this will look for a folder called test in the devices music directory
    // if it doesnt exist, it will be created. (some fixing to be done here, error if empty)
    // it then lists the files in the directory and adds them to an array.
    /*
    public void loadMusic() throws IOException {
        String extState = Environment.getExternalStorageState();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();

        //Mandatory check for availability of external storage state
        if(!extState.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mContext, "No Media Detected!", Toast.LENGTH_SHORT).show();
        } else {
            //do your file work here
            // Make sure the path exists, and create if not
            boolean exists = (new File(path)).exists();
            if (!exists){ new File(path).mkdirs(); }

            //This will return an array with all the Files (directories and files) in the external storage folder
            File sd = new File(path);
            File[] sdDirList = sd.listFiles();

            //add the files to the recycler array
            if (sdDirList != null) {
                for(int i=0;i<sdDirList.length;i++){
                    mFolders.add(sdDirList[i].getAbsolutePath());
                }

            }
        }
    }
    */
}
