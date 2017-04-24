package yasuaki.kyoto.com.simpledialog.ui.dialog;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import java.util.Calendar;

/**
 * Created by Yasuaki on 2017/04/24.
 */

public class DatePickerFragment extends DialogFragment {

  // DialogFragment に限らず、Fragment はコンストラクタなどで引数を渡そうとすると
  // 落ちる。空のコンストラクタを必ず定義し、
  // newInstance() などを作って、そこで受け取るようにすること。
  public DatePickerFragment() {
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // do not import java.icu.utils.Calendar
    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    DatePickerDialog.OnDateSetListener dateSetListener =
        (OnDateSetListener) getActivity();

    return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
  }
}
