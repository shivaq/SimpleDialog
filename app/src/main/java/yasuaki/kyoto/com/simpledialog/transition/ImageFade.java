package yasuaki.kyoto.com.simpledialog.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.drawable.Drawable;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFade extends Visibility {

  public ImageFade() {
  }

  public ImageFade(int mode){
    setMode(mode);
  }

  @Override
  public Animator onAppear(ViewGroup sceneRoot, final View view, TransitionValues startValues,
      TransitionValues endValues) {

    view.setAlpha(0f);

    if(!(view instanceof ImageView)){
      return null;
    }

    final ImageView imgV = (ImageView)view;
    final Drawable drawable = imgV.getDrawable();

    if (drawable == null) {
      return null;
    }

    drawable.setAlpha(0);
    final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 255);
    valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        final int alpha = (int) animation.getAnimatedValue();
        drawable.setAlpha(alpha);
      }
    });
    valueAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        view.setAlpha(1f);
      }
    });
    return valueAnimator;
  }

  @Override
  public Animator onDisappear(ViewGroup sceneRoot, final View view, TransitionValues startValues,
      TransitionValues endValues) {

    if(!(view instanceof ImageView)){
      return null;
    }

    final ImageView imgV = (ImageView)view;
    final Drawable drawable = imgV.getDrawable();

    if (drawable == null) {
      return null;
    }

    final ValueAnimator valueAnimator = ValueAnimator.ofInt(255, 0);
    valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        final int alpha = (int)valueAnimator.getAnimatedValue();
        drawable.setAlpha(alpha);
      }
    });
    return valueAnimator;
  }
}
