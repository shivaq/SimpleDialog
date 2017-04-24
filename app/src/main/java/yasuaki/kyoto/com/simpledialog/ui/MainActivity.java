package yasuaki.kyoto.com.simpledialog.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
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
import android.support.v4.app.FragmentManager;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.Calendar;
import javax.inject.Inject;
import yasuaki.kyoto.com.simpledialog.MvpApplication;
import yasuaki.kyoto.com.simpledialog.R;
import yasuaki.kyoto.com.simpledialog.di.component.ActivityComponent;
import yasuaki.kyoto.com.simpledialog.di.component.DaggerActivityComponent;
import yasuaki.kyoto.com.simpledialog.di.module.ActivityModule;
import yasuaki.kyoto.com.simpledialog.transition.FabTransForm;
import yasuaki.kyoto.com.simpledialog.ui.dialog.DatePickerFragment;
import yasuaki.kyoto.com.simpledialog.ui.dialog.MyBottomSheetDialogFragment;
import yasuaki.kyoto.com.simpledialog.ui.dialog.MyBuiltDialog;
import yasuaki.kyoto.com.simpledialog.ui.dialog.MyXbasedDFragment;
import yasuaki.kyoto.com.simpledialog.ui.dialog.MyXbasedDFragment.EditTextDialogListener;

