package yasuaki.kyoto.com.simpledialog.transition;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.transition.Visibility;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import yasuaki.kyoto.com.simpledialog.R;

public class FabTransForm extends TransitionSet {

  public FabTransForm(boolean doesTransFormed, float startRadius) {

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      setOrdering(ORDERING_SEQUENTIAL);

      if (doesTransFormed) {
        final Transition fabTransition = new TransitionSet()
            .setDuration(100)
            .setOrdering(ORDERING_TOGETHER)
            .addTransition(new ChangeBounds())
            .addTransition(new ImageFade(Visibility.MODE_OUT))
            .setInterpolator(new AccelerateInterpolator());

        fabTransition.setPathMotion(new FabTransitionPathMotion());

        final TransitionSet toolsTransition = new TransitionSet()
            .setDuration(200)
            .setOrdering(ORDERING_TOGETHER)
            .addTransition(new CircularRevealTransition(Visibility.MODE_IN, startRadius))
            .addTransition(new ChangeTransform().addTarget(R.id.neat_weapons))
            .addTransition(new Fade(Fade.IN).addTarget(R.id.neat_weapons));

        addTransition(fabTransition)
            .addTransition(toolsTransition);
      } else {
        final Transition fabTransition = new TransitionSet()
            .setDuration(100)
            .setOrdering(ORDERING_TOGETHER)
            .addTransition(new ChangeBounds())
            .addTransition(new ImageFade(Visibility.MODE_IN))
            .setInterpolator(new DecelerateInterpolator());

        fabTransition.setPathMotion(new FabTransitionPathMotion());

        final TransitionSet toolsTransition = new TransitionSet()
            .setDuration(200)
            .setOrdering(ORDERING_TOGETHER)
            .addTransition(new CircularRevealTransition(Visibility.MODE_OUT, startRadius))
            .addTransition(new ChangeTransform().addTarget(R.id.neat_weapons))
            .addTransition(new Fade(Fade.OUT).addTarget(R.id.neat_weapons));

        addTransition(toolsTransition)
            .addTransition(fabTransition);
      }
    }

  }
}
