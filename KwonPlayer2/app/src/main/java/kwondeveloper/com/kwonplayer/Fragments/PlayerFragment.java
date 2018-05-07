package kwondeveloper.com.kwonplayer.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import kwondeveloper.com.kwonplayer.AnalyticsApplication;
import kwondeveloper.com.kwonplayer.MainActivity;
import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;
import kwondeveloper.com.kwonplayer.MediaPlaybackService;
import kwondeveloper.com.kwonplayer.Playlists.AllSongsOnDevice;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.R;
import kwondeveloper.com.kwonplayer.SupportClasses.FadeAnimation;

public class PlayerFragment extends Fragment {

    private static Context mContext;
    private static Handler mHandler;
    private static SeekBar mSeekbar;
    private RelativeLayout mPlayPauseButtonBackground;
    private static ImageButton mPlayPauseButton;
    private static ImageButton mNextButton;
    private static ImageButton mPreviousButton;
    private static ImageButton mShuffleButton;
    private static ImageButton mRepeatButton;
    private static ImageView mAlbumArt;
    private static CardView mSeekbarIndicatorLayoutParent,mAlbumArtCard,mAlbumDetailsCard;
    private static TextView mSeekbarIndicatorText, mAlbumArtist, mAlbumTitle;
    private static PlayerFragment mInstance;
    private PlayerFragmentInteractionListener mListener;
    private Tracker mTracker;

    public PlayerFragment() {}

    public static PlayerFragment newInstance() {
        if(mInstance == null){
            mInstance = new PlayerFragment();
        }
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        //Song info/seekbar/playback elements
        mSeekbarIndicatorLayoutParent = (CardView) view.findViewById(R.id.seekbarIndicatorParent);
        mSeekbarIndicatorText = (TextView) view.findViewById(R.id.seekbarIndicatorText);
        mSeekbar = (SeekBar) view.findViewById(R.id.nowPlayingSeekBar);
        mPlayPauseButtonBackground = (RelativeLayout) view.findViewById(R.id.playPauseButtonBackground);
        mPlayPauseButton = (ImageButton) view.findViewById(R.id.playPauseButton);
        mNextButton = (ImageButton) view.findViewById(R.id.nextButton);
        mPreviousButton = (ImageButton) view.findViewById(R.id.previousButton);
        mShuffleButton = (ImageButton) view.findViewById(R.id.shuffleButton);
        mRepeatButton = (ImageButton) view.findViewById(R.id.repeatButton);
        mAlbumArt = (ImageView) view.findViewById(R.id.playerFragAlbumArt);
        mAlbumArtCard = (CardView) view.findViewById(R.id.playerFragAlbumArtCardView);
        mAlbumDetailsCard = (CardView) view.findViewById(R.id.playerFragDetailsCardView);
        mAlbumArtist = (TextView) view.findViewById(R.id.playerFragAlbumArtist);
        mAlbumTitle = (TextView) view.findViewById(R.id.playerFragAlbumTitle);
        Log.d("***PlayerFragOnCreate", "instantiates UI member variables");

        //click listeners
        mSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        mNextButton.setOnClickListener(mOnClickNextListener);
        mPreviousButton.setOnClickListener(mOnClickPreviousListener);
        mPlayPauseButton.setOnClickListener(playPauseClickListener);
        mPlayPauseButtonBackground.setOnClickListener(playPauseClickListener);
        mShuffleButton.setOnClickListener(shuffleButtonClickListener);
        mRepeatButton.setOnClickListener(repeatButtonClickListener);

        return view;
    }

