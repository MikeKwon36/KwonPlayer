package kwondeveloper.com.kwonplayer.Playlists;

import java.util.ArrayList;
import java.util.Random;

import kwondeveloper.com.kwonplayer.Fragments.PlaylistFragment;
import kwondeveloper.com.kwonplayer.Fragments.PlaylistRecyclerAdapter;
import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;

public class CurrentPlaylist {
    private static Playlist mCurrentPlaylist;
    private static CurrentPlaylist mInstance;
    private static ArrayList<KwonMediaObject> mSongsPlayedInCurrentTracker;

    private CurrentPlaylist(){
        mCurrentPlaylist = new Playlist();
    }

    public static CurrentPlaylist getInstance(){
        if(mInstance==null){
            mInstance = new CurrentPlaylist();
            mSongsPlayedInCurrentTracker = new ArrayList<>();
        }
        return mInstance;
    }

    public static void replacePlaylist(ArrayList<KwonMediaObject> objs, String newTitle){
        mCurrentPlaylist.getAllSongsInPlaylist().clear();
        mCurrentPlaylist.addPlaylistToPlaylist(objs);
        mCurrentPlaylist.setPlaylistTitle(newTitle);
        mSongsPlayedInCurrentTracker.clear();
    }

    public static void replacePlaylist(Playlist playlist){
        mCurrentPlaylist.getAllSongsInPlaylist().clear();
        mCurrentPlaylist.addPlaylistToPlaylist(playlist.getAllSongsInPlaylist());
        mCurrentPlaylist.setPlaylistTitle(playlist.getPlaylistTitle().toUpperCase());
        mSongsPlayedInCurrentTracker.clear();
    }

    public static Playlist getPlaylist(){
        return mCurrentPlaylist;
    }

    public static void clearPlaylist(){
        mCurrentPlaylist.clearPlaylist();
        mSongsPlayedInCurrentTracker.clear();
    }

    public static void replaceMediaObject(int index, KwonMediaObject obj) {
        mCurrentPlaylist.setSongInPlaylist(index, obj);
    }

    public static void addMediaObject(KwonMediaObject obj) {
        mCurrentPlaylist.addSongToPlaylist(obj);
    }

    public static void removeMediaObject(int index){
        for (KwonMediaObject song:mSongsPlayedInCurrentTracker) {
            if(song.getmID()==mCurrentPlaylist.getSongFromPlaylist(index).getmID()){
                mSongsPlayedInCurrentTracker.remove(song);
            }
        }
        mCurrentPlaylist.removeSongFromPlaylist(index);
    }

    public static void removeMediaObject(KwonMediaObject obj){
        for (KwonMediaObject song:mSongsPlayedInCurrentTracker) {
            if(song.getmID()==obj.getmID()){
                mSongsPlayedInCurrentTracker.remove(song);
            }
        }
        mCurrentPlaylist.removeSongFromPlaylist(obj);
    }

    public static void flagSongAsPlayedInSongsPlayedTracker(KwonMediaObject obj){
        mSongsPlayedInCurrentTracker.add(obj);
    }

    public static void clearSongsPlayedTracker(){
        mSongsPlayedInCurrentTracker.clear();
    }

    public static ArrayList<KwonMediaObject> getSongsPlayedTracker(){
        return mSongsPlayedInCurrentTracker;
    }

    public static KwonMediaObject getMediaObject(int index){
        return mCurrentPlaylist.getSongFromPlaylist(index);
    }

    public static void setPlaylistTitle(String newTitle){
        mCurrentPlaylist.setPlaylistTitle(newTitle);
    }

    public static String getPlaylistTitle(){
        return mCurrentPlaylist.getPlaylistTitle();
    }
}
