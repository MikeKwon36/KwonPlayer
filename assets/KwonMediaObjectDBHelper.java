package kwondeveloper.com.kwonplayer.MediaObjects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mike on 3/20/2016.
 */
public class KwonMediaObjectDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MEDIA_OBJECTS_DB";
    public static final String TABLE_MEDIA_OBJECTS = "MEDIA_OBJECTS_TABLE";

    public static final String COL_ID = "_id";
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

    public static final String[] MEDIAOBJ_TABLE_COLUMNS = {COL_ID,COL_FILENAME,COL_SOURCE,
            COL_MEDIA_TYPE,COL_INTERNAL_PATH,COL_SEARCH_QUERY,COL_ARTIST,COL_SONG_NAME,COL_DURATION,
            COL_FILE_DESCRIPTION,COL_SUBTITLES};

    public static final String CREATE_MEDIAOBJ_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MEDIA_OBJECTS + "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FILENAME + " TEXT, " +
                    COL_SOURCE + " TEXT, " + COL_MEDIA_TYPE + " TEXT, " + COL_INTERNAL_PATH + " TEXT, " +
                    COL_SEARCH_QUERY + " TEXT, " + COL_ARTIST + " TEXT, " + COL_SONG_NAME + " TEXT, " +
                    COL_DURATION + " TEXT, " + COL_FILE_DESCRIPTION + " TEXT, " + COL_SUBTITLES + " TEXT)";

    private static KwonMediaObjectDBHelper mInstance;

    public KwonMediaObjectDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static KwonMediaObjectDBHelper getInstance (Context context){
        return mInstance = mInstance == null? new KwonMediaObjectDBHelper(context) : mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEDIAOBJ_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA_OBJECTS);
        this.onCreate(db);
    }

    public Cursor getAllRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDIA_OBJECTS,MEDIAOBJ_TABLE_COLUMNS,null,null,null,null,null);
        db.close();
        return cursor;
    }

    public KwonMediaObject getMediaObjByFilename(String filename){
        SQLiteDatabase db = getReadableDatabase();
        KwonMediaObject obj = new KwonMediaObject();
        Cursor cursor = db.query(TABLE_MEDIA_OBJECTS, MEDIAOBJ_TABLE_COLUMNS, COL_FILENAME +
                " = " + filename, null, null, null, null, null);
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
        db.close();
        cursor.close();
        return obj;
    }

    public KwonMediaObject getMediaObjByID(int dbID){
        SQLiteDatabase db = getReadableDatabase();
        KwonMediaObject obj = new KwonMediaObject();
        Cursor cursor = db.query(TABLE_MEDIA_OBJECTS, MEDIAOBJ_TABLE_COLUMNS, COL_ID +
                " = " + dbID, null, null, null, null, null);
        cursor.moveToFirst();
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
        db.close();
        cursor.close();
        return obj;
    }

    public long addMediaObj(String pFilename, String pSource, String pMediaType){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FILENAME, pFilename);
        values.put(COL_SOURCE, pSource);
        values.put(COL_MEDIA_TYPE, pMediaType);
        long newRowId = db.insert(TABLE_MEDIA_OBJECTS, null, values);
        db.close();
        return newRowId;
    }

    public long addMediaObj(KwonMediaObject obj){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FILENAME, obj.getmFilename());
        values.put(COL_SOURCE, obj.getmSource());
        values.put(COL_MEDIA_TYPE, obj.getmMediaType());
        if(obj.getmInternalPath()!=null) values.put(COL_INTERNAL_PATH, obj.getmInternalPath());
        if(obj.getmSearchQuery()!=null) values.put(COL_SEARCH_QUERY, obj.getmSearchQuery());
        if(obj.getmArtist()!=null) values.put(COL_ARTIST, obj.getmArtist());
        if(obj.getmSongName()!=null) values.put(COL_SONG_NAME, obj.getmSongName());
        if(obj.getmDuration()>0) values.put(COL_DURATION, obj.getmDuration());
        if(obj.getmFileDescription()!=null) values.put(COL_FILE_DESCRIPTION, obj.getmFileDescription());
        if(obj.getmSubTitles()!=null) values.put(COL_SUBTITLES, obj.getmSubTitles());
        long newRowId = db.insert(TABLE_MEDIA_OBJECTS,null,values);
        db.close();
        return newRowId;
    }

    public long updateMediaObj(int colID,KwonMediaObject obj){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FILENAME, obj.getmFilename());
        values.put(COL_SOURCE, obj.getmSource());
        values.put(COL_MEDIA_TYPE, obj.getmMediaType());
        if(obj.getmInternalPath()!=null) values.put(COL_INTERNAL_PATH, obj.getmInternalPath());
        if(obj.getmSearchQuery()!=null) values.put(COL_SEARCH_QUERY, obj.getmSearchQuery());
        if(obj.getmArtist()!=null) values.put(COL_ARTIST, obj.getmArtist());
        if(obj.getmSongName()!=null) values.put(COL_SONG_NAME, obj.getmSongName());
        if(obj.getmDuration()>0) values.put(COL_DURATION, obj.getmDuration());
        if(obj.getmFileDescription()!=null) values.put(COL_FILE_DESCRIPTION, obj.getmFileDescription());
        if(obj.getmSubTitles()!=null) values.put(COL_SUBTITLES, obj.getmSubTitles());
        int rowsUpdated = db.update(TABLE_MEDIA_OBJECTS,values,COL_ID + " = " + String.valueOf(colID),null);
        db.close();
        return rowsUpdated;
    }

    public int deleteByTableID(int id){
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MEDIA_OBJECTS,COL_ID + " = ?",new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted;
    }

    public int deleteByPath(String path){
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MEDIA_OBJECTS,COL_INTERNAL_PATH + " = ?",new String[]{path});
        db.close();
        return rowsDeleted;
    }

    public int deleteByFilename(String filename){
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MEDIA_OBJECTS,COL_FILENAME + " = ?",new String[]{filename});
        db.close();
        return rowsDeleted;
    }

    public int deleteBySearchQuery(String query){
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MEDIA_OBJECTS,COL_SEARCH_QUERY + " = ?",new String[]{query});
        db.close();
        return rowsDeleted;
    }

    public int deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MEDIA_OBJECTS,"1",null);
        db.close();
        return rowsDeleted;
    }

    public Cursor searchByFilename(String filename){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDIA_OBJECTS, MEDIAOBJ_TABLE_COLUMNS, COL_FILENAME +
                " LIKE '%" + filename + "%'", null, null, null, null, null);
        db.close();
        return cursor;
    }

    public Cursor searchByPath(String path){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDIA_OBJECTS, MEDIAOBJ_TABLE_COLUMNS, COL_INTERNAL_PATH +
                " LIKE '%" + path + "%'", null, null, null, null, null);
        db.close();
        return cursor;
    }

    public Cursor searchByQuery(String query){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDIA_OBJECTS, MEDIAOBJ_TABLE_COLUMNS, COL_SEARCH_QUERY +
                " LIKE '%" + query + "%'", null, null, null, null, null);
        db.close();
        return cursor;
    }

    public Cursor userSearchByFilenameArtistSong(String userSearch){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDIA_OBJECTS, MEDIAOBJ_TABLE_COLUMNS, COL_FILENAME + " LIKE '%" +
                userSearch + "%' OR " + COL_ARTIST + " LIKE '%" + userSearch + "%' OR " + COL_SONG_NAME  + " LIKE '%" +
                userSearch + "%'",null,null,null,null,null);
        db.close();
        return cursor;
    }
}