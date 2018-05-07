package kwondeveloper.com.kwonplayer.AsyncTasks;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import kwondeveloper.com.kwonplayer.Fragments.PlaylistFragment;
import kwondeveloper.com.kwonplayer.Fragments.SongLibraryFragment;
import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;
import kwondeveloper.com.kwonplayer.Playlists.AllSongsOnDevice;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.Playlists.Playlist;
import kwondeveloper.com.kwonplayer.Playlists.PlaylistDBhelper;
import kwondeveloper.com.kwonplayer.R;

public class AsyncBuildSongLibrary extends AsyncTask<Context,Void,Void> {


    private Context mContext;
    private PlaylistDBhelper mDBHelper;

    //Device song library
    private Uri SONG_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    //Devuce album Art library
    private Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");

    //AsyncTask Override methods =============================================================
    @Override
    protected Void doInBackground(Context... contexts) {
        Log.d("***AsyncSongLib", "AsyncBuildSongLibrary thread launched and executing");
        mContext = contexts[0];
        AllSongsOnDevice.clearAllSongs();

        // Filter only mp3s, only those marked by the MediaStore to be music and longer than 1 minute
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0" + " AND " + MediaStore.Audio.Media.DURATION + " > 60000";
                //+ " AND " + MediaStore.Audio.Media.MIME_TYPE + "= 'audio/mpeg'"
        String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        //Song specific cursor
        Cursor cursor = mContext.getContentResolver().query(SONG_URI, null, selection, null, sortOrder);

        if (cursor == null || !cursor.moveToFirst()) {
            Log.d("***AsyncSongLib", "No media found on device");
        } else {
            while (cursor.moveToNext()) {
                KwonMediaObject songData = new KwonMediaObject();
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                long artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                //build path Uri for albumArt
                Uri albumArtUri = ContentUris.withAppendedId(ALBUM_ART_URI, albumId);

                songData.setmID(id);
                songData.setmTitle(title);
                songData.setmAlbum(album);
                songData.setmArtist(artist);
                songData.setmDuration(duration);
                songData.setmArtistID(artistId);
                songData.setmAlbumID(albumId);
                songData.setmAlbumArt(albumArtUri);
                AllSongsOnDevice.addMediaObject(songData);
                Log.d("***AsyncSongLib", "song retrieved and added to song library = " + songData.getmTitle());
            }
            cursor.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(AllSongsOnDevice.getAllSongs().size()==0){
            Toast.makeText(mContext, mContext.getString(R.string.Async_buildSongLibrary_Empty_Device), Toast.LENGTH_SHORT).show();
        } else {
            //If the current playlist is empty or if it is supposed to represent all songs on the
            // device, then update it with the latest version of AllSongsOnDevice... otherwise, the
            // playlist that was running before the app was exited will resume its place
            if(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().isEmpty() || CurrentPlaylist.getPlaylistTitle().equals(mContext.getString(R.string.PlaylistFragment_Playlist_DefaultTitle))){
                CurrentPlaylist.clearPlaylist();
                CurrentPlaylist.replacePlaylist(AllSongsOnDevice.getAllSongs(),mContext.getString(R.string.PlaylistFragment_Playlist_DefaultTitle));
                PlaylistFragment.mPlaylistAdapter.notifyDataSetChanged();
                Log.d("***LibAsync-postEx", "CurrentPlaylist was replaced with AllSongsOnDevice library");
            }
            SongLibraryFragment.mLibraryAdapter.notifyDataSetChanged();
            Log.d("***LibAsync-postEx", "Total # songs on device = " + AllSongsOnDevice.getAllSongs().size());
            Log.d("***LibAsync-postEx", "Songs in current playlist = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());

            //Locate any playlists stored in database and populate the list of titles for reference
            mDBHelper = PlaylistDBhelper.getInstance(mContext);
            mDBHelper.populatePlaylistTitles();
            mDBHelper.updatePlaylistInDB(CurrentPlaylist.getPlaylist(), CurrentPlaylist.getPlaylistTitle());
            Log.d("***LibAsync-postEx", "updatePlaylist called on = " + mContext.getString(R.string.PlaylistFragment_Playlist_DefaultTitle));
        }
    }
}