    // TODO: Rename method, update argument and hook method to fragment's user interface
    public void onButtonPressed(View v) {
        if (mListener != null) {
            mListener.onPlayerFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mHandler = new Handler();

        if (context instanceof PlayerFragmentInteractionListener) {
            mListener = (PlayerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        Log.d("***PlayerFragOnAttach", "instantiates mContext, mHandler, mListener");

        if(MediaPlaybackService.mMediaPlayer == null){
            MediaPlaybackService.mMediaPlayer = new MediaPlayer();
            Log.d("***PlayerFragOnAttach", "new MediaPlayer created on MediaPlaybackService.mMediaPlayer");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Updates the playback control buttons
        setPlayPauseButton();
        setRepeatButtonIcon();
        setShuffleButtonIcon();
        setSeekbarDuration();
        if(MediaPlaybackService.mCurrentSong != null){
            setAlbumArtAndDetails(MediaPlaybackService.mCurrentSong);
        }
        Log.d("***PlayerFragOnResume", "onResume calls playback UI updates");

        //Google Analytics
        ConnectivityManager mgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if(info != null && info.isConnected()){
            AnalyticsApplication application = (AnalyticsApplication) mContext.getApplicationContext();
            mTracker = application.getDefaultTracker();
            mTracker.setScreenName(getResources().getString(R.string.analytics_player_fragment));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public interface PlayerFragmentInteractionListener {
        void onPlayerFragmentInteraction();
    }

    //Sets album details
    public static void setAlbumArtAndDetails(KwonMediaObject object) {
        if(object!=null){
            mAlbumArtist.setText(object.getmArtist());
            mAlbumTitle.setText(object.getmTitle());
            mAlbumArt.setVisibility(View.VISIBLE);
            mAlbumArtCard.setVisibility(View.VISIBLE);
            mAlbumDetailsCard.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(object.getmAlbumArt()).centerCrop().fit()
                    .placeholder(R.drawable.kwondeveloper)
                    .into(mAlbumArt);
        } else {
            mAlbumArtist.setText("");
            mAlbumTitle.setText("");
            mAlbumArt.setVisibility(View.GONE);
            mAlbumArtCard.setVisibility(View.GONE);
            mAlbumDetailsCard.setVisibility(View.GONE);
            Picasso.with(mContext).load(R.drawable.kwondeveloper).centerCrop().fit().into(mAlbumArt);
        }
    }

    //Sets the play/pause button states
    public static void setPlayPauseButton() {
        if (MediaPlaybackService.mMediaPlayer != null && MediaPlaybackService.mMediaPlayer.isPlaying()) {
            animatePlayToPause();
        } else {
            animatePauseToPlay();
        }
    }

    //Sets the repeat button icon based on the current repeat mode
    private static void setRepeatButtonIcon() {
        if(MediaPlaybackService.mRepeatState == null){
            mRepeatButton.setAlpha(.2f);
        } else {
            switch (MediaPlaybackService.mRepeatState){
                case MediaPlaybackService.REPEAT_MODE_OFF:
                    mRepeatButton.setAlpha(.2f);
                    break;
                case MediaPlaybackService.REPEAT_MODE_ALL:
                    mRepeatButton.setAlpha(1.0f);
                    mRepeatButton.setImageResource(R.drawable.button_repeatall);
                    break;
                case MediaPlaybackService.REPEAT_MODE_SINGLE:
                    mRepeatButton.setAlpha(1.0f);
                    mRepeatButton.setImageResource(R.drawable.button_repeatone);
                    break;
            }
        }
    }

    //Sets the shuffle button icon based on the current shuffle mode
    private static void setShuffleButtonIcon() {
        if (MediaPlaybackService.mIsShuffleOn) {
            mShuffleButton.setAlpha(1.0f);
        } else {
            mShuffleButton.setAlpha(.2f);}
    }

    //Sets the seekbar's duration... Also updates the elapsed/remaining duration text
    public static void setSeekbarDuration() {
        if(MediaPlaybackService.mMediaPlayer != null && MediaPlaybackService.mMediaPlayer.isPlaying()){
            mSeekbar.setMax(MediaPlaybackService.mMediaPlayer.getDuration() / 1000);
            mSeekbar.setProgress(MediaPlaybackService.mMediaPlayer.getCurrentPosition() / 1000);
            mHandler.postDelayed(seekbarUpdateRunnable, 100);
        } else {
            mHandler.removeCallbacks(seekbarUpdateRunnable);
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int seekBarPosition, boolean changedByUser) {
            try {
                //dynamically update the seekbar & indicator values as user moves the seekbar
                long currentSongDuration = MediaPlaybackService.mMediaPlayer.getDuration();
                seekBar.setMax((int) currentSongDuration / 1000);
                if (changedByUser){
                    mSeekbarIndicatorText.setText(convertMillisToMinsSecs(seekBar.getProgress() * 1000));
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Keep seekbar indicator on screen as long as touching the seekbar (and remove the
            // handler updating the seekbar progress
            mHandler.removeCallbacks(seekbarUpdateRunnable);
            mHandler.removeCallbacks(fadeOutSeekbarIndicator);
            mSeekbarIndicatorLayoutParent.setVisibility(View.VISIBLE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int seekBarPosition = seekBar.getProgress();
            MediaPlaybackService.mMediaPlayer.seekTo(seekBarPosition * 1000);

            //Reinitiate the handler controlling the seekbar's progress
            mHandler.post(seekbarUpdateRunnable);

            //Fade out the indicator once you let go of the seekbar
            mHandler.postDelayed(fadeOutSeekbarIndicator, 1000);
        }
    };

    //Repeat button click listener
    private View.OnClickListener repeatButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            if(MediaPlaybackService.mRepeatState == null){
                mRepeatButton.setAlpha(.2f);
            } else {
                switch (MediaPlaybackService.mRepeatState){
                    case MediaPlaybackService.REPEAT_MODE_OFF:
                        MediaPlaybackService.mRepeatState = MediaPlaybackService.REPEAT_MODE_ALL;
                        break;
                    case MediaPlaybackService.REPEAT_MODE_ALL:
                        MediaPlaybackService.mRepeatState = MediaPlaybackService.REPEAT_MODE_SINGLE;
                        break;
                    case MediaPlaybackService.REPEAT_MODE_SINGLE:
                        MediaPlaybackService.mRepeatState = MediaPlaybackService.REPEAT_MODE_OFF;
                        break;
                }
                setRepeatButtonIcon();
            }
        }
    };

    //Shuffle button click listener will toggle shuffle button on/off
    private View.OnClickListener shuffleButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            if (MediaPlaybackService.mIsShuffleOn){
                MediaPlaybackService.mIsShuffleOn = false;}
            else {
                MediaPlaybackService.mIsShuffleOn = true;}
            setShuffleButtonIcon();
        }
    };

    //Click listener for the play/pause button
    private View.OnClickListener playPauseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            if (MediaPlaybackService.mMediaPlayer!=null && MediaPlaybackService.mMediaPlayer.isPlaying()) {
                animatePauseToPlay();
                Intent intent = new Intent(mContext,MediaPlaybackService.class);
                intent.setAction(MediaPlaybackService.PAUSE);
                mContext.startService(intent);
                mHandler.removeCallbacks(seekbarUpdateRunnable);
            } else {
                animatePlayToPause();
                Intent intent = new Intent(mContext,MediaPlaybackService.class);
                intent.setAction(MediaPlaybackService.PLAY);
                mContext.startService(intent);
                mHandler.post(seekbarUpdateRunnable);
            }
        }
    };

    //Click listener for the previous button
    private View.OnClickListener mOnClickPreviousListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            //Remove the seekbar update runnable
            mHandler.removeCallbacks(seekbarUpdateRunnable);
            Intent intent = new Intent(mContext,MediaPlaybackService.class);
            intent.setAction(MediaPlaybackService.PREVIOUS);
            mContext.startService(intent);
        }

    };

    //Click listener for the next button
    private View.OnClickListener mOnClickNextListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            //Remove the seekbar update runnable
            mHandler.removeCallbacks(seekbarUpdateRunnable);
            Intent intent = new Intent(mContext,MediaPlaybackService.class);
            intent.setAction(MediaPlaybackService.SKIP);
            mContext.startService(intent);
        }

    };

