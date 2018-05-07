package kwondeveloper.com.kwonplayer.AsyncTasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import kwondeveloper.com.kwonplayer.Fragments.SubFragments.AndroidPlaylistsFragment;
import kwondeveloper.com.kwonplayer.Fragments.SubFragments.AndroidPlaylistsRecyclerAdapter;
import kwondeveloper.com.kwonplayer.Fragments.SubFragments.GenreFragment;
import kwondeveloper.com.kwonplayer.MediaObjects.AndroidPlaylistFile;
import kwondeveloper.com.kwonplayer.MediaObjects.GenreFile;
import kwondeveloper.com.kwonplayer.R;

public class AsyncBuildAndroidPlaylistsLibrary extends AsyncTask<Void,Void,Void> {

    private static Context mContext;

    //Class constructor instantiates a Context member variable which will provide access to MediaStore
    public AsyncBuildAndroidPlaylistsLibrary(Context context) {
        mContext = context;
    }

    //AsyncTask Override methods =============================================================

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("***AsyncAndroidList", "AsyncBuildAndroidPlaylistsLibrary thread launched and executing");
        //Queries MediaStore and returns a cursor with all unique android playlists
        ContentResolver contentResolver = mContext.getContentResolver();
        String[] projection = {MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        Cursor cursor = contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                projection,null,null,MediaStore.Audio.Playlists.NAME + " ASC");

        AndroidPlaylistsFragment.mAndroidPlaylists.clear();
        if (cursor == null || !cursor.moveToFirst()) {
            // query failed OR no media on the device
        } else {
            while (cursor.moveToNext()) {
                AndroidPlaylistFile androidPlaylistFile = new AndroidPlaylistFile();
                androidPlaylistFile.setPlaylistID(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
                androidPlaylistFile.setPlaylistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
                AndroidPlaylistsFragment.mAndroidPlaylists.add(androidPlaylistFile);
            }
        }

        cursor.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        AndroidPlaylistsFragment.mAndroidPlaylistAdapter.notifyDataSetChanged();
    }
}