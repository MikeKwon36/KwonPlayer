package kwondeveloper.com.kwonplayer.AsyncTasks;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import kwondeveloper.com.kwonplayer.Fragments.SubFragments.AlbumFragment;
import kwondeveloper.com.kwonplayer.Fragments.SubFragments.ArtistFragment;
import kwondeveloper.com.kwonplayer.Fragments.SubFragments.ArtistFragmentRecyclerAdapter;
import kwondeveloper.com.kwonplayer.MediaObjects.ArtistFile;
import kwondeveloper.com.kwonplayer.R;

public class AsyncBuildArtistLibrary extends AsyncTask<Void,Void,Void> {

    private static Context mContext;

    //Class constructor instantiates a Context member variable which will provide access to MediaStore
    public AsyncBuildArtistLibrary(Context context) {
        mContext = context;
    }

    //AsyncTask Override methods =============================================================

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("***AsyncArtistLib", "AsyncBuildArtistLibrary thread launched and executing");
        //Queries MediaStore and returns a cursor with all unique artists, their ids
        ContentResolver contentResolver = mContext.getContentResolver();
        String[] projection = {MediaStore.Audio.Artists.ARTIST_KEY,MediaStore.Audio.Artists.ARTIST,MediaStore.Audio.Artists._ID,MediaStore.Audio.Artists.NUMBER_OF_TRACKS};
        Cursor cursor = contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,null,null,MediaStore.Audio.Artists.ARTIST + " ASC");

        ArtistFragment.mArtists.clear();
        if (cursor == null || !cursor.moveToFirst()) {
            // query failed OR no media on the device
        } else {
            while (cursor.moveToNext()) {
                ArtistFile artist = new ArtistFile();
                artist.setmID(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
                artist.setmName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
                ArtistFragment.mArtists.add(artist);
            }
        }
        cursor.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ArtistFragment.mArtistAdapter.notifyDataSetChanged();
    }
}