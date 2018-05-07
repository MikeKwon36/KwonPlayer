package kwondeveloper.com.kwonplayer.Playlists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;

/**
 * Created by Mike on 3/20/2016.
 */
public class PlaylistDBhelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PLAYLIST_DB";
    public static final String TABLE_PLAYLISTS = "PLAYLISTS_TABLE";

    public static final String COL_ID = "_id";
    public static final String COL_PLAYLIST_TITLE = "PLAYLIST_TITLE";
    public static final String COL_MEDIAOBJ_ID = "MEDIA_OBJECT_TABLE_ID";
    public static final String COL_FILENAME = "FILENAME";
    public static final String COL_SOURCE = "SOURCE";
    public static final String COL_MEDIA_TYPE = "MEDIA_TYPE";
    public static final String COL_INTERNAL_PATH = "INTERNAL_PATH";
    public static final String COL_SEARCH_QUERY = "SEARCH_QUERY";
    public static final String COL_ARTIST = "ARTIST";
    public static final String COL_SONG_NAME = "SONG_NAME";
    public static final String COL_DURATION = "DURATION";
    public static final String COL_FILE_DESCRIPTION = "FILE_DESCRIPTION";
    public static final String COL_SUBTITLES = "SUBTITLES";

    public static final String[] PLAYLIST_TABLE_COLUMNS = {COL_ID,COL_PLAYLIST_TITLE,COL_MEDIAOBJ_ID,COL_FILENAME,COL_SOURCE,
            COL_MEDIA_TYPE,COL_INTERNAL_PATH,COL_SEARCH_QUERY,COL_ARTIST,COL_SONG_NAME,COL_DURATION,
            COL_FILE_DESCRIPTION,COL_SUBTITLES};

    public static final String CREATE_PLAYLIST_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYLISTS + "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PLAYLIST_TITLE + " TEXT, " + COL_MEDIAOBJ_ID + " INTEGER, " +
                    COL_FILENAME + " TEXT, " + COL_SOURCE + " TEXT, " + COL_MEDIA_TYPE + " TEXT, " + COL_INTERNAL_PATH + " TEXT, " +
                    COL_SEARCH_QUERY + " TEXT, " + COL_ARTIST + " TEXT, " + COL_SONG_NAME + " TEXT, " +
                    COL_DURATION + " TEXT, " + COL_FILE_DESCRIPTION + " TEXT, " + COL_SUBTITLES + " TEXT)";

    private static PlaylistDBhelper mInstance;

    public PlaylistDBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static PlaylistDBhelper getInstance (Context context){
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

    public ArrayList<String> listAllPlaylistTitles(){
        ArrayList<String> allPlaylists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PLAYLISTS,new String[]{COL_PLAYLIST_TITLE},null,null,null,null,null);
        db.close();
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            if(!allPlaylists.contains(cursor.getString(cursor.getColumnIndex(COL_PLAYLIST_TITLE)))){
                allPlaylists.add(cursor.getString(cursor.getColumnIndex(COL_PLAYLIST_TITLE)));
            }
        }
        cursor.close();
        return allPlaylists;
    }

    public ArrayList<KwonMediaObject> getPlaylist(String playlist){
        ArrayList<KwonMediaObject> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PLAYLISTS, PLAYLIST_TABLE_COLUMNS, COL_PLAYLIST_TITLE
                + " = " + playlist, null, null, null, null, null);
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            KwonMediaObject obj = new KwonMediaObject();
            obj.setmDatabaseID(cursor.getInt(cursor.getColumnIndex(COL_ID)));
            obj.setmFilename(cursor.getString(cursor.getColumnIndex(COL_FILENAME)));
            obj.setmSource(cursor.getString(cursor.getColumnIndex(COL_SOURCE)));
            obj.setmMediaType(cursor.getString(cursor.getColumnIndex(COL_MEDIA_TYPE)));
            if(cursor.getString(cursor.getColumnIndex(COL_INTERNAL_PATH))!=null) {
                obj.setmInternalPath(cursor.getString(cursor.getColumnIndex(COL_INTERNAL_PATH)));}
            if(cursor.getString(cursor.getColumnIndex(COL_SEARCH_QUERY))!=null){
                obj.setmSearchQuery(cursor.getString(cursor.getColumnIndex(COL_SEARCH_QUERY)));}
            if(cursor.getString(cursor.getColumnIndex(COL_ARTIST))!=null){
                obj.setmArtist(cursor.getString(cursor.getColumnIndex(COL_ARTIST)));}
            if(cursor.getString(cursor.getColumnIndex(COL_SONG_NAME))!=null){
                obj.setmSongName(cursor.getString(cursor.getColumnIndex(COL_SONG_NAME)));}
            if(cursor.getInt(cursor.getColumnIndex(COL_DURATION))>0){
                obj.setmDuration(cursor.getInt(cursor.getColumnIndex(COL_DURATION)));}
            if(cursor.getString(cursor.getColumnIndex(COL_FILE_DESCRIPTION))!=null){
                obj.setmFileDescription(cursor.getString(cursor.getColumnIndex(COL_FILE_DESCRIPTION)));}
            if(cursor.getString(cursor.getColumnIndex(COL_SUBTITLES))!=null){
                obj.setmSubTitles(cursor.getString(cursor.getColumnIndex(COL_SUBTITLES)));}
            list.add(obj);
        }
        db.close();
        cursor.close();
        return list;
    }

    public long addMediaObj(String playlist, KwonMediaObject obj){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PLAYLIST_TITLE,playlist);
        values.put(COL_FILENAME, obj.getmFilename());
        values.put(COL_SOURCE, obj.getmSource());
        values.put(COL_MEDIA_TYPE, obj.getmMediaType());
        values.put(COL_MEDIAOBJ_ID, obj.getmDatabaseID());
        if(obj.getmInternalPath()!=null) values.put(COL_INTERNAL_PATH, obj.getmInternalPath());
        if(obj.getmSearchQuery()!=null) values.put(COL_SEARCH_QUERY, obj.getmSearchQuery());
        if(obj.getmArtist()!=null) values.put(COL_ARTIST, obj.getmArtist());
        if(obj.getmSongName()!=null) values.put(COL_SONG_NAME, obj.getmSongName());
        if(obj.getmDuration()>0) values.put(COL_DURATION, obj.getmDuration());
        if(obj.getmFileDescription()!=null) values.put(COL_FILE_DESCRIPTION, obj.getmFileDescription());
        if(obj.getmSubTitles()!=null) values.put(COL_SUBTITLES, obj.getmSubTitles());
        long newRowId = db.insert(TABLE_PLAYLISTS, null, values);
        db.close();
        return newRowId;
    }

    public long updateMediaObj(String playlist, KwonMediaObject obj){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PLAYLIST_TITLE,playlist);
        values.put(COL_FILENAME, obj.getmFilename());
        values.put(COL_SOURCE, obj.getmSource());
        values.put(COL_MEDIA_TYPE, obj.getmMediaType());
        values.put(COL_MEDIAOBJ_ID, obj.getmDatabaseID());
        if(obj.getmInternalPath()!=null) values.put(COL_INTERNAL_PATH, obj.getmInternalPath());
        if(obj.getmSearchQuery()!=null) values.put(COL_SEARCH_QUERY, obj.getmSearchQuery());
        if(obj.getmArtist()!=null) values.put(COL_ARTIST, obj.getmArtist());
        if(obj.getmSongName()!=null) values.put(COL_SONG_NAME, obj.getmSongName());
        if(obj.getmDuration()>0) values.put(COL_DURATION, obj.getmDuration());
        if(obj.getmFileDescription()!=null) values.put(COL_FILE_DESCRIPTION, obj.getmFileDescription());
        if(obj.getmSubTitles()!=null) values.put(COL_SUBTITLES, obj.getmSubTitles());
        int rowsUpdated = db.update(TABLE_PLAYLISTS, values,
                COL_PLAYLIST_TITLE + " = " + playlist + " AND " + COL_MEDIAOBJ_ID + " = " + obj.getmDatabaseID(),null);
        db.close();
        return rowsUpdated;
    }

    public int deletePlaylist(String playlist){
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PLAYLISTS,COL_PLAYLIST_TITLE + " = " + playlist,null);
        db.close();
        return rowsDeleted;
    }

    public int deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PLAYLISTS, "1", null);
        db.close();
        return rowsDeleted;
    }
}
