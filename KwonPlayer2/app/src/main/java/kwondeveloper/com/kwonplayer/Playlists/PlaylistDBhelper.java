package kwondeveloper.com.kwonplayer.Playlists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import kwondeveloper.com.kwonplayer.Fragments.PlaylistFragment;
import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;

public class PlaylistDBhelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PLAYLIST_DB";
    public static final String TABLE_PLAYLISTS = "PLAYLISTS_TABLE";

    public static final String COL_ID = "_id";
    public static final String COL_PLAYLIST_TITLE = "PLAYLIST_TITLE";
    public static final String COL_SONG_ID = "SONG_ID";
    public static final String COL_SONG_NAME = "SONG_NAME";
    public static final String COL_ARTIST = "ARTIST";
    public static final String COL_ALBUM = "ALBUM";
    public static final String COL_DURATION = "DURATION";
    public static final String COL_DATA = "DATA";
    public static final String COL_ALBUM_ID = "ALBUM_ID";
    public static final String COL_COMPOSER = "COMPOSER";
    public static final String COL_ALBUM_ART = "ALBUM_ART";

    public static final String[] PLAYLIST_TABLE_COLUMNS = {COL_ID,COL_PLAYLIST_TITLE,COL_SONG_ID,COL_ARTIST,
            COL_ALBUM,COL_SONG_NAME,COL_DURATION,COL_DATA,COL_ALBUM_ID,COL_COMPOSER,COL_ALBUM_ART};

    public static final String CREATE_PLAYLIST_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYLISTS + "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PLAYLIST_TITLE + " TEXT, " + COL_SONG_ID + " INTEGER, " +
                    COL_SONG_NAME + " TEXT, " + COL_ARTIST + " TEXT, " + COL_ALBUM + " TEXT, " + COL_DATA + " TEXT, " +
                    COL_ALBUM_ID + " INTEGER, " + COL_COMPOSER + " TEXT, "  + COL_ALBUM_ART + " TEXT, " + COL_DURATION + " INTEGER)";

    private static PlaylistDBhelper mInstance;

    private static ArrayList<String> mPlaylistTitles;
    private static Handler mHandler;

    public PlaylistDBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static PlaylistDBhelper getInstance (Context context){
        mHandler = new Handler();
        if(mPlaylistTitles == null){mPlaylistTitles = new ArrayList<>();}
        return mInstance = mInstance == null? new PlaylistDBhelper(context) : mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLAYLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        this.onCreate(db);
    }

    public void populatePlaylistTitles(){
        Log.d("***PlaylistDBHelper", "populate playlist titles called");
        if(mPlaylistTitles == null){
            mPlaylistTitles = new ArrayList<>();
        } else {
            mPlaylistTitles.clear();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.query(TABLE_PLAYLISTS,new String[]{COL_PLAYLIST_TITLE},null,null,null,null,null);
                cursor.moveToFirst();
                while (cursor.moveToNext()){
                    if(!mPlaylistTitles.contains(cursor.getString(cursor.getColumnIndex(COL_PLAYLIST_TITLE)).toUpperCase())){
                        mPlaylistTitles.add(cursor.getString(cursor.getColumnIndex(COL_PLAYLIST_TITLE)).toUpperCase());
                        Log.d("***PlaylistDBHelper", cursor.getString(cursor.getColumnIndex(COL_PLAYLIST_TITLE)).toUpperCase() + " added to playlist titles list");
                    }
                }
                cursor.close();
                db.close();
            }
        };
        mHandler.post(runnable);

    }

    public String[] listAllPlaylistTitles(){
        Log.d("***PlaylistDBHelper", "list all playlist titles called");
        String[] arrayOfTitles = new String[mPlaylistTitles.size()];
        for (int i = 0; i < mPlaylistTitles.size(); i++) {
            arrayOfTitles[i] = mPlaylistTitles.get(i).toUpperCase();
            Log.d("***PlaylistDBHelper", mPlaylistTitles.get(i) + " added to list of titles");
        }
        return arrayOfTitles;
    }

    public void setCurrentPlaylist(final String playlist){
        Log.d("***PlaylistDBHelper", "getPlaylist called on " + playlist.toUpperCase());
        CurrentPlaylist.clearPlaylist();
        CurrentPlaylist.setPlaylistTitle(playlist.toUpperCase());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.query(TABLE_PLAYLISTS, PLAYLIST_TABLE_COLUMNS, COL_PLAYLIST_TITLE + " = ?"
                        , new String[]{playlist.toUpperCase()}, null, null, null, null);
                while(cursor.moveToNext()){
                    KwonMediaObject obj = new KwonMediaObject();

                    obj.setmID(cursor.getLong(cursor.getColumnIndex(COL_SONG_ID)));
                    obj.setmTitle(cursor.getString(cursor.getColumnIndex(COL_SONG_NAME)));
                    obj.setmAlbum(cursor.getString(cursor.getColumnIndex(COL_ALBUM)));
                    obj.setmArtist(cursor.getString(cursor.getColumnIndex(COL_ARTIST)));
                    obj.setmDuration(cursor.getLong(cursor.getColumnIndex(COL_DURATION)));
                    obj.setmData(cursor.getString(cursor.getColumnIndex(COL_DATA)));
                    obj.setmAlbumID(cursor.getLong(cursor.getColumnIndex(COL_ALBUM_ID)));
                    obj.setmComposer(cursor.getString(cursor.getColumnIndex(COL_COMPOSER)));
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(COL_ALBUM_ART)));
                    obj.setmAlbumArt(uri);

                    CurrentPlaylist.addMediaObject(obj);
                }
                PlaylistFragment.mPlaylistAdapter.notifyDataSetChanged();
                PlaylistFragment.mPlaylistTitle.setText(CurrentPlaylist.getPlaylistTitle());
                cursor.close();
                db.close();
            }
        };
        mHandler.post(runnable);
    }

    public void deletePlaylist(final String[] playlists){
        for (String list:playlists) {
            mPlaylistTitles.remove(list.toUpperCase());
            Log.d("***PlaylistDBHelper", list.toUpperCase() + " removed from list of playlist titles");
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                for (String playlist:playlists) {
                    String[] list = new String[]{playlist};
                    int rows = db.delete(TABLE_PLAYLISTS, COL_PLAYLIST_TITLE + " = ?", list);
                }
                db.close();
            }
        };
        mHandler.post(runnable);
    }

    public void updatePlaylistInDB(final Playlist playlist, final String newPlaylistTitle){
        Log.d("***PlaylistDBHelper", "updatePlaylistInDB called with " + newPlaylistTitle);
        if(mPlaylistTitles.contains(playlist.getPlaylistTitle().toUpperCase())){
            mPlaylistTitles.set(mPlaylistTitles.indexOf(playlist.getPlaylistTitle().toUpperCase()), newPlaylistTitle.toUpperCase());
            Log.d("***PlaylistDBHelper", "PlaylistTitles list updated with " + newPlaylistTitle.toUpperCase());
        } else {
            mPlaylistTitles.add(newPlaylistTitle.toUpperCase());
            Log.d("***PlaylistDBHelper", newPlaylistTitle.toUpperCase() + " added to PlaylistTitles list");
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                int rows = db.delete(TABLE_PLAYLISTS, COL_PLAYLIST_TITLE + " = ?", new String[]{playlist.getPlaylistTitle().toUpperCase()});
                for (KwonMediaObject obj:playlist.getAllSongsInPlaylist()) {
                    ContentValues values = new ContentValues();
                    values.put(COL_PLAYLIST_TITLE,newPlaylistTitle.toUpperCase());
                    values.put(COL_SONG_ID,obj.getmID());
                    values.put(COL_SONG_NAME, obj.getmTitle());
                    values.put(COL_ALBUM, obj.getmAlbum());
                    values.put(COL_ARTIST, obj.getmArtist());
                    values.put(COL_DURATION, obj.getmDuration());
                    values.put(COL_DATA, obj.getmData());
                    values.put(COL_ALBUM_ID, obj.getmAlbumID());
                    values.put(COL_COMPOSER, obj.getmComposer());
                    values.put(COL_ALBUM_ART, obj.getmAlbumArt().toString());
                    long is = db.insert(TABLE_PLAYLISTS, null, values);
                }
                db.close();
            }
        };
        mHandler.post(runnable);
    }
}