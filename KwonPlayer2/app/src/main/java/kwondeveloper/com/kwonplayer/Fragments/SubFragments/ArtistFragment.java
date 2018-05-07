package kwondeveloper.com.kwonplayer.Fragments.SubFragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import kwondeveloper.com.kwonplayer.AnalyticsApplication;
import kwondeveloper.com.kwonplayer.AsyncTasks.AsyncBuildAlbumLibrary;
import kwondeveloper.com.kwonplayer.AsyncTasks.AsyncBuildAndroidPlaylistsLibrary;
import kwondeveloper.com.kwonplayer.AsyncTasks.AsyncBuildArtistLibrary;
import kwondeveloper.com.kwonplayer.MainActivity;
import kwondeveloper.com.kwonplayer.MediaObjects.ArtistFile;
import kwondeveloper.com.kwonplayer.R;

public class ArtistFragment extends Fragment {
    public static ArrayList<ArtistFile> mArtists;
    public static ArtistFragmentRecyclerAdapter mArtistAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    private Tracker mTracker;
    private AsyncBuildArtistLibrary mAsync;
    private static ArtistFragment mInstance;

    public ArtistFragment() {
    }

    public static ArtistFragment newInstance() {
        if(mInstance == null){
            mInstance = new ArtistFragment();
        }
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.ArtistFragmentRecycler);
        mLayoutManager = new GridLayoutManager(mContext,2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mArtistAdapter = new ArtistFragmentRecyclerAdapter(mArtists);
        mRecyclerView.setAdapter(mArtistAdapter);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(mContext, getString(R.string.MainActivity_StoragePermission), Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), MainActivity.dataPerms, MainActivity.dataRequestCode);
            }
        } else {
            mAsync = new AsyncBuildArtistLibrary(mContext);
            mAsync.execute();
        }
        Log.d("***ArtistFragOnCreate", "instantiates device album library UI member variables");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mArtists==null){mArtists = new ArrayList<>();}
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
            mTracker.setScreenName(getResources().getString(R.string.analytics_device_artists_fragment));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode) {
            case MainActivity.dataRequestCode:
                boolean dataPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (dataPermissionAccepted) {
                    mAsync = new AsyncBuildArtistLibrary(mContext);
                    mAsync.execute();
                }
                break;
        }
    }
}
