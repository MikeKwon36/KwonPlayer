package kwondeveloper.com.kwonplayer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Todo on 3/22/2016.
 */
public class MusicIntentReceiver extends android.content.BroadcastReceiver {

    //Catch system broadcasts when headphone jack is pulled out from socket during playback

    @Override
    public void onReceive(Context ctx, Intent intent) {

        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            Log.d("***BroadcastReceiver", "intent action received = " + intent.getAction());
            // signal service to stop playback via an Intent
            if(MediaPlaybackService.mMediaPlayer!=null && MediaPlaybackService.mMediaPlayer.isPlaying()){
                Intent stopPlayback = new Intent(ctx, MediaPlaybackService.class);
                stopPlayback.setAction(MediaPlaybackService.PAUSE);
                ctx.startService(stopPlayback);
                Log.d("***BroadcastReceiver", "intent sent with action = " + stopPlayback.getAction());
            }
        }

        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            Log.d("***BroadcastReceiver", "intent action received = " + intent.getAction());
            int state = intent.getIntExtra("state", -1);
            if (state == 0) {
                // signal service to stop playback via an Intent
                if(MediaPlaybackService.mMediaPlayer!=null && MediaPlaybackService.mMediaPlayer.isPlaying()){
                    Intent stopPlayback = new Intent(ctx, MediaPlaybackService.class);
                    stopPlayback.setAction(MediaPlaybackService.PAUSE);
                    ctx.startService(stopPlayback);
                    Log.d("***BroadcastReceiver", "intent sent with action = " + stopPlayback.getAction());
                }
            }
        }
    }
}
