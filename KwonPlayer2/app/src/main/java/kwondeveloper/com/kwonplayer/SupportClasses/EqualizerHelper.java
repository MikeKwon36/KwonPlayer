package kwondeveloper.com.kwonplayer.SupportClasses;

import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;

public class EqualizerHelper {

    private Equalizer mEqualizer;
    private Virtualizer mVirtualizer;
    private BassBoost mBassBoost;
    private PresetReverb mReverb;

    private boolean mIsEqualizerEnabled;

    final public int DEFAULT_Hz_LEVEL = 16;
    final public short DEFAULT_AUDIOEFFECT_LEVEL = 0;
    final public short DEFAULT_REVERB_SETTING = 0;
    final public int FIFTY_HZ_BAND = 50000;
    final public int ONETHIRTY_HZ_BAND = 130000;
    final public int THREETWENTY_HZ_BAND = 320000;
    final public int EIGHTHUNDRED_HZ_BAND = 800000;
    final public int TWO_KHZ_BAND = 2000000;
    final public int FIVE_KHZ_BAND = 5000000;
    final public int TWELVE_KHZ_BAND = 12000000;

    private int m50HzLevel = DEFAULT_Hz_LEVEL;
    private int m130HzLevel = DEFAULT_Hz_LEVEL;
    private int m320HzLevel = DEFAULT_Hz_LEVEL;
    private int m800HzLevel = DEFAULT_Hz_LEVEL;
    private int m2kHzLevel = DEFAULT_Hz_LEVEL;
    private int m5kHzLevel = DEFAULT_Hz_LEVEL;
    private int m12kHzLevel = DEFAULT_Hz_LEVEL;
    private short mVirtualizerLevel = DEFAULT_AUDIOEFFECT_LEVEL;
    private short mBassBoostLevel = DEFAULT_AUDIOEFFECT_LEVEL;
    private short mReverbSetting = DEFAULT_AUDIOEFFECT_LEVEL;

    //Equalizer helper constructors w/option to enable equalizer simultaneously====================
    public EqualizerHelper(int audioSessionId, boolean equalizerEnabled) {
        mEqualizer = new Equalizer(0, audioSessionId);
        mEqualizer.setEnabled(equalizerEnabled);
        mVirtualizer = new Virtualizer(0, audioSessionId);
        mVirtualizer.setEnabled(equalizerEnabled);
        mBassBoost = new BassBoost(0, audioSessionId);
        mBassBoost.setEnabled(equalizerEnabled);
        mReverb = new PresetReverb(0, audioSessionId);
        mReverb.setEnabled(equalizerEnabled);

        mIsEqualizerEnabled = equalizerEnabled;
    }

    //Release Equalizer objects and sets their references to null
    public void releaseEQ() {
        if(mEqualizer != null && mVirtualizer != null && mBassBoost != null && mReverb != null) {
            mEqualizer.release();
            mVirtualizer.release();
            mBassBoost.release();
            mReverb.release();
        }
        mEqualizer = null;
        mVirtualizer = null;
        mBassBoost = null;
        mReverb = null;

        mIsEqualizerEnabled = false;
    }

    //Enable Equalizer objects
    public void enableEQ(int audioSessionId) {
        mEqualizer = new Equalizer(0, audioSessionId);
        mEqualizer.setEnabled(true);
        mVirtualizer = new Virtualizer(0, audioSessionId);
        mVirtualizer.setEnabled(true);
        mBassBoost = new BassBoost(0, audioSessionId);
        mBassBoost.setEnabled(true);
        mReverb = new PresetReverb(0, audioSessionId);
        mReverb.setEnabled(true);

        mIsEqualizerEnabled = true;
    }

    //Getters & Setters ===========================================================================
    public Equalizer getEqualizer() {
        return mEqualizer;
    }

    public Virtualizer getVirtualizer() {
        return mVirtualizer;
    }

    public BassBoost getBassBoost() {
        return mBassBoost;
    }

    public PresetReverb getReverb() {
        return mReverb;
    }

    public int get50HzLevel() {
        m50HzLevel = mEqualizer.getBandLevel(mEqualizer.getBand(FIFTY_HZ_BAND));
        return m50HzLevel;
    }

    public int get130HzLevel() {
        m130HzLevel = mEqualizer.getBandLevel(mEqualizer.getBand(ONETHIRTY_HZ_BAND));
        return m130HzLevel;
    }

    public int get320HzLevel() {
        m320HzLevel = mEqualizer.getBandLevel(mEqualizer.getBand(THREETWENTY_HZ_BAND));
        return m320HzLevel;
    }

    public int get800HzLevel() {
        m800HzLevel = mEqualizer.getBandLevel(mEqualizer.getBand(EIGHTHUNDRED_HZ_BAND));
        return m800HzLevel;
    }

    public int get2kHzLevel() {
        m2kHzLevel = mEqualizer.getBandLevel(mEqualizer.getBand(TWO_KHZ_BAND));
        return m2kHzLevel;
    }

    public int get5kHzLevel() {
        m5kHzLevel = mEqualizer.getBandLevel(mEqualizer.getBand(FIVE_KHZ_BAND));
        return m5kHzLevel;
    }

    public int get12kHzLevel() {
        m12kHzLevel = mEqualizer.getBandLevel(mEqualizer.getBand(TWELVE_KHZ_BAND));
        return m12kHzLevel;
    }

    public short getVirtualizerLevel() {
        mVirtualizerLevel = mVirtualizer.getRoundedStrength();
        return mVirtualizerLevel;
    }

    public short getBassBoostLevel() {
        mBassBoostLevel = mBassBoost.getRoundedStrength();
        return mBassBoostLevel;
    }

    public short getReverbSetting() {
        mReverbSetting = mReverb.getPreset();
        return mReverbSetting;
    }

    public boolean ismIsEqualizerEnabled(){
        return mIsEqualizerEnabled;
    }

    public void setEqualizer(Equalizer equalizer) {
        mEqualizer = equalizer;
    }

    public void setVirtualizer(Virtualizer virtualizer) {
        mVirtualizer = virtualizer;
    }

    public void setBassBoost(BassBoost bassBoost) {
        mBassBoost = bassBoost;
    }

    public void setReverb(PresetReverb reverb) {
        mReverb = reverb;
    }

    public void set50HzLevel(int l50HzLevel) {
        m50HzLevel = l50HzLevel;
    }

    public void set130HzLevel(int l130HzLevel) {
        m130HzLevel = l130HzLevel;
    }

    public void set320HzLevel(int l320HzLevel) {
        m320HzLevel = l320HzLevel;
    }

    public void set800HzLevel(int l800HzLevel) {
        m800HzLevel = l800HzLevel;
    }

    public void set2kHzLevel(int l2kHzLevel) {
        m2kHzLevel = l2kHzLevel;
    }

    public void set5kHzLevel(int l5kHzLevel) {
        m5kHzLevel = l5kHzLevel;
    }

    public void set12kHzLevel(int l12kHzLevel) {
        m12kHzLevel = l12kHzLevel;
    }

    public void setVirtualizerLevel(short virtualizerLevel) {
        mVirtualizerLevel = virtualizerLevel;
    }

    public void setBassBoostLevel(short bassBoostLevel) {
        mBassBoostLevel = bassBoostLevel;
    }

    public void setReverbSetting(short reverbSetting) {
        mReverbSetting = reverbSetting;
    }

    public void setmIsEqualizerEnabled(boolean isEqualizerEnabled){
        mIsEqualizerEnabled = isEqualizerEnabled;
    }
}