    //Animates the play button to a pause button
    private static void animatePlayToPause() {
        //Scale out the play button
        final ScaleAnimation scaleOut = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                mPlayPauseButton.getWidth()/2,
                mPlayPauseButton.getHeight()/2);
        scaleOut.setDuration(150);
        scaleOut.setInterpolator(new AccelerateInterpolator());

        //Scale in the pause button
        final ScaleAnimation scaleIn = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                mPlayPauseButton.getWidth()/2,
                mPlayPauseButton.getHeight()/2);
        scaleIn.setDuration(150);
        scaleIn.setInterpolator(new DecelerateInterpolator());

        scaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlayPauseButton.setImageResource(R.drawable.button_pause);
                mPlayPauseButton.setPadding(0, 0, 0, 0);
                mPlayPauseButton.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        scaleIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlayPauseButton.setScaleX(1.0f);
                mPlayPauseButton.setScaleY(1.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mPlayPauseButton.startAnimation(scaleOut);
    }

    //Animates the pause button to a play button
    private static void animatePauseToPlay() {

        //Scale out the pause button.
        final ScaleAnimation scaleOut = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                mPlayPauseButton.getWidth()/2, mPlayPauseButton.getHeight()/2);
        scaleOut.setDuration(150);
        scaleOut.setInterpolator(new AccelerateInterpolator());

        //Scale in the play button.
        final ScaleAnimation scaleIn = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                mPlayPauseButton.getWidth()/2, mPlayPauseButton.getHeight()/2);
        scaleIn.setDuration(150);
        scaleIn.setInterpolator(new DecelerateInterpolator());

        scaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlayPauseButton.setImageResource(R.drawable.button_play);
                mPlayPauseButton.setPadding(0, 0, -5, 0);
                mPlayPauseButton.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        scaleIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlayPauseButton.setScaleX(1.0f);
                mPlayPauseButton.setScaleY(1.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mPlayPauseButton.startAnimation(scaleOut);
    }

    //Create a new Runnable to update the seekbar and time every 100ms
    public static Runnable seekbarUpdateRunnable = new Runnable() {
        public void run() {
            try {
                long currentPosition = MediaPlaybackService.mMediaPlayer.getCurrentPosition();
                int currentPositionInSecs = (int) currentPosition/1000;
                smoothScrollSeekbar(currentPositionInSecs);

                //recursively call the runnable until the song completes
                mHandler.postDelayed(seekbarUpdateRunnable, 100);

            } catch (Exception e) {e.printStackTrace();}
        }
    };

    //Seekbar change indicator
    private Runnable fadeOutSeekbarIndicator = new Runnable() {
        @Override
        public void run() {
            FadeAnimation fadeOut = new FadeAnimation(mSeekbarIndicatorLayoutParent, 300, 0.9f, 0.0f, null);
            fadeOut.animate();
        }
    };

    //Smoothly scrolls the seekbar to the indicated position
    private static void smoothScrollSeekbar(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(mSeekbar, "progress", progress);
        animation.setDuration(200);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    //Converts milliseconds to hh:mm:ss format
    public String convertMillisToMinsSecs(long milliseconds) {
        int secondsValue = (int) (milliseconds / 1000) % 60 ;
        int minutesValue = (int) ((milliseconds / (1000*60)) % 60);
        int hoursValue  = (int) ((milliseconds / (1000*60*60)) % 24);

        String seconds = secondsValue < 10 ? "0"+secondsValue : ""+secondsValue;
        String minutes = minutesValue < 10 ? "0"+minutesValue : ""+minutesValue;
        String hours = hoursValue < 10 ? "0"+hoursValue : ""+hoursValue;

        String output = hoursValue!=0 ? hours+":"+minutes+":"+seconds : minutes+":"+seconds;
        return output;
    }
}
