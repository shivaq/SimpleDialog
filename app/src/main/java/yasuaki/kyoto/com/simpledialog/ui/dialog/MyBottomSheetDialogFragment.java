package yasuaki.kyoto.com.simpledialog.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import yasuaki.kyoto.com.simpledialog.R;

/**
 * Created by Yasuaki on 2017/04/24.
 */

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

  // DialogFragment に限らず、Fragment はコンストラクタなどで引数を渡そうとすると
  // 落ちる。空のコンストラクタを必ず定義し、
  // newInstance() などを作って、そこで受け取るようにすること。
  public MyBottomSheetDialogFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_bottom_sheet, container);
  }
}
