package yasuaki.kyoto.com.simpledialog.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import yasuaki.kyoto.com.simpledialog.R;

public class MyXbasedDFragment extends DialogFragment implements OnEditorActionListener {

  private EditText editText;

  public MyXbasedDFragment() {
    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
  }

  public static MyXbasedDFragment newInstance(String title) {
    MyXbasedDFragment myXbasedDFragment = new MyXbasedDFragment();
    Bundle args = new Bundle();
    args.putString("title", title);
    myXbasedDFragment.setArguments(args);
    return myXbasedDFragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_edit_name, container);
  }

  @Override
  public void onResume() {
    super.onResume();

    if (getDialog().getWindow() == null) {
      return;
    }

    // スクリーンのサイズを取得
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    int dialogWidth = (int) (metrics.widthPixels * 0.8);
    int dialogHeight = (int) (metrics.heightPixels * 0.4);

    // Dialog の Window のパラメータを取得
    ViewGroup.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
    // パラメータの値をセット
    layoutParams.width = dialogWidth;
    layoutParams.height = dialogHeight;

    // ダイアログのパラメータに、上記をセット
    getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) layoutParams);


  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (getDialog().getWindow() == null) {
      return;
    }

    String title = getArguments().getString("title", "Enter Name");
    getDialog().setTitle(title);

    editText = (EditText) view.findViewById(R.id.txt_your_name);
    editText.requestFocus();

    getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    editText.setOnEditorActionListener(this);
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if(EditorInfo.IME_ACTION_DONE == actionId){
      EditTextDialogListener dialogListener = (EditTextDialogListener) getActivity();
      dialogListener.onFinishEditDialog(editText.getText().toString());
      dismiss();
      return true;
    }
    return false;
  }

  public interface EditTextDialogListener{
    void onFinishEditDialog(String inputText);

    // Fragment から起動したダイアログが、Fragment に値を渡す時は、
//    https://guides.codepath.com/android/Using-DialogFragment#passing-data-to-parent-fragment
  }

}
