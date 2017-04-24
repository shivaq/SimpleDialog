package yasuaki.kyoto.com.simpledialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

public class MyXbasedDFragment extends DialogFragment {

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
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    editText = (EditText) view.findViewById(R.id.txt_your_name);
    String title = getArguments().getString("title", "Enter Name");
    getDialog().setTitle(title);
    editText.requestFocus();
    getDialog().getWindow().setSoftInputMode(
        LayoutParams.SOFT_INPUT_STATE_VISIBLE);
  }
}
