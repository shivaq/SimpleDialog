package yasuaki.kyoto.com.simpledialog.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import timber.log.Timber;
import yasuaki.kyoto.com.simpledialog.R;

/**
 * Created by Yasuaki on 2017/04/24.
 *
 * MainActivity の onRevealDBtnClicked のアニメーションを、
 * DialogFragment をコールすることで実現しようとしたが、
 * 下記エラーがでてしまう。
 * onResume に onShow を入れたのではすでに onShow 済みで、
 * その前に入れると、下記が出てしまい、結局断念
 * Cannot start this animator on a detached view
 */

public class RevealAllDialog extends DialogFragment {

  Dialog dialog;
  View dialogView;
  boolean isAttached;

  // DialogFragment に限らず、Fragment はコンストラクタなどで引数を渡そうとすると
  // 落ちる。空のコンストラクタを必ず定義し、
  // newInstance() などを作って、そこで受け取るようにすること。
  public RevealAllDialog() {
  }

  public static RevealAllDialog newInstance() {
    RevealAllDialog revealAllDialog = new RevealAllDialog();
    return revealAllDialog;
  }


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    dialogView = inflater.inflate(R.layout.paint_the_world_dialog, container);
    ButterKnife.bind(this, dialogView);
    isAttached = ViewCompat.isAttachedToWindow(dialogView);


    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {

      Timber.d("RevealAllDialog:onCreateView: %s", isAttached);
    }
    return dialogView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Timber.d("RevealAllDialog:onViewCreated: dialog is %s", isAttached);
    dialog.setOnShowListener(new OnShowListener() {
      @Override
      public void onShow(DialogInterface dialog) {
        Timber.d("RevealAllDialog:onShow: ");
        animRevealDialogPink(dialogView, true, null);
      }
    });
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    AlertDialog.Builder builder = new Builder(getActivity(), R.style.MyAlertDialogStyle);

    builder.setMessage("Are you sure?");
    builder.setPositiveButton("OK", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {

      }
    });
    builder.setNegativeButton("Cancel", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    dialog = builder.create();

    Timber.d("RevealAllDialog:onCreateDialog: %s", isAttached);

    return dialog;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    

  }

  @Override
  public void onStart() {
    super.onStart();
    Timber.d("RevealAllDialog:onStart: %s", isAttached);
//    dialog.setOnShowListener(new OnShowListener() {
//      @Override
//      public void onShow(DialogInterface dialog) {
//        Timber.d("RevealAllDialog:onShow: ");
//      }
//    });
//        dialog.setOnShowListener(new OnShowListener() {
//      @Override
//      public void onShow(DialogInterface dialogInterface) {
//        Timber.d("RevealAllDialog:onShow: ");
//        animRevealDialogPink(dialogView, true, null);
//      }
//    });



    //    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
  }

  @Override
  public void onResume() {
    super.onResume();
    Timber.d("RevealAllDialog:onResume: %s", isAttached);




  }





  //  @OnClick(R.id.closeDialogImg)
//  void onXClick(){
//    animRevealDialogPink(dialogView, false, dialog);
//  }





  /**
   * ダイアログを表示させる際に、
   * 円状に visibility を変化させる
   */
  private void animRevealDialogPink(View dialogView, boolean shouldAnimate, final Dialog dialog) {

    final View animationTarget = dialogView.findViewById(R.id.dialog_r_layout);
    final View animationOrigin = getActivity().findViewById(R.id.btn_reveal_dialog);

    int width = animationTarget.getWidth();
    int height = animationTarget.getHeight();

    int endRadius = (int) Math.hypot(width, height);

    int centerX = (int) (animationOrigin.getX() + (animationOrigin.getWidth() / 2));
    int centerY = (int) (animationOrigin.getY() + (animationOrigin.getHeight()) + 56);

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      if (shouldAnimate) {
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(animationTarget, centerX, centerY, 0, endRadius);

        animationTarget.setVisibility(View.VISIBLE);
        revealAnimator.setDuration(700);
        revealAnimator.start();
      } else {
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(animationTarget, centerX, centerY, endRadius, 0);

        revealAnimator.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            dialog.dismiss();
            animationTarget.setVisibility(View.INVISIBLE);
          }
        });
        revealAnimator.setDuration(700);
        revealAnimator.start();
      }
    }
  }
}