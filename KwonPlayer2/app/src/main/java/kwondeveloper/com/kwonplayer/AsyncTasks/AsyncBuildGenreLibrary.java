package kwondeveloper.com.kwonplayer.AsyncTasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import kwondeveloper.com.kwonplayer.Fragments.SubFragments.GenreFragment;
import kwondeveloper.com.kwonplayer.Fragments.SubFragments.GenreFragmentRecyclerAdapter;
import kwondeveloper.com.kwonplayer.MediaObjects.GenreFile;
import kwondeveloper.com.kwonplayer.R;

public class AsyncBuildGenreLibrary extends AsyncTask<Void,Void,Void> {

    private static Context mContext;

    //Class constructor instantiates a Context member variable which will provide access to MediaStore
    public AsyncBuildGenreLibrary(Context context) {
        mContext = context;
    }

    //AsyncTask Override methods =============================================================

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("***AsyncGenreLib", "AsyncBuildGenreLibrary thread launched and executing");
        //Queries MediaStore and returns a cursor with all unique genres and their ids
        ContentResolver contentResolver = mContext.getContentResolver();
        String[] projection = {MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME};
        Cursor cursor = contentResolver.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                projection,null,null,MediaStore.Audio.Genres.NAME + " ASC");

        GenreFragment.mGenres.clear();
        if (cursor == null || !cursor.moveToFirst()) {
            // query failed OR no media on the device
        } else {
            while (cursor.moveToNext()) {
                GenreFile genre = new GenreFile();
                genre.setGenreID(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Genres._ID)));
                genre.setGenreName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME)));
                GenreFragment.mGenres.add(genre);
            }
        }
        cursor.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        GenreFragment.mGenreAdapter.notifyDataSetChanged();
    }
}