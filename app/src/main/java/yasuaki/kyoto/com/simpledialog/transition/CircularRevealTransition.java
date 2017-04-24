package yasuaki.kyoto.com.simpledialog.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import java.util.ArrayList;

public class CircularRevealTransition extends Visibility {

  private final float startRadius;

  public CircularRevealTransition(float startRadius) {
    this.startRadius = startRadius;
  }

  public CircularRevealTransition(int mode, float startRadius) {
    this.startRadius = startRadius;
    setMode(mode);
  }

  @Override
  public Animator onAppear(ViewGroup sceneRoot, final View view, TransitionValues startValues,
      TransitionValues endValues) {

    float endRadius = (float) (Math.hypot(view.getWidth(), view.getHeight()));
    int centerX = (int) view.getPivotX();
    int centerY = (int) view.getPivotY();

    view.setAlpha(0f);
    final Animator circularRevealAnim = ViewAnimationUtils.createCircularReveal(
        view, centerX, centerY, startRadius, endRadius);
    circularRevealAnim.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        view.setAlpha(1f);
      }
    });
    return new NoPauseAnimator(circularRevealAnim);
  }

  @Override
  public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues,
      TransitionValues endValues) {
    float endRadius = (float) (Math.hypot(view.getWidth(), view.getHeight()));
    int centerX = (int) view.getPivotX();
    int centerY = (int) view.getPivotY();

    final Animator circularRevealAnim = ViewAnimationUtils.createCircularReveal(
        view, centerX, centerY, endRadius, startRadius);
    return new NoPauseAnimator(circularRevealAnim);
  }



  /********************* NoPauseAnimator *****************/
  private static class NoPauseAnimator extends Animator {

    private final Animator mOriginalAnimator;
    private final ArrayMap<AnimatorListener, AnimatorListenerWrapper> mListenerMap
        = new ArrayMap<>();

    NoPauseAnimator(Animator animator) {
      mOriginalAnimator = animator;
    }

    @Override
    public void addListener(AnimatorListener listener) {
      final AnimatorListenerWrapper listenerWrapper = new AnimatorListenerWrapper(this, listener);

      if(!mListenerMap.containsKey(listener)){
        mListenerMap.put(listener, listenerWrapper);
        mOriginalAnimator.addListener(listenerWrapper);
      }
    }

    @Override
    public void cancel() {
      mOriginalAnimator.cancel();
    }

    @Override
    public void end() {
      mOriginalAnimator.end();
    }

    @Override
    public TimeInterpolator getInterpolator() {
      return mOriginalAnimator.getInterpolator();
    }

    @Override
    public ArrayList<AnimatorListener> getListeners() {
      return new ArrayList<>(mListenerMap.keySet());
    }

    @Override
    public long getStartDelay() {
      return mOriginalAnimator.getStartDelay();
    }

    @Override
    public void setStartDelay(long startDelay) {
      mOriginalAnimator.setStartDelay(startDelay);
    }

    @Override
    public Animator setDuration(long duration) {
      mOriginalAnimator.setDuration(duration);
      return this;
    }

    @Override
    public long getDuration() {
      return mOriginalAnimator.getDuration();
    }

    @Override
    public void setInterpolator(TimeInterpolator value) {
      mOriginalAnimator.setInterpolator(value);
    }

    @Override
    public boolean isPaused() {
      return mOriginalAnimator.isPaused();
    }

    @Override
    public boolean isRunning() {
      return mOriginalAnimator.isRunning();
    }

    @Override
    public boolean isStarted() {
      return mOriginalAnimator.isStarted();
    }

    @Override
    public void removeAllListeners() {
      super.removeAllListeners();
      mListenerMap.clear();
      mOriginalAnimator.removeAllListeners();
    }

    @Override
    public void removeListener(AnimatorListener listener) {
      AnimatorListener wrapper = mListenerMap.get(listener);
      if(wrapper != null){
        mListenerMap.remove(listener);
        mOriginalAnimator.removeListener(wrapper);
      }
    }

    @Override
    public void setTarget(@Nullable Object target) {
      mOriginalAnimator.setTarget(target);
    }

    @Override
    public void setupEndValues() {
      mOriginalAnimator.setupEndValues();
    }

    @Override
    public void setupStartValues() {
      mOriginalAnimator.setupStartValues();
    }

    @Override
    public void start() {
      mOriginalAnimator.start();
    }

    /***************** AnimatorListenerWrapper ****************/
    static class AnimatorListenerWrapper implements Animator.AnimatorListener {

      private final Animator mAnimator;
      private final AnimatorListener mOriginalListener;

      public AnimatorListenerWrapper(Animator animator,
          AnimatorListener originalListener) {
        mAnimator = animator;
        mOriginalListener = originalListener;
      }

      @Override
      public void onAnimationStart(Animator animation) {
        mOriginalListener.onAnimationStart(mAnimator);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mOriginalListener.onAnimationEnd(mAnimator);
      }

      @Override
      public void onAnimationCancel(Animator animation) {
        mOriginalListener.onAnimationCancel(mAnimator);
      }

      @Override
      public void onAnimationRepeat(Animator animation) {
        mOriginalListener.onAnimationRepeat(mAnimator);
      }
    }
  }
}
