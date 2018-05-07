package kwondeveloper.com.kwonplayer.SupportClasses;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

/**
 * Created by Mike on 3/27/2016.
 */
public class FadeAnimation extends AlphaAnimation {

    private View mView;
    private long mDuration;
    private float mFromAlpha;
    private float mToAlpha;
    private Interpolator mInterpolator;

    public FadeAnimation(View view, long duration, float fromAlpha, float toAlpha, Interpolator interpolator) {
        super(fromAlpha, toAlpha);
        mView = view;
        mDuration = duration;
        mFromAlpha = fromAlpha;
        mToAlpha = toAlpha;
        mInterpolator = interpolator;
    }

    //Performs the fade animation
    public void animate() {
        if (mView==null || mDuration==0 || mFromAlpha==mToAlpha) {return;}

        //Set the animation parameters
        if (mFromAlpha > mToAlpha){
            this.setAnimationListener(fadeOutListener);}
        else {
            this.setAnimationListener(fadeInListener);}

        this.setDuration(mDuration);
        if (mInterpolator!=null){this.setInterpolator(mInterpolator);}
        mView.startAnimation(this);
    }

    //Fade in animation listener
    private AnimationListener fadeInListener = new AnimationListener() {
        @Override
        public void onAnimationEnd(Animation arg0) {mView.setVisibility(View.VISIBLE);}

        @Override
        public void onAnimationRepeat(Animation arg0) {}

        @Override
        public void onAnimationStart(Animation arg0) {mView.setVisibility(View.INVISIBLE);}
    };

    //Fade out animation listener
    private AnimationListener fadeOutListener = new AnimationListener() {
        @Override
        public void onAnimationEnd(Animation arg0) {mView.setVisibility(View.INVISIBLE);}

        @Override
        public void onAnimationRepeat(Animation arg0) {}

        @Override
        public void onAnimationStart(Animation arg0) {mView.setVisibility(View.VISIBLE);}
    };
}