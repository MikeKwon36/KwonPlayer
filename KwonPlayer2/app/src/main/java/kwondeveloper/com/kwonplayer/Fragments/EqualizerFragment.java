package kwondeveloper.com.kwonplayer.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.PresetReverb;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kwondeveloper.com.kwonplayer.MediaPlaybackService;
import kwondeveloper.com.kwonplayer.R;
import kwondeveloper.com.kwonplayer.SupportClasses.EqualizerHelper;
import kwondeveloper.com.kwonplayer.SupportClasses.VerticalSeekBar;

public class EqualizerFragment extends Fragment {

    private Context mContext;
    private EqualizerHelper mEqualizerHelper;
    private FloatingActionButton mFAB;
    private SharedPreferences mSharedPreferences;
    private static final String SHARED_PREFS_PRESETS = "com.kwondeveloper.kwonplayer.presets";
    private static final String SHARED_PREFS_50 = "com.kwondeveloper.kwonplayer.50";
    private static final String SHARED_PREFS_130 = "com.kwondeveloper.kwonplayer.130";
    private static final String SHARED_PREFS_320 = "com.kwondeveloper.kwonplayer.320";
    private static final String SHARED_PREFS_800 = "com.kwondeveloper.kwonplayer.800";
    private static final String SHARED_PREFS_2K = "com.kwondeveloper.kwonplayer.2K";
    private static final String SHARED_PREFS_5K = "com.kwondeveloper.kwonplayer.5K";
    private static final String SHARED_PREFS_12K = "com.kwondeveloper.kwonplayer.12K";
    private static final String SHARED_PREFS_VIRTUALIZER = "com.kwondeveloper.VIRTUALIZER";
    private static final String SHARED_PREFS_BASS = "com.kwondeveloper.kwonplayer.BASS";
    private static final String SHARED_PREFS_REVERB = "com.kwondeveloper.kwonplayer.REVERB";

    //equalizer controls
    private VerticalSeekBar equalizer50HzSeekBar;
    private TextView text50HzGainTextView;
    private VerticalSeekBar equalizer130HzSeekBar;
    private TextView text130HzGainTextView;
    private VerticalSeekBar equalizer320HzSeekBar;
    private TextView text320HzGainTextView;
    private VerticalSeekBar equalizer800HzSeekBar;
    private TextView text800HzGainTextView;
    private VerticalSeekBar equalizer2kHzSeekBar;
    private TextView text2kHzGainTextView;
    private VerticalSeekBar equalizer5kHzSeekBar;
    private TextView text5kHzGainTextView;
    private VerticalSeekBar equalizer12_5kHzSeekBar;
    private TextView text12_5kHzGainTextView;

    //Audio FX elements
    private SeekBar virtualizerSeekBar;
    private SeekBar bassBoostSeekBar;
    private Spinner reverbSpinner;

    public EqualizerFragment() {}

    public static EqualizerFragment newInstance() {
        EqualizerFragment fragment = new EqualizerFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equalizer, container, false);

