package yasuaki.kyoto.com.simpledialog.transition;

import android.graphics.Path;
import android.transition.PathMotion;

public class FabTransitionPathMotion extends PathMotion {

  @Override
  public Path getPath(float startX, float startY, float endX, float endY) {

    float diffX = endX - startX;
    float diffY = endY - startY;

    Path path = new Path();
    path.moveTo(startX, startY);

    if (diffX < 0 && diffY > 0) {
      // 引数をもとにベジェ曲線を生成
      path.quadTo(startX, endY, endX, endY);
    } else {
      path.quadTo(endX, startY, endX, endY);
    }
    return path;
  }
}
