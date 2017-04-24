package yasuaki.kyoto.com.simpledialog.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javax.inject.Inject;
import yasuaki.kyoto.com.simpledialog.MvpApplication;
import yasuaki.kyoto.com.simpledialog.R;
import yasuaki.kyoto.com.simpledialog.di.component.ActivityComponent;
import yasuaki.kyoto.com.simpledialog.di.component.DaggerActivityComponent;
import yasuaki.kyoto.com.simpledialog.di.module.ActivityModule;
import yasuaki.kyoto.com.simpledialog.transition.FabTransForm;

public class MainActivity extends AppCompatActivity implements MvpView {

  @Inject
  MvpPresenter<MvpView> mPresenter;

  public MainActivity() {
  }

  @BindView(R.id.tv_what_number_am_i)
  TextView mTvNumber;
  @BindView(R.id.btn_show_dialog)
  Button mBtnShowDialog;
  @BindView(R.id.btn_paint_it_pink)
  Button mBtnPaintItPink;
  @BindView(R.id.img_open_the_pink_block)
  ImageView mImgBtnOpenPink;
  @BindView(R.id.pink_layout)
  View mPinkLayout;

  @BindView(R.id.main_layout_container)
  ViewGroup mSceneRoot;
  @BindView(R.id.weapons_container)
  View mWeaponsContainer;
  @BindView(R.id.weapons)
  View mWeapons;
  @BindView(R.id.floatingActionButton)
  FloatingActionButton mFab1;

  @BindView(R.id.armoury)
  View mArmoury;
  @BindView(R.id.fab2)
  FloatingActionButton mFab2;

  private static final int HORIZONTAL_FACTOR = 2;
  private float diff;
  private ActivityComponent mActivityComponent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    mActivityComponent = DaggerActivityComponent.builder()
        .activityModule(new ActivityModule(this))
        .applicationComponent(((MvpApplication) getApplication()).getComponent())
        .build();

    getActivityComponent().inject(this);
    mPresenter.onAttachView(this);

    getGlobalLayoutLocation();

