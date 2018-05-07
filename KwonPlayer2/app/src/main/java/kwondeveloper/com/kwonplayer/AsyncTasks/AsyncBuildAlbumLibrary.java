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

import java.util.ArrayList;

import kwondeveloper.com.kwonplayer.Fragments.SubFragments.AlbumFragment;
import kwondeveloper.com.kwonplayer.Fragments.SubFragments.AlbumFragmentRecyclerAdapter;
import kwondeveloper.com.kwonplayer.R;

public class AsyncBuildAlbumLibrary extends AsyncTask<Void,Void,Void> {

    private static Context mContext;
    private Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");

    public AsyncBuildAlbumLibrary(Context context) {mContext = context;}

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("***AsyncAlbumLib", "AsyncBuildAlbumLibrary thread launched and executing");
        //Queries MediaStore and returns a cursor with all unique albums and their ids
        ContentResolver contentResolver = mContext.getContentResolver();
        String[] projection = {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM};
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Audio.Albums.ALBUM + " ASC");

        AlbumFragment.mAlbums.clear();
        if (cursor == null || !cursor.moveToFirst()) {
            // query failed OR no media on the device
        } else {
            while (cursor.moveToNext()) {
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                Uri albumArtUri = ContentUris.withAppendedId(ALBUM_ART_URI, albumId);
                AlbumFragment.mAlbums.add(albumArtUri);
            }
        }
        cursor.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        AlbumFragment.mAlbumAdapter.notifyDataSetChanged();
    }
}
