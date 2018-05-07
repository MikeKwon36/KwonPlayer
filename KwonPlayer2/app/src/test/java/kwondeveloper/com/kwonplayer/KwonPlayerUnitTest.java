package kwondeveloper.com.kwonplayer;

import android.content.Context;
import android.media.MediaPlayer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import kwondeveloper.com.kwonplayer.SupportClasses.EqualizerHelper;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class KwonPlayerUnitTest {

    @Mock
    MediaPlayer mMediaPlayer = new MediaPlayer();

    @Mock
    Context mMockContext;

    @Test
    public void testUseOfMockMediaPlayerObjects() throws Exception {
        int audioSessionId = mMediaPlayer.getAudioSessionId();
        assertEquals(audioSessionId,mMediaPlayer.getAudioSessionId());
    }


}