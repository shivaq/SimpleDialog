package yasuaki.kyoto.com.simpledialog.ui.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * XML ファイルなど不要不要
 */
public class MyBuiltDialog extends DialogFragment {

  // DialogFragment に限らず、Fragment はコンストラクタなどで引数を渡そうとすると
  // 落ちる。空のコンストラクタを必ず定義し、
  // newInstance() などを作って、そこで受け取るようにすること。
  public MyBuiltDialog() {
  }

  public static MyBuiltDialog newInstance() {
    return new MyBuiltDialog();
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    AlertDialog.Builder builder = new Builder(getActivity());
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
    return builder.create();
  }

}
