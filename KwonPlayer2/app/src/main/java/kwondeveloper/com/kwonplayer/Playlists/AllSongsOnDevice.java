package kwondeveloper.com.kwonplayer.Playlists;

import java.util.ArrayList;
import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;

public class AllSongsOnDevice {
    private static ArrayList<KwonMediaObject> mCurrentPlaylist;
    private static AllSongsOnDevice mInstance;

    private AllSongsOnDevice(){
        mCurrentPlaylist = new ArrayList<KwonMediaObject>();
    }

    public static AllSongsOnDevice getInstance(){
        if(mInstance==null){
            mInstance = new AllSongsOnDevice();
        }
        return mInstance;
    }

    public static void getMediaObject(int index){
        mCurrentPlaylist.get(index);
    }

    public static void replaceAllSongs(ArrayList<KwonMediaObject> objs){
        mCurrentPlaylist.clear();
        mCurrentPlaylist.addAll(objs);
    }

    public static ArrayList<KwonMediaObject> getAllSongs(){
        return mCurrentPlaylist;
    }

    public static void clearAllSongs(){
        mCurrentPlaylist.clear();
    }

    public static void replaceMediaObject(int index, KwonMediaObject obj){
        mCurrentPlaylist.set(index,obj);
    }

    public static void addMediaObject(KwonMediaObject obj){
        mCurrentPlaylist.add(obj);
    }

    public static void addMediaObjectCollection(ArrayList<KwonMediaObject> objs){
        mCurrentPlaylist.addAll(objs);
    }

    public static void removeMediaObject(int index){
        mCurrentPlaylist.remove(index);
    }

    public static void removeMediaObject(KwonMediaObject obj){
        mCurrentPlaylist.remove(obj);
    }
}
