package kwondeveloper.com.kwonplayer.MediaObjects;

import android.net.Uri;

public class KwonMediaObject {

    //Member Variables =====================================================================

    //The album the audio file is from, if any
    private String mAlbum;

    //The id of the album the audio file is from, if any
    private long mAlbumID;

    //A non human readable key calculated from the ALBUM, used for searching, sorting and grouping
    private String mAlbumKey;

    //The artist who created the audio file, if any
    private String mArtist;

    //The id of the artist who created the audio file, if any
    private long mArtistID;

    //A non human readable key calculated from the ARTIST, used for searching, sorting and grouping
    private String mArtistKey;

    //The position, in ms, playback was at when playback for this file was last stopped
    private long mBookmark;

    //The composer of the audio file, if any
    private String mComposer;

    //The data stream for the file
    private String mData;

    //The display name of the file
    private String mDisplayName;

    //The duration of the audio file, in ms
    private long mDuration;

    //The unique ID for a row
    private long mID;

    //Non-zero if the audio file is music
    private int mIsMusic;

    //Non-zero if the audio file is a podcast
    private int mIsPodcast;

    //The MIME type of the file
    private String mMIMEType;

    //The title of the content
    private String mTitle;

    //A non human readable key calculated from the TITLE, used for searching, sorting and grouping
    private String mTitleKey;

    //The track number of this song on the album, if any
    private int mTrack;

    //album art URI
    private Uri mAlbumArt;

    //Media Type
    private String mMediaType;
    public enum MediaType {AUDIO,VIDEO,IMAGE};

    //Default Constructor =====================================================================
    public KwonMediaObject() {
    }

    //toString Override =======================================================================

    @Override
    public String toString() {
        return mTitle;
    }

    //Getters & Setters =======================================================================
    public String getmAlbumKey() {
        return mAlbumKey;
    }

    public void setmAlbumKey(String mAlbumKey) {
        this.mAlbumKey = mAlbumKey;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public long getmAlbumID() {
        return mAlbumID;
    }

    public void setmAlbumID(long mAlbumID) {
        this.mAlbumID = mAlbumID;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public long getmArtistID() {
        return mArtistID;
    }

    public void setmArtistID(long mArtistID) {
        this.mArtistID = mArtistID;
    }

    public String getmArtistKey() {
        return mArtistKey;
    }

    public void setmArtistKey(String mArtistKey) {
        this.mArtistKey = mArtistKey;
    }

    public long getmBookmark() {
        return mBookmark;
    }

    public void setmBookmark(long mBookmark) {
        this.mBookmark = mBookmark;
    }

    public String getmComposer() {
        return mComposer;
    }

    public void setmComposer(String mComposer) {
        this.mComposer = mComposer;
    }

    public String getmData() {
        return mData;
    }

    public void setmData(String mData) {
        this.mData = mData;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public long getmDuration() {
        return mDuration;
    }

    public void setmDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public long getmID() {
        return mID;
    }

    public void setmID(long mID) {
        this.mID = mID;
    }

    public int getmIsMusic() {
        return mIsMusic;
    }

    public void setmIsMusic(int mIsMusic) {
        this.mIsMusic = mIsMusic;
    }

    public int getmIsPodcast() {
        return mIsPodcast;
    }

    public void setmIsPodcast(int mIsPodcast) {
        this.mIsPodcast = mIsPodcast;
    }

    public String getmMIMEType() {
        return mMIMEType;
    }

    public void setmMIMEType(String mMIMEType) {
        this.mMIMEType = mMIMEType;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmTitleKey() {
        return mTitleKey;
    }

    public void setmTitleKey(String mTitleKey) {
        this.mTitleKey = mTitleKey;
    }

    public int getmTrack() {
        return mTrack;
    }

    public void setmTrack(int mTrack) {
        this.mTrack = mTrack;
    }

    public Uri getmAlbumArt() {
        return mAlbumArt;
    }

    public void setmAlbumArt(Uri mAlbumArt) {
        this.mAlbumArt = mAlbumArt;
    }

    public String getmMediaType() {
        return mMediaType;
    }

    public void setmMediaType(String mMediaType) {
        this.mMediaType = mMediaType;
    }
}