public class MainActivity extends AppCompatActivity implements MvpView,
    EditTextDialogListener, DatePickerDialog.OnDateSetListener {

  @Inject
  MvpPresenter<MvpView> mPresenter;

  public MainActivity() {
  }

  @BindView(R.id.tv_what_number_am_i)
  TextView mTvNumber;
  @BindView(R.id.btn_reveal_dialog)
  Button mBtnRevealDialog;
  @BindView(R.id.img_btn_open_block)
  ImageView mImgBtnOpenBlock;
  @BindView(R.id.opened_block)
  View opendBlockLayout;

  @BindView(R.id.main_layout_container)
  ViewGroup mSceneRoot;
  @BindView(R.id.neat_weapons_container)
  View neatWeaponsContainer;
  @BindView(R.id.neat_weapons)
  View neatWeapons;
  @BindView(R.id.neat_fab)
  FloatingActionButton neatFab;

  @BindView(R.id.simple_weapons_container)
  View simpleWeaponsContainer;
  @BindView(R.id.simple_fab)
  FloatingActionButton simpleFab;

  private static final int HORIZONTAL_FACTOR = 2;
  private float yPositionDiff;
  private ActivityComponent mActivityComponent;
  private Dialog dialog;

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

    animRevealNeatWeapons(false, false);
  }

  public ActivityComponent getActivityComponent() {
    return mActivityComponent;
  }

  // 描画時座標Listenerの追加、計算、削除をこなす
  private void getGlobalLayoutLocation() {
    // OnGlobalLayoutListener を使って、描画後に各Viewの座標を取得
    mSceneRoot.getViewTreeObserver().addOnGlobalLayoutListener(
        new OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            int[] weaponsLocation = new int[2];
            // 拡張する帯（アニメーション後コンポーネント）の座標(x, y)取得
            neatWeaponsContainer.getLocationInWindow(weaponsLocation);

            // Fab(アニメーション元コンポーネント）の座標(x, y)取得
            int[] fabLocation = new int[2];
            neatFab.getLocationInWindow(fabLocation);

            // アニメーション元とアニメーション先との Y の差を計算
            // y座標 + コンポーネントの高さ/2
            yPositionDiff = (weaponsLocation[1] + neatWeaponsContainer.getHeight() / 2)
                - (fabLocation[1] + neatFab.getHeight() / 2);

            final float pivotX = fabLocation[0] + neatFab.getWidth() / 2// Fab の中心x座標計算
                - weaponsLocation[0]// 拡張する帯の左端の X 座標
                - yPositionDiff * HORIZONTAL_FACTOR;// モーションパスを計算にいれてる？
            neatWeaponsContainer.setPivotX(pivotX);// rotate|scale するポイント
            neatWeapons.setPivotX(pivotX);

            mSceneRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          }
        });
  }

  @Override
  public void onBackPressed() {
    if (neatFab.getVisibility() != View.VISIBLE) {
      animRevealNeatWeapons(false, true);
      return;
    }
    super.onBackPressed();
  }



  /**
   * ダイアログの生成 →表示 ＋ アニメーション効果
   * DialogFragment を継承させた RevealAllDialog を使ってのアニメーションには成功せず。
   *
   * DialogFragment を使わない場合、機種によっては落ちる可能性もあるため、
   * onDestroy() でダイアログを閉じさせる必要がある。
   */
  @OnClick(R.id.btn_reveal_dialog)
  void onRevealDBtnClicked() {


    final View dialogView = View.inflate(this, R.layout.paint_the_world_dialog, null);
    dialog = new Dialog(MainActivity.this, R.style.MyAlertDialogStyle);
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
    if(dialog.getWindow() == null){
      return;
    }

    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.show();
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (dialog != null) {
      dialog.dismiss();
    }
  }

  @OnClick(R.id.img_btn_open_block)
  void onImgBtnOpenBlockClicked() {
    animRevealBlock(true);

  }

  @OnClick(R.id.opened_block)
  void onOpenedBlockClicked() {
    animRevealBlock(false);
  }

  @OnClick(R.id.neat_fab)
  void onNeatFabClicked() {
    animRevealNeatWeapons(true, true);
  }

  @OnClick(R.id.droid_in_neat_weapons)
  void onNeatDroidClicked() {
    animRevealNeatWeapons(false, true);
  }


  @OnClick(R.id.simple_fab)
  void onSimpleFabClicked() {
    animRevealSimpleWeapons(true, true);
  }

  @OnClick(R.id.droid_in_simple_weapons)
  void onSimpleDroidClicked() {
    animRevealSimpleWeapons(false, false);
  }

  @OnClick(R.id.btn_open_input_field)
  void onInputDBtnClicked(){
    showEditDialog();
  }

  private void showEditDialog() {
    FragmentManager fm = getSupportFragmentManager();
    MyXbasedDFragment myXbasedDFragment = MyXbasedDFragment.newInstance("何か書いてください");
    myXbasedDFragment.show(fm, "fragment_xml_base");
  }

  @OnClick(R.id.btn_open_on_off_dialog)
  void onOnOffDBtnClicked(){
    FragmentManager fm = getSupportFragmentManager();
    MyBuiltDialog myBuiltDialog = MyBuiltDialog.newInstance();
    myBuiltDialog.show(fm, "fragment__onoff_dialog");
  }

  /**
   * 円状にView の visibility を変化させる
   */
  private void animRevealBlock(boolean shouldAnimate) {
    int width = opendBlockLayout.getWidth();
    int height = opendBlockLayout.getHeight();
    int endRadius = (int) Math.hypot(width, height);

    int centerX = (int) (mImgBtnOpenBlock.getX());
    int centerY = (int) (mImgBtnOpenBlock.getY() + (mImgBtnOpenBlock.getHeight()));
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      if (shouldAnimate) {
        // $2, $3 引数は、ターゲットからの相対位置のため、
        // ターゲットがスクリーン全体を覆うViewの場合 →アニメーション元の絶対位置でいい
        // ターゲットが一部だけを覆う場合 →X は絶対位置、Yは要調整
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(opendBlockLayout, centerX, 50, 0, endRadius);
        opendBlockLayout.setVisibility(View.VISIBLE);
        revealAnimator.setDuration(700);
        revealAnimator.start();
      } else {
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(opendBlockLayout, centerX, 50, endRadius, 0);

        revealAnimator.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            opendBlockLayout.setVisibility(View.INVISIBLE);
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

    int centerX = (int) (mBtnRevealDialog.getX() + (mBtnRevealDialog.getWidth() / 2));
    int centerY = (int) (mBtnRevealDialog.getY() + (mBtnRevealDialog.getHeight()) + 56);

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

  private void animRevealNeatWeapons(boolean doesTransformed, boolean shouldAnimate) {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && shouldAnimate) {
      final TransitionSet transition = new FabTransForm(doesTransformed, neatFab.getHeight() / 2f);
      TransitionManager.beginDelayedTransition(mSceneRoot, transition);

      final float baseMargin = getResources().getDimension(R.dimen.fab_margin);
      final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) neatFab
          .getLayoutParams();
      layoutParams.bottomMargin = (int) (baseMargin - (doesTransformed ? yPositionDiff : 0));
      layoutParams
          .setMarginEnd((int) (baseMargin + (doesTransformed ? yPositionDiff * HORIZONTAL_FACTOR : 0)));
      neatFab.setLayoutParams(layoutParams);

      neatWeaponsContainer.setVisibility(doesTransformed ? View.VISIBLE : View.INVISIBLE);
      neatWeapons.setVisibility(doesTransformed ? View.VISIBLE : View.INVISIBLE);
      neatWeapons.setScaleX(doesTransformed ? 1f : 0.8f);
      neatFab.setVisibility(doesTransformed ? View.INVISIBLE : View.VISIBLE);
    }
  }

  private void animRevealSimpleWeapons(boolean doesTransformed, boolean shouldAnimate) {

    int width = simpleWeaponsContainer.getWidth();
    int height = simpleWeaponsContainer.getHeight();
    int endWidth = (int) (Math.hypot(width, height));

    int centerX = (int) (simpleFab.getX());
    int centerY = (int) (simpleFab.getY() + (simpleFab.getHeight()));
    Log.d("Main", "" + shouldAnimate);
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      if (shouldAnimate) {
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(simpleWeaponsContainer, centerX, 0, 0, endWidth);
        simpleFab.setVisibility(View.INVISIBLE);
        simpleWeaponsContainer.setVisibility(View.VISIBLE);
        revealAnimator.setDuration(350);
        revealAnimator.start();
      } else {
        Animator revealAnimator = ViewAnimationUtils
            .createCircularReveal(simpleWeaponsContainer, centerX, 0, endWidth, 0);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            simpleWeaponsContainer.setVisibility(View.INVISIBLE);
            simpleFab.setVisibility(View.VISIBLE);
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

  @OnClick(R.id.btn_date_picker)
  void onDatePickerBtnClicked(){
    showDatePickerDialog();
  }
  public void showDatePickerDialog(){
    DatePickerFragment newFragment = new DatePickerFragment();
    newFragment.show(getSupportFragmentManager(), "datePicker");
  }

  @Override
  public void onFinishEditDialog(String inputText) {
    Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
    final Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    Toast.makeText(this, year + "/" + month + "/" + dayOfMonth, Toast.LENGTH_SHORT).show();
  }
  @OnClick(R.id.btn_from_bottom)
  void onBottomBtnClicked(){
    MyBottomSheetDialogFragment bottomSheetDialogFragment = new MyBottomSheetDialogFragment();
    FragmentManager fragmentManager = getSupportFragmentManager();
    bottomSheetDialogFragment.show(fragmentManager, "test");
  }
}