        if(MediaPlaybackService.mMediaPlayer==null){
            MediaPlaybackService.mMediaPlayer = new MediaPlayer();
        }
        if(mEqualizerHelper==null){
            mEqualizerHelper = new EqualizerHelper(MediaPlaybackService.mMediaPlayer.getAudioSessionId(),true);
        }
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS_PRESETS,mContext.MODE_PRIVATE);

        equalizer50HzSeekBar = (VerticalSeekBar) view.findViewById(R.id.equalizer50Hz);
        text50HzGainTextView = (TextView) view.findViewById(R.id.text50HzGain);
        equalizer130HzSeekBar = (VerticalSeekBar) view.findViewById(R.id.equalizer130Hz);
        text130HzGainTextView = (TextView) view.findViewById(R.id.text130HzGain);
        equalizer320HzSeekBar = (VerticalSeekBar) view.findViewById(R.id.equalizer320Hz);
        text320HzGainTextView = (TextView) view.findViewById(R.id.text320HzGain);
        equalizer800HzSeekBar = (VerticalSeekBar) view.findViewById(R.id.equalizer800Hz);
        text800HzGainTextView = (TextView) view.findViewById(R.id.text800HzGain);
        equalizer2kHzSeekBar = (VerticalSeekBar) view.findViewById(R.id.equalizer2kHz);
        text2kHzGainTextView = (TextView) view.findViewById(R.id.text2kHzGain);
        equalizer5kHzSeekBar = (VerticalSeekBar) view.findViewById(R.id.equalizer5kHz);
        text5kHzGainTextView = (TextView) view.findViewById(R.id.text5kHzGain);
        equalizer12_5kHzSeekBar = (VerticalSeekBar) view.findViewById(R.id.equalizer12_5kHz);
        text12_5kHzGainTextView = (TextView) view.findViewById(R.id.text12_5kHzGain);

        virtualizerSeekBar = (SeekBar) view.findViewById(R.id.virtualizer_seekbar);
        virtualizerSeekBar.setMax(1000);
        bassBoostSeekBar = (SeekBar) view.findViewById(R.id.bass_boost_seekbar);
        bassBoostSeekBar.setMax(1000);

        //Initialize reverb presets
        reverbSpinner = (Spinner) view.findViewById(R.id.reverb_spinner);
        String[] reverbPresets = getResources().getStringArray(R.array.ReverbPresets);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, reverbPresets);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reverbSpinner.setAdapter(dataAdapter);

        mFAB = (FloatingActionButton) view.findViewById(R.id.resetFAB);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset all sliders
                equalizer50HzSeekBar.setProgressAndThumb(mEqualizerHelper.DEFAULT_Hz_LEVEL);
                equalizer130HzSeekBar.setProgressAndThumb(mEqualizerHelper.DEFAULT_Hz_LEVEL);
                equalizer320HzSeekBar.setProgressAndThumb(mEqualizerHelper.DEFAULT_Hz_LEVEL);
                equalizer800HzSeekBar.setProgressAndThumb(mEqualizerHelper.DEFAULT_Hz_LEVEL);
                equalizer2kHzSeekBar.setProgressAndThumb(mEqualizerHelper.DEFAULT_Hz_LEVEL);
                equalizer5kHzSeekBar.setProgressAndThumb(mEqualizerHelper.DEFAULT_Hz_LEVEL);
                equalizer12_5kHzSeekBar.setProgressAndThumb(mEqualizerHelper.DEFAULT_Hz_LEVEL);
                virtualizerSeekBar.setProgress(mEqualizerHelper.DEFAULT_AUDIOEFFECT_LEVEL);
                bassBoostSeekBar.setProgress(mEqualizerHelper.DEFAULT_AUDIOEFFECT_LEVEL);
                reverbSpinner.setSelection(mEqualizerHelper.DEFAULT_REVERB_SETTING, false);

                //Apply all settings to audio and UI
                updateEQ();
            }
        });

        equalizer50HzSeekBar.setOnSeekBarChangeListener(equalizer50HzListener);
        equalizer130HzSeekBar.setOnSeekBarChangeListener(equalizer130HzListener);
        equalizer320HzSeekBar.setOnSeekBarChangeListener(equalizer320HzListener);
        equalizer800HzSeekBar.setOnSeekBarChangeListener(equalizer800HzListener);
        equalizer2kHzSeekBar.setOnSeekBarChangeListener(equalizer2kHzListener);
        equalizer5kHzSeekBar.setOnSeekBarChangeListener(equalizer5kHzListener);
        equalizer12_5kHzSeekBar.setOnSeekBarChangeListener(equalizer12_5kHzListener);
        virtualizerSeekBar.setOnSeekBarChangeListener(virtualizerListener);
        bassBoostSeekBar.setOnSeekBarChangeListener(bassBoostListener);
        reverbSpinner.setOnItemSelectedListener(reverbListener);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(SHARED_PREFS_50,equalizer50HzSeekBar.getProgress());
        editor.putInt(SHARED_PREFS_130,equalizer130HzSeekBar.getProgress());
        editor.putInt(SHARED_PREFS_320,equalizer320HzSeekBar.getProgress());
        editor.putInt(SHARED_PREFS_800,equalizer800HzSeekBar.getProgress());
        editor.putInt(SHARED_PREFS_2K,equalizer2kHzSeekBar.getProgress());
        editor.putInt(SHARED_PREFS_5K,equalizer5kHzSeekBar.getProgress());
        editor.putInt(SHARED_PREFS_12K,equalizer12_5kHzSeekBar.getProgress());
        editor.putInt(SHARED_PREFS_VIRTUALIZER,virtualizerSeekBar.getProgress());
        editor.putInt(SHARED_PREFS_BASS,bassBoostSeekBar.getProgress());
        editor.putInt(SHARED_PREFS_REVERB,reverbSpinner.getSelectedItemPosition());
        editor.commit();
        Log.d("***EqualFragOnPause", "Saved 50hz level = " + equalizer50HzSeekBar.getProgress());
        Log.d("***EqualFragOnPause", "Saved 130hz level = " + equalizer130HzSeekBar.getProgress());
        Log.d("***EqualFragOnPause", "Saved 320hz level = " + equalizer320HzSeekBar.getProgress());
        Log.d("***EqualFragOnPause", "Saved 800hz level = " + equalizer800HzSeekBar.getProgress());
        Log.d("***EqualFragOnPause", "Saved 2khz level = " + equalizer2kHzSeekBar.getProgress());
        Log.d("***EqualFragOnPause", "Saved 5khz level = " + equalizer5kHzSeekBar.getProgress());
        Log.d("***EqualFragOnPause", "Saved 12khz level = " + equalizer12_5kHzSeekBar.getProgress());
        Log.d("***EqualFragOnPause", "Saved Virtualizer level = " + virtualizerSeekBar.getProgress());
        Log.d("***EqualFragOnPause", "Saved Bass level = " + bassBoostSeekBar.getProgress());
        Log.d("***EqualFragOnPause", "Saved Reverb setting = " + reverbSpinner.getSelectedItemPosition());
    }

    @Override
    public void onResume() {
        super.onResume();
        //restore previous levels
        if(mSharedPreferences.getInt(SHARED_PREFS_50,-1)!=-1){
            equalizer50HzSeekBar.setProgressAndThumb(mSharedPreferences.getInt(SHARED_PREFS_50,-1));
            Log.d("***EqualFragOnResume", "50hz level retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_50,-1));
        }
        if(mSharedPreferences.getInt(SHARED_PREFS_130,-1)!=-1){
            equalizer130HzSeekBar.setProgressAndThumb(mSharedPreferences.getInt(SHARED_PREFS_130,-1));
            Log.d("***EqualFragOnResume", "130hz level retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_130,-1));
        }
        if(mSharedPreferences.getInt(SHARED_PREFS_320,-1)!=-1){
            equalizer320HzSeekBar.setProgressAndThumb(mSharedPreferences.getInt(SHARED_PREFS_320,-1));
            Log.d("***EqualFragOnResume", "320hz level retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_320,-1));
        }
        if(mSharedPreferences.getInt(SHARED_PREFS_800,-1)!=-1){
            equalizer800HzSeekBar.setProgressAndThumb(mSharedPreferences.getInt(SHARED_PREFS_800,-1));
            Log.d("***EqualFragOnResume", "800hz level retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_800,-1));
        }
        if(mSharedPreferences.getInt(SHARED_PREFS_2K,-1)!=-1){
            equalizer2kHzSeekBar.setProgressAndThumb(mSharedPreferences.getInt(SHARED_PREFS_2K,-1));
            Log.d("***EqualFragOnResume", "2khz level retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_2K,-1));
        }
        if(mSharedPreferences.getInt(SHARED_PREFS_5K,-1)!=-1){
            equalizer5kHzSeekBar.setProgressAndThumb(mSharedPreferences.getInt(SHARED_PREFS_5K,-1));
            Log.d("***EqualFragOnResume", "5khz level retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_5K,-1));
        }
        if(mSharedPreferences.getInt(SHARED_PREFS_12K,-1)!=-1){
            equalizer12_5kHzSeekBar.setProgressAndThumb(mSharedPreferences.getInt(SHARED_PREFS_12K,-1));
            Log.d("***EqualFragOnResume", "12khz level retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_12K,-1));
        }
        if(mSharedPreferences.getInt(SHARED_PREFS_VIRTUALIZER,-1)!=-1){
            virtualizerSeekBar.setProgress((short)mSharedPreferences.getInt(SHARED_PREFS_VIRTUALIZER,-1));
            Log.d("***EqualFragOnResume", "Virtualizer level retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_VIRTUALIZER,-1));
        }
        if(mSharedPreferences.getInt(SHARED_PREFS_BASS,-1)!=-1){
            bassBoostSeekBar.setProgress((short)mSharedPreferences.getInt(SHARED_PREFS_BASS,-1));
            Log.d("***EqualFragOnResume", "Bass level retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_BASS,-1));
        }
        if(mSharedPreferences.getInt(SHARED_PREFS_REVERB,-1)!=-1){
            reverbSpinner.setSelection(mSharedPreferences.getInt(SHARED_PREFS_REVERB,-1),false);
            Log.d("***EqualFragOnResume", "Reverb setting retrieved = " + mSharedPreferences.getInt(SHARED_PREFS_REVERB,-1));
        }
        updateEQ();
    }

    //50 Hz equalizer seekbar listener
    private SeekBar.OnSeekBarChangeListener equalizer50HzListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                //Get the appropriate equalizer band.
                short hertzBand = mEqualizerHelper.getEqualizer().getBand(mEqualizerHelper.FIFTY_HZ_BAND);
                //Set the gain level text based on the slider position.
                if (seekBarLevel==16) {
                    text50HzGainTextView.setText(getString(R.string.EqualizerFragment_0db));
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel==0) {
                        text50HzGainTextView.setText("-" + getString(R.string.EqualizerFragment_15db));
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) (-1500));
                    } else {
                        text50HzGainTextView.setText("-" + (16-seekBarLevel) + " dB");
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) -((16-seekBarLevel)*100));
                    }
                } else if (seekBarLevel > 16) {
                    text50HzGainTextView.setText("+" + (seekBarLevel-16) + " dB");
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) ((seekBarLevel-16)*100));
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {}

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {}
    };

    //130 Hz equalizer seekbar listener.
    private SeekBar.OnSeekBarChangeListener equalizer130HzListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                //Get the appropriate equalizer band.
                short hertzBand = mEqualizerHelper.getEqualizer().getBand(mEqualizerHelper.ONETHIRTY_HZ_BAND);

                //Set the gain level text based on the slider position.
                if (seekBarLevel==16) {
                    text130HzGainTextView.setText(getString(R.string.EqualizerFragment_0db));
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel==0) {
                        text130HzGainTextView.setText("-" + getString(R.string.EqualizerFragment_15db));
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) (-1500));
                    } else {
                        text130HzGainTextView.setText("-" + (16-seekBarLevel) + " dB");
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) -((16-seekBarLevel)*100));
                    }
                } else if (seekBarLevel > 16) {
                    text130HzGainTextView.setText("+" + (seekBarLevel-16) + " dB");
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) ((seekBarLevel-16)*100));
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {}

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {}
    };

    //320 Hz equalizer seekbar listener.
    private SeekBar.OnSeekBarChangeListener equalizer320HzListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                //Get the appropriate equalizer band.
                short hertzBand = mEqualizerHelper.getEqualizer().getBand(mEqualizerHelper.THREETWENTY_HZ_BAND);

                //Set the gain level text based on the slider position.
                if (seekBarLevel==16) {
                    text320HzGainTextView.setText(getString(R.string.EqualizerFragment_0db));
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel==0) {
                        text320HzGainTextView.setText("-" + getString(R.string.EqualizerFragment_15db));
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) (-1500));
                    } else {
                        text320HzGainTextView.setText("-" + (16-seekBarLevel) + " dB");
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) -((16-seekBarLevel)*100));
                    }
                } else if (seekBarLevel > 16) {
                    text320HzGainTextView.setText("+" + (seekBarLevel-16) + " dB");
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) ((seekBarLevel-16)*100));
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {}

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {}
    };

    //800 Hz equalizer seekbar listener.
    private SeekBar.OnSeekBarChangeListener equalizer800HzListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                //Get the appropriate equalizer band.
                short hertzBand = mEqualizerHelper.getEqualizer().getBand(mEqualizerHelper.EIGHTHUNDRED_HZ_BAND);

                //Set the gain level text based on the slider position.
                if (seekBarLevel==16) {
                    text800HzGainTextView.setText(getString(R.string.EqualizerFragment_0db));
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel==0) {
                        text800HzGainTextView.setText("-" + getString(R.string.EqualizerFragment_15db));
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) (-1500));
                    } else {
                        text800HzGainTextView.setText("-" + (16-seekBarLevel) + " dB");
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) -((16-seekBarLevel)*100));
                    }
                } else if (seekBarLevel > 16) {
                    text800HzGainTextView.setText("+" + (seekBarLevel-16) + " dB");
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) ((seekBarLevel-16)*100));
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {}

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {}
    };

    //2 kHz equalizer seekbar listener.
    private SeekBar.OnSeekBarChangeListener equalizer2kHzListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                //Get the appropriate equalizer band.
                short hertzBand = mEqualizerHelper.getEqualizer().getBand(mEqualizerHelper.TWO_KHZ_BAND);

                //Set the gain level text based on the slider position.
                if (seekBarLevel==16) {
                    text2kHzGainTextView.setText(getString(R.string.EqualizerFragment_0db));
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel==0) {
                        text2kHzGainTextView.setText("-" + getString(R.string.EqualizerFragment_15db));
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) (-1500));
                    } else {
                        text2kHzGainTextView.setText("-" + (16-seekBarLevel) + " dB");
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) -((16-seekBarLevel)*100));
                    }
                } else if (seekBarLevel > 16) {
                    text2kHzGainTextView.setText("+" + (seekBarLevel-16) + " dB");
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) ((seekBarLevel-16)*100));
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {}

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {}
    };

    //5 kHz equalizer seekbar listener.
    private SeekBar.OnSeekBarChangeListener equalizer5kHzListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                //Get the appropriate equalizer band.
                short hertzBand = mEqualizerHelper.getEqualizer().getBand(mEqualizerHelper.FIVE_KHZ_BAND);

                //Set the gain level text based on the slider position.
                if (seekBarLevel==16) {
                    text5kHzGainTextView.setText(getString(R.string.EqualizerFragment_0db));
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel==0) {
                        text5kHzGainTextView.setText("-" + getString(R.string.EqualizerFragment_15db));
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) (-1500));
                    } else {
                        text5kHzGainTextView.setText("-" + (16-seekBarLevel) + " dB");
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) -((16-seekBarLevel)*100));
                    }
                } else if (seekBarLevel > 16) {
                    text5kHzGainTextView.setText("+" + (seekBarLevel-16) + " dB");
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) ((seekBarLevel-16)*100));
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {}

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {}
    };

    //12.5 kHz equalizer seekbar listener.
    private SeekBar.OnSeekBarChangeListener equalizer12_5kHzListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                //Get the appropriate equalizer band.
                short hertzBand = mEqualizerHelper.getEqualizer().getBand(mEqualizerHelper.TWELVE_KHZ_BAND);

                //Set the gain level text based on the slider position.
                if (seekBarLevel==16) {
                    text12_5kHzGainTextView.setText(getString(R.string.EqualizerFragment_0db));
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel==0) {
                        text12_5kHzGainTextView.setText("-" + getString(R.string.EqualizerFragment_15db));
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) (-1500));
                    } else {
                        text12_5kHzGainTextView.setText("-" + (16-seekBarLevel) + " dB");
                        mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) -((16-seekBarLevel)*100));
                    }
                } else if (seekBarLevel > 16) {
                    text12_5kHzGainTextView.setText("+" + (seekBarLevel-16) + " dB");
                    mEqualizerHelper.getEqualizer().setBandLevel(hertzBand, (short) ((seekBarLevel-16)*100));
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {}

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {}
    };

    //Spinner listener for reverb effects.
    private AdapterView.OnItemSelectedListener reverbListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int index, long arg3) {
            if (MediaPlaybackService.mMediaPlayer!=null)
                if (index==0) {
                    mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_NONE);
                } else if (index==1) {
                    mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_LARGEHALL);
                } else if (index==2) {
                    mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_LARGEROOM);
                } else if (index==3) {
                    mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_MEDIUMHALL);
                } else if (index==4) {
                    mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_MEDIUMROOM);
                } else if (index==5) {
                    mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_SMALLROOM);
                } else if (index==6) {
                    mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_PLATE);
                }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {}
    };

    //Bass boost listener.
    private SeekBar.OnSeekBarChangeListener bassBoostListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            mEqualizerHelper.getBassBoost().setStrength((short) arg1);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    //Virtualizer listener
    private SeekBar.OnSeekBarChangeListener virtualizerListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            mEqualizerHelper.getVirtualizer().setStrength((short) arg1);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    //Updates the EQ settings
    public void updateEQ() {
        equalizer50HzListener.onProgressChanged(equalizer50HzSeekBar, equalizer50HzSeekBar.getProgress(), true);
        equalizer130HzListener.onProgressChanged(equalizer130HzSeekBar, equalizer130HzSeekBar.getProgress(), true);
        equalizer320HzListener.onProgressChanged(equalizer320HzSeekBar, equalizer320HzSeekBar.getProgress(), true);
        equalizer800HzListener.onProgressChanged(equalizer800HzSeekBar, equalizer800HzSeekBar.getProgress(), true);
        equalizer2kHzListener.onProgressChanged(equalizer2kHzSeekBar, equalizer2kHzSeekBar.getProgress(), true);
        equalizer5kHzListener.onProgressChanged(equalizer5kHzSeekBar, equalizer5kHzSeekBar.getProgress(), true);
        equalizer12_5kHzListener.onProgressChanged(equalizer12_5kHzSeekBar, equalizer12_5kHzSeekBar.getProgress(), true);
        virtualizerListener.onProgressChanged(virtualizerSeekBar, virtualizerSeekBar.getProgress(), true);
        bassBoostListener.onProgressChanged(bassBoostSeekBar, bassBoostSeekBar.getProgress(), true);
        reverbListener.onItemSelected(reverbSpinner, null, reverbSpinner.getSelectedItemPosition(), 0L);
    }
}
