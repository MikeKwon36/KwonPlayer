package kwondeveloper.com.kwonplayer.Playlists;

import java.util.ArrayList;

import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;

public class Playlist {
    private String mPlaylistTitle;
    private ArrayList<KwonMediaObject> mSongsInPlaylist;
    private static int mPlaylistCount;

    public Playlist() {
        mSongsInPlaylist = new ArrayList<>();
        if(mPlaylistCount==99){
            mPlaylistCount=0;
        }
        mPlaylistCount++;
        mPlaylistTitle = "New Playlist " + mPlaylistCount;
    }

    public KwonMediaObject getSongFromPlaylist (int index) {
        return mSongsInPlaylist.get(index);
    }

    public ArrayList<KwonMediaObject> getAllSongsInPlaylist() {
        return mSongsInPlaylist;
    }

    public void addSongToPlaylist(KwonMediaObject object){
        mSongsInPlaylist.add(object);
    }

    public void addPlaylistToPlaylist(ArrayList<KwonMediaObject> playlist){
        mSongsInPlaylist.addAll(playlist);
    }

    public void setSongInPlaylist(int index, KwonMediaObject object){
        mSongsInPlaylist.set(index,object);
    }
    public void removeSongFromPlaylist(KwonMediaObject object){
        mSongsInPlaylist.remove(object);
    }

    public void removeSongFromPlaylist(int index){
        mSongsInPlaylist.remove(index);
    }

    public void clearPlaylist (){
        mSongsInPlaylist.clear();
    }

    public String getPlaylistTitle() {
        return mPlaylistTitle;
    }

    public void setPlaylistTitle(String title) {
        this.mPlaylistTitle = title;
    }

    public void replaceSongsInPlaylist(ArrayList<KwonMediaObject> songs) {
        mSongsInPlaylist.clear();
        mSongsInPlaylist.addAll(songs);
    }

    public static int getPlaylistCount() {
        return mPlaylistCount;
    }

    public static void setPlaylistCount(int mPlaylistCount) {
        Playlist.mPlaylistCount = mPlaylistCount;
    }

    @Override
    public String toString() {
        return mPlaylistTitle;
    }
}
