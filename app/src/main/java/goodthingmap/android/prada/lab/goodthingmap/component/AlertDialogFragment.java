package goodthingmap.android.prada.lab.goodthingmap.component;

/**
 * Created by prada on 2014/7/5.
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;

public class AlertDialogFragment extends DialogFragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_POSITIVE_BUTTON_TITLE = "positive_button_title";
    private static final String KEY_NEGATIVE_BUTTON_TITLE = "negative_button_title";

    private DialogInterface.OnClickListener mPositiveListener;
    private DialogInterface.OnClickListener mNegativeListener;

    /**
     * Creates a new instance of AlertDialogFragment.
     * @param title string. The dialog won't have title if it's empty.
     * @param message string. The dialog won't have message if it's empty.
     * @param positiveButtonTitle Positive button title string. Required.
     * @param positiveListener Listener for the positive button. Required.
     * @return
     */
    public static AlertDialogFragment newInstance(String title,
                                                  String message,
                                                  String positiveButtonTitle,
                                                  DialogInterface.OnClickListener positiveListener) {
        return newInstance(title, message, positiveButtonTitle, positiveListener,
                null, null);
    }

    /**
     * Creates a new instance of AlertDialogFragment.
     * @param title string. The dialog won't have title if it's empty.
     * @param message string. The dialog won't have message if it's empty.
     * @return
     */
    public static AlertDialogFragment newInstance(String title,
                                                  String message,
                                                  String positiveButtonTitle,
                                                  DialogInterface.OnClickListener positiveListener,
                                                  String negativeButtonTitle,
                                                  DialogInterface.OnClickListener negativeListener) {
        AlertDialogFragment fragment = new AlertDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_MESSAGE, message);
        bundle.putString(KEY_POSITIVE_BUTTON_TITLE, positiveButtonTitle);
        bundle.putString(KEY_NEGATIVE_BUTTON_TITLE, negativeButtonTitle);
        fragment.setArguments(bundle);

        fragment.setPositiveListener(positiveListener);
        fragment.setNegativeListener(negativeListener);

        return fragment;
    }

    public void setPositiveListener(DialogInterface.OnClickListener mListener) {
        this.mPositiveListener = mListener;
    }

    public void setNegativeListener(DialogInterface.OnClickListener mListener) {
        this.mNegativeListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(KEY_TITLE);
        String message = args.getString(KEY_MESSAGE);
        String positiveButtonTitle = args.getString(KEY_POSITIVE_BUTTON_TITLE);

        Builder builder = new AlertDialogWrapper.Builder(getActivity())
                .setPositiveButton(positiveButtonTitle, mPositiveListener)
                .setCancelable(false);

        String negativeButtonTitle = args.getString(KEY_NEGATIVE_BUTTON_TITLE);
        if (!TextUtils.isEmpty(negativeButtonTitle)) {
            builder.setNegativeButton(negativeButtonTitle, mNegativeListener);
        }
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        return builder.create();
    }
}

