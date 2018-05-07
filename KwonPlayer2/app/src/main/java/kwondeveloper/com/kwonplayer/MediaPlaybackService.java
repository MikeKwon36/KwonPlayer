package kwondeveloper.com.kwonplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import kwondeveloper.com.kwonplayer.Fragments.PlayerFragment;
import kwondeveloper.com.kwonplayer.Fragments.PlaylistFragment;
import kwondeveloper.com.kwonplayer.Fragments.SongLibraryRecyclerAdapter;
import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;
import kwondeveloper.com.kwonplayer.Playlists.AllSongsOnDevice;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.SupportClasses.EqualizerHelper;

public class MediaPlaybackService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private static WifiManager.WifiLock mWifiLock;
    private static AudioManager mAudioManager;
    private static Notification mNotification;
    private static NotificationManager mManager;
    private static NotificationCompat.Builder mBuilder;
    private static Boolean mDontTrackSongFlag;
    private static Context mServiceContext;
    private static EqualizerHelper mEqualizerHelper;
    private static Uri mSongToPlayUri;
    private static long mCurrentSongId;
    private static boolean mIsPaused;

    public static MediaPlayer mMediaPlayer;
    public static KwonMediaObject mCurrentSong;
    public static boolean mIsShuffleOn;
    public static String mRepeatState;

    //Global Constants
    public static final String REPEAT_MODE_ALL = "com.kwondeveloper.kwonplayer.all";
    public static final String REPEAT_MODE_SINGLE = "com.kwondeveloper.kwonplayer.single";
    public static final String REPEAT_MODE_OFF = "com.kwondeveloper.kwonplayer.off";
    public static final String SELECTED_SONG_ID = "com.kwondeveloper.kwonplayer.song_id";
    public static final String STOP = "com.kwondeveloper.kwonplayer.stop";
    public static final String PREVIOUS = "com.kwondeveloper.kwonplayer.previous";
    public static final String SKIP = "com.kwondeveloper.kwonplayer.skip";
    public static final String PLAY = "com.kwondeveloper.kwonplayer.play";
    public static final String PAUSE = "com.kwondeveloper.kwonplayer.pause";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        super.onCreate();

        //Instantiate member variables
        Log.d("***ServiceOnCreate", "instantiates member & global variables");
        mServiceContext = getApplicationContext();
        mDontTrackSongFlag = false;
        mRepeatState = REPEAT_MODE_OFF;
        mIsShuffleOn = false;
        mIsPaused = false;
        mCurrentSongId = 0;
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(MediaPlaybackService.this);
        mWifiLock = ((WifiManager)getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "lock");
        if(mMediaPlayer == null){mMediaPlayer = new MediaPlayer();}
        initAudioFX();
        Log.d("***ServiceOnCreate", "mMediaPlayer.isPlaying() = " + mMediaPlayer.isPlaying());

        mMediaPlayer.setWakeMode(mServiceContext, PowerManager.PARTIAL_WAKE_LOCK);

        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Toast.makeText(MediaPlaybackService.this, R.string.audio_focus_error, Toast.LENGTH_SHORT).show();
            stopSelf();
        }

        //If mMediaPlayer enters error state, immediately reset lifecycle state
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mMediaPlayer.reset();
                return true;
            }
        });

        //Checks for repeat/shuffle/playlistSize to determine what to do once a song finishes
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            long nextSongId = 0;
            int nextSongIndex = 0;
            Intent completionIntent = new Intent(MediaPlaybackService.this,MediaPlaybackService.class);

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                //If repeatSingle is on
                if(mRepeatState.equals(REPEAT_MODE_SINGLE)){
                    Log.d("***ServiceOnComplete", "Single Repeat is on = " + mRepeatState);
                    CurrentPlaylist.clearSongsPlayedTracker();
                    completionIntent.putExtra(SELECTED_SONG_ID, mCurrentSongId);
                    completionIntent.setAction(PLAY);
                    startService(completionIntent);
                    Log.d("***ServiceOnComplete", "CurrentSongIndex = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().indexOf(mCurrentSong));
                    Log.d("***ServiceOnComplete", "NextSongIndex = " + nextSongIndex);
                    Log.d("***ServiceOnComplete", "CurrentSongId = " + mCurrentSongId);
                    Log.d("***ServiceOnComplete", "NextSongId = " + nextSongId);
                } else {

                    //If repeatSingle is NOT on, confirm if shuffle is off
                    if(!mIsShuffleOn){
                        Log.d("***ServiceOnComplete", "shuffle is off = " + mIsShuffleOn);

                        //If shuffle is off, the next song to play is the next index in the current playlist
                        int nextSongIndex = CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().indexOf(mCurrentSong) + 1;

                        //If we're not at the end of the playlist, play next song in list
                        if(nextSongIndex <= CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size()-1){
                            nextSongId = CurrentPlaylist.getPlaylist().getSongFromPlaylist(nextSongIndex).getmID();
                            completionIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                            completionIntent.setAction(PLAY);
                            startService(completionIntent);
                            Log.d("***ServiceOnComplete", "CurrentSongIndex = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().indexOf(mCurrentSong));
                            Log.d("***ServiceOnComplete", "NextSongIndex = " + nextSongIndex);
                            Log.d("***ServiceOnComplete", "CurrentSongId = " + mCurrentSongId);
                            Log.d("***ServiceOnComplete", "NextSongId = " + nextSongId);
                        } else {
                            //If repeatAll is on and we're at the end of the playlist, play first song in playlist
                            if(mRepeatState.equals(REPEAT_MODE_ALL)){
                                nextSongId = CurrentPlaylist.getPlaylist().getSongFromPlaylist(0).getmID();
                                Log.d("***ServiceOnComplete", "at end of Playlist and repeat all is on = " + mRepeatState);
                                Log.d("***ServiceOnComplete", "next Song Id is first song in playlist = " + nextSongId);
                                completionIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                                completionIntent.setAction(PLAY);
                                startService(completionIntent);
                                Log.d("***ServiceOnComplete", "CurrentSongIndex = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().indexOf(mCurrentSong));
                                Log.d("***ServiceOnComplete", "NextSongIndex = " + nextSongIndex);
                                Log.d("***ServiceOnComplete", "CurrentSongId = " + mCurrentSongId);
                                Log.d("***ServiceOnComplete", "NextSongId = " + nextSongId);
                            } else {
                                //Stop MediaPlayer if we're at end of playlist and repeat is not on
                                Log.d("***ServiceOnComplete", "MediaPlayer OnComplete - at end of Playlist and repeat is off = " + mRepeatState);
                                completionIntent.putExtra(SELECTED_SONG_ID, mCurrentSongId);
                                completionIntent.setAction(STOP);
                                startService(completionIntent);
                                Log.d("***ServiceOnComplete", "playback stopped and current Song Id set to = " + mCurrentSongId);
                            }
                        }
                    } else {
                        //If shuffle is on, next song index is randomly generated
                        Log.d("***ServiceOnComplete", "shuffle is on = " + mIsShuffleOn);
                        Log.d("***ServiceOnComplete", "Playlist Size = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                        Log.d("***ServiceOnComplete", "Songs Played Tracker Size = " + CurrentPlaylist.getSongsPlayedTracker().size());
                        Random random = new Random();

                        //If we're not at the end of the playlist, ensure next song selected wasn't already
                        // played by comparing against list of songs already played (stored in playedSongTracker)
                        if(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size() > CurrentPlaylist.getSongsPlayedTracker().size()){
                            Log.d("***ServiceOnComplete", "Current Playlist size: " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size() + " > songsPlayedTracker size: " + CurrentPlaylist.getSongsPlayedTracker().size());
                            ArrayList<Integer> indexesUsed = new ArrayList<>();

                            //In the event that a playlist has multiple events of the same song,
                            // the cycles counter ensures the shuffle doesn't enter an infinite loop when checking for duplicates
                            int cycles = 0;
                            boolean isDuplicate = true;
                            while(isDuplicate){
                                cycles++;
                                if(cycles == CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size()){CurrentPlaylist.getSongsPlayedTracker().clear();}
                                nextSongIndex = random.nextInt(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                                while(indexesUsed.contains(nextSongIndex)){
                                    nextSongIndex = random.nextInt(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                                }
                                indexesUsed.add(nextSongIndex);
                                nextSongId = CurrentPlaylist.getPlaylist().getSongFromPlaylist(nextSongIndex).getmID();
                                isDuplicate = false;
                                for (KwonMediaObject song:CurrentPlaylist.getSongsPlayedTracker()) {
                                    if(song.getmID()==nextSongId){isDuplicate = true;
                                    }
                                }
                            }
                            completionIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                            completionIntent.setAction(PLAY);
                            startService(completionIntent);
                            Log.d("***ServiceOnComplete", "CurrentSongIndex = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().indexOf(mCurrentSong));
                            Log.d("***ServiceOnComplete", "NextSongIndex = " + nextSongIndex);
                            Log.d("***ServiceOnComplete", "CurrentSongId = " + mCurrentSongId);
                            Log.d("***ServiceOnComplete", "NextSongId = " + nextSongId);
                        } else {
                            //If repeatAll is on and we're at the end of the playlist, clear tracker and restart shuffle
                            if(mRepeatState.equals(REPEAT_MODE_ALL)){
                                Log.d("***ServiceOnComplete", "end of list reached and repeat all is on = " + mRepeatState);
                                CurrentPlaylist.clearSongsPlayedTracker();
                                nextSongIndex = random.nextInt(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                                nextSongId = CurrentPlaylist.getPlaylist().getSongFromPlaylist(nextSongIndex).getmID();
                                completionIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                                completionIntent.setAction(PLAY);
                                startService(completionIntent);
                                Log.d("***ServiceOnComplete", "MediaPlayer OnComplete - CurrentSongIndex = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().indexOf(mCurrentSong));
                                Log.d("***ServiceOnComplete", "MediaPlayer OnComplete - NextSongIndex = " + nextSongIndex);
                                Log.d("***ServiceOnComplete", "MediaPlayer OnComplete - CurrentSongId = " + mCurrentSongId);
                                Log.d("***ServiceOnComplete", "MediaPlayer OnComplete - NextSongId = " + nextSongId);
                            } else {
                                //Stop MediaPlayer if we're at end of playlist with no repeat state
                                Log.d("***ServiceOnComplete", "end of Playlist reached and repeat is off = " + mRepeatState);
                                completionIntent.putExtra(SELECTED_SONG_ID, mCurrentSongId);
                                completionIntent.setAction(STOP);
                                startService(completionIntent);
                                Log.d("***ServiceOnComplete", "playback stopped and current song id set to = " + mCurrentSongId);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            Log.d("***ServiceOnStart", "new MediaPlayer object instantiated");
        }

        if(intent != null) {
            //Confirm what song ID needs to be loaded into mediaPlayer object
            Log.d("***ServiceOnStart", "service starting with ID = " + mCurrentSongId);

            if (intent.hasExtra(SELECTED_SONG_ID)) {
                //if a new song is being loaded to the media player, so pause state is reset to allow PLAY to load a new song
                if(mCurrentSongId != intent.getLongExtra(SELECTED_SONG_ID,-1)){
                    mIsPaused = false;
                }
                mCurrentSongId = intent.getLongExtra(SELECTED_SONG_ID, -1);
                Log.d("***ServiceOnStart", "intent had an ID & set current song ID to = " + mCurrentSongId);
            } else if (mCurrentSong != null) {
                //if no song ID was provided in the intent, but the mCurrentSong object is populated,
                // then the songID to stream is pulled from the object (ex: pressing play on a paused song)
                mCurrentSongId = mCurrentSong.getmID();
                Log.d("***ServiceOnStart", "intent did not have an ID, so service using current song ID = " + mCurrentSongId);
            } else {
                //To account for pressing play on an empty playlist, which defaults playlist to device's full library
                if(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size()==0){
                    CurrentPlaylist.replacePlaylist(AllSongsOnDevice.getAllSongs(),getResources().getString(R.string.PlaylistFragment_Playlist_DefaultTitle));
                    PlaylistFragment.mPlaylistAdapter.notifyDataSetChanged();
                }
                //Defaults to first song, if pressing play in PlayerFragment before any song/playlist is
                // selected when app launched OR when skipping songs
                if(AllSongsOnDevice.getAllSongs().size()>0) {
                    mCurrentSongId = CurrentPlaylist.getPlaylist().getSongFromPlaylist(0).getmID();
                    Log.d("***ServiceOnStart", "intent did not have an ID and no song stored in queue, so will use first song in device library = " + mCurrentSongId);
                } else {
                    intent.setAction(STOP);
                    Log.d("***ServiceOnStart", "No songs were on device, so Service shut down to prevent null exceptions");
                }
            }

            //Filter appropriate action behavior from intent
            if(intent.getAction()!=null){
                //initialize intent & song identifiers used in SKIP/PREVIOUS functions
                Intent skipIntent = new Intent(MediaPlaybackService.this,MediaPlaybackService.class);
                long nextSongId = 0;
                int nextSongIndex = 0;

                switch (intent.getAction()){
                    case PLAY:
                        //ensures multiple wifilocks are not enabled after PLAY intent is addressed
                        if(mWifiLock.isHeld()){mWifiLock.release();}
                        Log.d("***ServiceIntentPLAY", "intent.getAction(" + intent.getAction() + ")");

                        if(!mIsPaused){
                            //If no song is currently loaded (ex: when app boots) OR if a song is currently playing
                            // and a new song is selected, the MediaPlayer is reset before a song is loaded (to prevent overlapping audio)
                            Log.d("***ServiceIntentPLAY", "mMediaPlayer.isPlaying() is " + mMediaPlayer.isPlaying() + " so mediaPlayer reset and new song loaded");
                            Log.d("***ServiceIntentPLAY", "Song ID used in mediaPlayer = " + mCurrentSongId);
                            mMediaPlayer.reset();
                            playSingleSong(mCurrentSongId);
                        } else {
                            //if a PLAY button is pressed on a song that's paused, the audio is resumed and the song won't be tracked again
                            Log.d("***ServiceIntentPLAY", "song's mIsPaused state was = " + mIsPaused + " so .start() called on mediaPlayer");
                            Log.d("***ServiceIntentPLAY", "Song ID used in mediaPlayer = " + mCurrentSongId);
                            mDontTrackSongFlag = true;
                            mMediaPlayer.start();
                        }
                        //Paused state set to false, playerfragment button state updated to reflect song is playing,
                        // wifilock enabled, the notification control panel is built/updated, and the songTrackerList is Enabled
                        mIsPaused = false;
                        PlayerFragment.setPlayPauseButton();
                        mWifiLock.acquire();
                        buildNotification(mCurrentSongId);
                        mDontTrackSongFlag = false;
                        Log.d("***ServiceIntentPLAY", "mDontTrackSongFlag is set to = " + mDontTrackSongFlag);
                        Log.d("***ServiceIntentPLAY", "mIsPaused set to " + mIsPaused);
                        Log.d("***ServiceIntentPLAY", "Songs played tracker size = " + CurrentPlaylist.getSongsPlayedTracker().size());
                        break;
                    case STOP:
                        Log.d("***ServiceIntentSTOP", "intent.getAction(" + intent.getAction() + ")");
                        mIsPaused = false;
                        mMediaPlayer.reset();
                        if(mWifiLock.isHeld()){mWifiLock.release();}
                        Log.d("***ServiceIntentSTOP", "mIsPaused set to " + mIsPaused);
                        Log.d("***ServiceIntentSTOP", "mMediaPlayer.isPlaying() = " + mMediaPlayer.isPlaying());
                        Log.d("***ServiceIntentSTOP", "CurrentSongID = " + mCurrentSongId);
                        stopSelf();
                        break;
                    case PAUSE:
                        Log.d("***ServiceIntentPAUSE", "intent.getAction(" + intent.getAction() + ")");
                        Log.d("***ServiceIntentPAUSE", "mIsPaused = " + mIsPaused);
                        mIsPaused = true;
                        mMediaPlayer.pause();
                        updateNotification(mCurrentSongId);
                        PlayerFragment.setPlayPauseButton();
                        if(mWifiLock.isHeld()){mWifiLock.release();}
                        Log.d("***ServiceIntentPAUSE", "mIsPaused set to " + mIsPaused);
                        Log.d("***ServiceIntentPAUSE", "mMediaPlayer.isPlaying() = " + mMediaPlayer.isPlaying());
                        Log.d("***ServiceIntentPAUSE", "CurrentSongID = " + mCurrentSongId);
                        break;
                    case PREVIOUS:
                        Log.d("***ServiceIntentPREV", "intent.getAction(" + intent.getAction() + ")");
                        Log.d("***ServiceIntentPREV", "Songs played tracker size = " + CurrentPlaylist.getSongsPlayedTracker().size());
                        mIsPaused = false;
                        mMediaPlayer.reset();
                        mDontTrackSongFlag = true;
                        Log.d("***ServiceIntentPREV", "mIsPaused set to " + mIsPaused);
                        Log.d("***ServiceIntentPREV", "mMediaPlayer reset to idle state");
                        Log.d("***ServiceIntentPREV", "mDontTrackSongFlag is set to = " + mDontTrackSongFlag);

                        //If no songs have been played prior to clicking previous, a random song is selected
                        if (CurrentPlaylist.getSongsPlayedTracker().size() == 0){
                            Log.d("***ServiceIntentPREV", "no prior songs, so random song selected = " + mRepeatState);
                            Random random = new Random();
                            nextSongIndex = random.nextInt(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                            KwonMediaObject newSong = CurrentPlaylist.getPlaylist().getSongFromPlaylist(nextSongIndex);
                            nextSongId = newSong.getmID();
                            skipIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                            skipIntent.setAction(PLAY);
                            startService(skipIntent);
                            Log.d("***ServiceIntentPREV", "songId to play = " + nextSongId);
                            break;
                        } else {
                            //Select index that's prior to the index of the currently playing song
                            nextSongIndex = CurrentPlaylist.getSongsPlayedTracker().size() - 2;
                            Log.d("***ServiceIntentPREV", "next song id is based on current song's index minus 1 = " + nextSongIndex);

                            //If we're still ahead of the beginning of the tracker, play identified song in songsPlayedTracker
                            if(nextSongIndex >= 0){
                                Log.d("***ServiceIntentPREV", "not at zero index of tracker");
                                nextSongId = CurrentPlaylist.getSongsPlayedTracker().get(nextSongIndex).getmID();
                                CurrentPlaylist.getSongsPlayedTracker().remove(CurrentPlaylist.getSongsPlayedTracker().size()-1);
                                skipIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                                skipIntent.setAction(PLAY);
                                startService(skipIntent);
                                Log.d("***ServiceIntentPREV", "previous song id = " + mCurrentSongId);
                                Log.d("***ServiceIntentPREV", "song id sent to play = " + nextSongId);
                                break;
                            } else {
                                //If the zero index of the tracker is reached, a random song is played
                                Log.d("***ServiceIntentPREV", "reached zero index of tracker, so random song selected");
                                Random random = new Random();
                                nextSongIndex = random.nextInt(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                                KwonMediaObject newSong = CurrentPlaylist.getPlaylist().getSongFromPlaylist(nextSongIndex);
                                nextSongId = newSong.getmID();
                                skipIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                                skipIntent.setAction(PLAY);
                                startService(skipIntent);
                                Log.d("***ServiceIntentPREV", "MediaPlayer prev - songId = " + mCurrentSongId);
                                Log.d("***ServiceIntentPREV", "MediaPlayer prev - NextSongId = " + nextSongId);
                                break;
                            }
                        }
                    case SKIP:
                        Log.d("***ServiceIntentSKIP", "intent.getAction(" + intent.getAction() + ")");
                        mIsPaused = false;
                        mMediaPlayer.reset();
                        mDontTrackSongFlag = false;
                        Log.d("***ServiceIntentSKIP", "mIsPaused set to " + mIsPaused);
                        Log.d("***ServiceIntentSKIP", "mMediaPlayer reset to idle state");

                        //If shuffle is off
                        if(!mIsShuffleOn){
                            Log.d("***ServiceIntentSKIP", "shuffle is off = " + mIsShuffleOn);
                            //ID next song in list for playback
                            nextSongIndex = CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().indexOf(mCurrentSong) + 1;
                            Log.d("***ServiceIntentSKIP", "next song index set to " + nextSongIndex + "vs. Playlist's size =" + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                            //If we're not at the end of the playlist, play next song in list
                            if(nextSongIndex <= CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size()-1){
                                nextSongId = CurrentPlaylist.getPlaylist().getSongFromPlaylist(nextSongIndex).getmID();
                                skipIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                                skipIntent.setAction(PLAY);
                                startService(skipIntent);
                                Log.d("***ServiceIntentSKIP", "current song's id = " + mCurrentSongId);
                                Log.d("***ServiceIntentSKIP", "next song's id sent to play = " + nextSongId);
                                break;
                            } else {
                                //If we're at the end of the playlist, play first song in playlist
                                nextSongId = CurrentPlaylist.getPlaylist().getSongFromPlaylist(0).getmID();
                                Log.d("***ServiceIntentSKIP", "end of playlist reached");
                                Log.d("***ServiceIntentSKIP", "next Song's Id set to first song in playlist = " + nextSongId);
                                skipIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                                skipIntent.setAction(PLAY);
                                startService(skipIntent);
                                Log.d("***ServiceIntentSKIP", "current song's id = " + mCurrentSongId);
                                Log.d("***ServiceIntentSKIP", "next song's id sent to play = " + nextSongId);
                                break;
                            }
                        } else {
                            //If shuffle is on, id next song by comparing playlist against
                            // playedSongTracker and confirm song hasn't already played
                            Log.d("***ServiceIntentSKIP", "Shuffle is On = " + mIsShuffleOn);
                            Log.d("***ServiceIntentSKIP", "Playlist Size = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                            Log.d("***ServiceIntentSKIP", "Songs Played Tracker Size = " + CurrentPlaylist.getSongsPlayedTracker().size());
                            Random random = new Random();

                            ArrayList<Integer> indexesUsed = new ArrayList<>();

                            //In the event that a playlist has multiple events of the same song,
                            // the cycles counter ensures the shuffle doesn't enter an infinite loop when checking for duplicates
                            int cycles = 0;
                            boolean isDuplicate = true;
                            while(isDuplicate){
                                cycles++;
                                if(cycles == CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size()){
                                    CurrentPlaylist.getSongsPlayedTracker().clear();
                                }
                                nextSongIndex = random.nextInt(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                                while(indexesUsed.contains(nextSongIndex)){
                                    nextSongIndex = random.nextInt(CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
                                }
                                indexesUsed.add(nextSongIndex);
                                nextSongId = CurrentPlaylist.getPlaylist().getSongFromPlaylist(nextSongIndex).getmID();
                                isDuplicate = false;
                                for (KwonMediaObject song:CurrentPlaylist.getSongsPlayedTracker()) {
                                    if(song.getmID()==nextSongId){isDuplicate = true;}
                                }
                            }

                            //If we're not at the end of the playlist, play next song in list
                            if(CurrentPlaylist.getSongsPlayedTracker().size() < CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size()){
                                skipIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                                skipIntent.setAction(PLAY);
                                startService(skipIntent);
                                Log.d("***ServiceIntentSKIP", "current song's id = " + mCurrentSongId);
                                Log.d("***ServiceIntentSKIP", "id of song sent to play = " + nextSongId);
                                break;
                            } else {
                                CurrentPlaylist.clearSongsPlayedTracker();
                                //If we're at the end of the playlist, clear tracker and restart shuffle
                                Log.d("***ServiceIntentSKIP", "end of playlist reached");
                                skipIntent.putExtra(SELECTED_SONG_ID, nextSongId);
                                skipIntent.setAction(PLAY);
                                startService(skipIntent);
                                Log.d("***ServiceIntentSKIP", "current song's id = " + mCurrentSongId);
                                Log.d("***ServiceIntentSKIP", "id of next song sent to play = " + nextSongId);
                                break;
                            }
                        }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("***ServiceOnDestroy", "Service destroyed, mMediaPlayer set null");
        if(mWifiLock.isHeld()){
            mWifiLock.release();}
        mEqualizerHelper.releaseEQ();
        stopForeground(true);
        mCurrentSong = null;
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
        mAudioManager.abandonAudioFocus(this);
        PlayerFragment.setPlayPauseButton();
        PlayerFragment.setSeekbarDuration();
        PlayerFragment.setAlbumArtAndDetails(mCurrentSong);
        super.onDestroy();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Intent focusIntent = new Intent(this,MediaPlaybackService.class);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                Log.d("***ServiceOnFocusChange", "Focus returned, play intent sent with full volume");
                focusIntent.setAction(PLAY);
                startService(focusIntent);
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                Log.d("***ServiceOnFocusChange", "Focus lost, stop intent sent");
                focusIntent.setAction(STOP);
                startService(focusIntent);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we can pause playback since it's likely to resume
                Log.d("***ServiceOnFocusChange", "Focus temporarily lost, pause intent sent");
                focusIntent.setAction(PAUSE);
                startService(focusIntent);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing at an attenuated level
                Log.d("***ServiceOnFocusChange", "Focus temporarily lost, volume reduced");
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }

    private static void playSingleSong(long id){
        Log.d("***ServicePlaySong", "song id to load in media plaer = " + id);
        mSongToPlayUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        Log.d("***ServicePlaySong", "song uri sent to MediaPlayer = " + mSongToPlayUri);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setDataSource(mServiceContext, mSongToPlayUri);
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            Log.d("***ServicePlaySong", "Media is prepared and start was called");
                            Log.d("***ServicePlaySong", "mMediaPlayer.isPlaying() = " + mMediaPlayer.isPlaying());
                            PlayerFragment.setPlayPauseButton();
                            PlayerFragment.setSeekbarDuration();
                            PlayerFragment.setAlbumArtAndDetails(mCurrentSong);
                            Log.d("***ServicePlaySong", "set play/pause/seekbar/albumArt called on PlayerFragment");
                        }
                    });
                    mMediaPlayer.prepareAsync();
                    Log.d("***ServicePlaySong", "MediaPlayer is preparing");
                } catch (Throwable thr) {
                }
            }
        };
        new Thread(runnable).start();
        Log.d("***ServicePlaySong", "play song worker thread launched to process uri in MediaPlayer");
    }

    //Notification is launched when a new song plays, so it's built with the pause button displayed (to pause a playing song)
    private void buildNotification(long songId){
        Log.d("***ServiceBuildNotif", "song id received to build notification = " + songId);
        for (KwonMediaObject song: CurrentPlaylist.getPlaylist().getAllSongsInPlaylist()) {
            if(song.getmID()==songId){
                mCurrentSong = song;
                Log.d("***ServiceBuildNotif", "mCurrentSong = " + mCurrentSong.getmTitle());
            }
        }

        //Any new song is flagged in the songsPlayed tracker, as long as it wasn't launched by clicking the previous button
        Log.d("***ServiceBuildNotif", "mDontTrackSongFlag is currently set to = " + mDontTrackSongFlag);
        if(mDontTrackSongFlag){
            Log.d("***ServiceBuildNotif", "song wasn't added to tracker... tracker size = " + CurrentPlaylist.getSongsPlayedTracker().size());
        } else {
            CurrentPlaylist.flagSongAsPlayedInSongsPlayedTracker(mCurrentSong);
            Log.d("***ServiceBuildNotif", "song was added to tracker... tracker size = " + CurrentPlaylist.getSongsPlayedTracker().size());
        }

        //Once song is identified, Player Fragment UI is updated with the details
        PlayerFragment.setAlbumArtAndDetails(mCurrentSong);
        Log.d("***ServiceBuildNotif", "Player fragment's setAlbumArt method called");

        Intent launchPlayer = new Intent (MediaPlaybackService.this,MainActivity.class);
        launchPlayer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingLaunch = PendingIntent.getActivity(MediaPlaybackService.this, (int) System.currentTimeMillis(), launchPlayer, 0);

        Intent closePlayer = new Intent (MediaPlaybackService.this,MediaPlaybackService.class);
        closePlayer.setAction(STOP);
        PendingIntent pendingClose = PendingIntent.getService(MediaPlaybackService.this,(int)System.currentTimeMillis(),closePlayer,0);

        Intent pauseSong = new Intent (MediaPlaybackService.this,MediaPlaybackService.class);
        pauseSong.setAction(PAUSE);
        PendingIntent pendingPause = PendingIntent.getService(MediaPlaybackService.this,(int)System.currentTimeMillis(),pauseSong,0);

        Intent skipSong = new Intent (MediaPlaybackService.this,MediaPlaybackService.class);
        skipSong.setAction(SKIP);
        PendingIntent pendingSkip = PendingIntent.getService(MediaPlaybackService.this,(int)System.currentTimeMillis(),skipSong,0);

        Intent previousSong = new Intent (MediaPlaybackService.this,MediaPlaybackService.class);
        previousSong.setAction(PREVIOUS);
        PendingIntent pendingPrev = PendingIntent.getService(MediaPlaybackService.this,(int)System.currentTimeMillis(),previousSong,0);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_panel);
        contentView.setTextViewText(R.id.notificationArtist, mCurrentSong.getmArtist());
        contentView.setTextViewText(R.id.notificationTitle, mCurrentSong.getmTitle());

        contentView.setImageViewResource(R.id.notificationPrev, R.drawable.ic_skip_previous_white_24dp);
        contentView.setImageViewResource(R.id.notificationSkip, R.drawable.ic_skip_next_white_24dp);
        contentView.setImageViewResource(R.id.notificationClose, R.drawable.ic_close_white_24dp);
        contentView.setImageViewResource(R.id.notificationPausePlay, R.drawable.ic_pause_circle_outline_white_24dp);
        contentView.setOnClickPendingIntent(R.id.notificationPausePlay, pendingPause);
        contentView.setOnClickPendingIntent(R.id.notificationImage, pendingLaunch);
        contentView.setOnClickPendingIntent(R.id.notificationSkip, pendingSkip);
        contentView.setOnClickPendingIntent(R.id.notificationPrev, pendingPrev);
        contentView.setOnClickPendingIntent(R.id.notificationClose, pendingClose);

        mBuilder.setContent(contentView);
        mBuilder.setOngoing(true);
        mBuilder.setAutoCancel(false);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        mNotification = mBuilder.build();
        Picasso.with(this).load(mCurrentSong.getmAlbumArt())
                .placeholder(R.drawable.kwondeveloper)
                .into(contentView,R.id.notificationImage,1,mNotification);
        mManager.notify(1, mNotification);
        startForeground(1, mNotification);
    }

    //Every time the state of the MediaPlayer is paused or stopped, the notification panel is updated
    // to display the play button and will not track the song
    private void updateNotification(long songId) {
        Log.d("***ServiceUpdateNotif", "song id received to update notification = " + songId);
        mNotification = null;
        for (KwonMediaObject song: CurrentPlaylist.getPlaylist().getAllSongsInPlaylist()) {
            if(song.getmID()==songId){
                mCurrentSong = song;
                Log.d("***ServiceUpdateNotif", "mCurrentSong = " + mCurrentSong.getmTitle());
            }
        }

        PlayerFragment.setAlbumArtAndDetails(mCurrentSong);
        Log.d("***ServiceUpdateNotif", "Player fragment's setAlbumArt method called");

        Intent launchPlayer = new Intent (MediaPlaybackService.this,MainActivity.class);
        launchPlayer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingLaunch = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), launchPlayer, 0);

        Intent closePlayer = new Intent (MediaPlaybackService.this,MediaPlaybackService.class);
        closePlayer.setAction(STOP);
        PendingIntent pendingClose = PendingIntent.getService(MediaPlaybackService.this,(int)System.currentTimeMillis(),closePlayer,0);

        Intent pauseSong = new Intent (MediaPlaybackService.this,MediaPlaybackService.class);
        pauseSong.setAction(PAUSE);
        PendingIntent pendingPause = PendingIntent.getService(MediaPlaybackService.this,(int)System.currentTimeMillis(),pauseSong,0);

        Intent playSong = new Intent (MediaPlaybackService.this,MediaPlaybackService.class);
        playSong.setAction(PLAY);
        PendingIntent pendingPlay = PendingIntent.getService(MediaPlaybackService.this,(int)System.currentTimeMillis(),playSong,0);

        Intent skipSong = new Intent (MediaPlaybackService.this,MediaPlaybackService.class);
        skipSong.setAction(SKIP);
        PendingIntent pendingSkip = PendingIntent.getService(MediaPlaybackService.this,(int)System.currentTimeMillis(),skipSong,0);

        Intent previousSong = new Intent (MediaPlaybackService.this,MediaPlaybackService.class);
        previousSong.setAction(PREVIOUS);
        PendingIntent pendingPrev = PendingIntent.getService(MediaPlaybackService.this,(int)System.currentTimeMillis(),previousSong,0);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_panel);
        contentView.setTextViewText(R.id.notificationArtist, mCurrentSong.getmArtist());
        contentView.setTextViewText(R.id.notificationTitle, mCurrentSong.getmTitle());

        //check to determine whether to display the play or pause button
        if (mIsPaused) {
            contentView.setImageViewResource(R.id.notificationPausePlay, R.drawable.ic_play_circle_filled_white_24dp);
            contentView.setOnClickPendingIntent(R.id.notificationPausePlay, pendingPlay);
        } else {
            contentView.setImageViewResource(R.id.notificationPausePlay, R.drawable.ic_pause_circle_outline_white_24dp);
            contentView.setOnClickPendingIntent(R.id.notificationPausePlay, pendingPause);
        }

        contentView.setImageViewResource(R.id.notificationPrev, R.drawable.ic_skip_previous_white_24dp);
        contentView.setImageViewResource(R.id.notificationSkip, R.drawable.ic_skip_next_white_24dp);
        contentView.setImageViewResource(R.id.notificationClose, R.drawable.ic_close_white_24dp);
        contentView.setOnClickPendingIntent(R.id.image, pendingLaunch);
        contentView.setOnClickPendingIntent(R.id.notificationSkip, pendingSkip);
        contentView.setOnClickPendingIntent(R.id.notificationPrev, pendingPrev);
        contentView.setOnClickPendingIntent(R.id.notificationClose, pendingClose);

        mBuilder.setContent(contentView);
        mBuilder.setOngoing(true);
        mBuilder.setAutoCancel(false);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        mNotification = mBuilder.build();
        Picasso.with(this).load(mCurrentSong.getmAlbumArt())
                .placeholder(R.drawable.kwondeveloper)
                .into(contentView,R.id.notificationImage,1,mNotification);
        mManager.notify(1, mNotification);
        startForeground(1, mNotification);
    }

    //Initializer Equalizer for session
    public static void initAudioFX() {
        try {
            mEqualizerHelper = new EqualizerHelper(mMediaPlayer.getAudioSessionId(),true);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            mEqualizerHelper.setmIsEqualizerEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            mEqualizerHelper.setmIsEqualizerEnabled(false);
        }

    }
}