    changeFabMode1(false, false);
  }

  public ActivityComponent getActivityComponent() {
    return mActivityComponent;
  }

  private void getGlobalLayoutLocation() {
    // OnGlobalLayoutListener を使って、描画後に各Viewの座標を取得
    mSceneRoot.getViewTreeObserver().addOnGlobalLayoutListener(
        new OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            int[] weaponsLocation = new int[2];
            mWeaponsContainer.getLocationInWindow(weaponsLocation);

            int[] fabLocation = new int[2];
            mFab1.getLocationInWindow(fabLocation);

            diff = (weaponsLocation[1] + mWeaponsContainer.getHeight() / 2)
                - (fabLocation[1] + mFab1.getHeight() / 2);

            final float pivotX = fabLocation[0] + mFab1.getWidth() / 2 - weaponsLocation[0]
                - diff * HORIZONTAL_FACTOR;
            mWeaponsContainer.setPivotX(pivotX);
            mWeapons.setPivotX(pivotX);

            mSceneRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          }
        });
  }

  @Override
  public void onBackPressed() {
    if (mFab1.getVisibility() != View.VISIBLE) {
      changeFabMode1(false, true);
      return;
    }
    super.onBackPressed();
  }

  /**
   * ダイアログの生成 →表示
   */
  @OnClick(R.id.btn_show_dialog)
  void onShowDialogClicked() {

    View dialogLayout = View.inflate(this, R.layout.custom_dialog, null);
    Dialog dialog = new Dialog(MainActivity.this);
    dialog.setContentView(dialogLayout);

    TextView firstText = (TextView) dialog.findViewById(R.id.textView);
    TextView secondText = (TextView) dialog.findViewById(R.id.textView2);
    TextView thirdText = (TextView) dialog.findViewById(R.id.textView3);

    setClickListenerToChangeText(firstText);
    setClickListenerToChangeText(secondText);
    setClickListenerToChangeText(thirdText);
    dialog.show();
  }

  /**
   * ダイアログの生成 →表示 ＋ アニメーション効果
   */
  @OnClick(R.id.btn_paint_it_pink)
  void OnSecondButtonClicked() {
    final View dialogView = View.inflate(this, R.layout.paint_the_world_dialog, null);
    final Dialog dialog = new Dialog(MainActivity.this, R.style.MyAlertDialogStyle);
    dialog.setContentView(dialogView);

    ImageView imgXClose = (ImageView) dialog.findViewById(R.id.closeDialogImg);
    imgXClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        animRevealDialogPink(dialogView, false, dialog);
      }
    });

    dialog.setOnShowListener(new OnShowListener() {
      @Override
      public void onShow(DialogInterface dialogInterface) {
        animRevealDialogPink(dialogView, true, null);
      }
    });

    dialog.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          animRevealDialogPink(dialogView, false, dialog);
          return true;
        }
        return false;
      }
    });

    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.show();
  }

  @OnClick(R.id.img_open_the_pink_block)
  void onImageClicked() {
    animRevealPartiallyPink(true);

  }

  @OnClick(R.id.pink_layout)
  void onImageInvisibleClicked() {
    animRevealPartiallyPink(false);
  }

  @OnClick(R.id.floatingActionButton)
  void onClickedFirstFab() {
    changeFabMode1(true, true);
  }

  @OnClick(R.id.droid1)
  void onDroidClicked() {
    changeFabMode1(false, true);
  }


  @OnClick(R.id.fab2)
  void onClickedSecondFab() {
    changeFabMode2(true, true);
  }

  @OnClick(R.id.droid2)
  void onDroid2Clicked() {
    changeFabMode2(false, false);
  }


  /**
   * 円状にView の visibility を変化させる
   */
  private void animRevealPartiallyPink(boolean shouldAnimate) {
    int width = mPinkLayout.getWidth();
    int height = mPinkLayout.getHeight();
    int endRadius = (int) Math.hypot(width, height);

    int centerX = (int) (mImgBtnOpenPink.getX());
    int centerY = (int) (mImgBtnOpenPink.getY() + (mImgBtnOpenPink.getHeight()));
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      if (shouldAnimate) {
        // $2, $3 引数は、ターゲットからの相対位置のため、
        // ターゲットがスクリーン全体を覆うViewの場合 →アニメーション元の絶対位置でいい
        // ターゲットが一部だけを覆う場合 →X は絶対位置、Yは要調整
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(mPinkLayout, centerX, 50, 0, endRadius);
        mPinkLayout.setVisibility(View.VISIBLE);
        revealAnimator.setDuration(700);
        revealAnimator.start();
      } else {
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(mPinkLayout, centerX, 50, endRadius, 0);

        revealAnimator.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mPinkLayout.setVisibility(View.INVISIBLE);
          }
        });
        revealAnimator.setDuration(700);
        revealAnimator.start();
      }
    }
  }

  /**
   * ダイアログを表示させる際に、
   * 円状に visibility を変化させる
   */
  private void animRevealDialogPink(View dialogView, boolean shouldAnimate, final Dialog dialog) {

    final View animationTarget = dialogView.findViewById(R.id.dialog_r_layout);

    int width = animationTarget.getWidth();
    int height = animationTarget.getHeight();

    int endRadius = (int) Math.hypot(width, height);

    int centerX = (int) (mBtnPaintItPink.getX() + (mBtnPaintItPink.getWidth() / 2));
    int centerY = (int) (mBtnPaintItPink.getY() + (mBtnPaintItPink.getHeight()) + 56);

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

  private void changeFabMode1(boolean doesTransformed, boolean shouldAnimate) {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && shouldAnimate) {
      final TransitionSet transition = new FabTransForm(doesTransformed, mFab1.getHeight() / 2f);
      TransitionManager.beginDelayedTransition(mSceneRoot, transition);
    } else {

    }
    Log.d("Main", "" + shouldAnimate);

    final float baseMargin = getResources().getDimension(R.dimen.fab_margin);
    final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mFab1
        .getLayoutParams();
    layoutParams.bottomMargin = (int) (baseMargin - (doesTransformed ? diff : 0));
    layoutParams
        .setMarginEnd((int) (baseMargin + (doesTransformed ? diff * HORIZONTAL_FACTOR : 0)));
    mFab1.setLayoutParams(layoutParams);

    mWeaponsContainer.setVisibility(doesTransformed ? View.VISIBLE : View.INVISIBLE);
    mWeapons.setVisibility(doesTransformed ? View.VISIBLE : View.INVISIBLE);
    mWeapons.setScaleX(doesTransformed ? 1f : 0.8f);
    mFab1.setVisibility(doesTransformed ? View.INVISIBLE : View.VISIBLE);
  }

  private void setClickListenerToChangeText(final TextView tv) {
    tv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showToast(tv.getText().toString());
        mTvNumber.setText(tv.getText().toString());
      }
    });
  }

  private void changeFabMode2(boolean doesTransformed, boolean shouldAnimate) {

    int width = mArmoury.getWidth();
    int height = mArmoury.getHeight();
    int endWidth = (int) (Math.hypot(width, height));

    int centerX = (int) (mFab2.getX());
    int centerY = (int) (mFab2.getY() + (mFab2.getHeight()));
    Log.d("Main", "" + shouldAnimate);
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      if (shouldAnimate) {
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(mArmoury, centerX, 0, 0, endWidth);
        mFab2.setVisibility(View.INVISIBLE);
        mArmoury.setVisibility(View.VISIBLE);
        revealAnimator.setDuration(350);
        revealAnimator.start();
      } else {
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(mArmoury, centerX, 0, endWidth, 0);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mArmoury.setVisibility(View.INVISIBLE);
            mFab2.setVisibility(View.VISIBLE);
          }
        });
        revealAnimator.setDuration(400);
        revealAnimator.start();
      }
    }
  }

  private void showToast(String s) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
  }
}
