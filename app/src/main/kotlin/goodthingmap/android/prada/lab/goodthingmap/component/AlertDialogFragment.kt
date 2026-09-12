package goodthingmap.android.prada.lab.goodthingmap.component

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.AlertDialogWrapper

class AlertDialogFragment : DialogFragment() {
    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_POSITIVE_BUTTON_TITLE = "positive_button_title"
        private const val KEY_NEGATIVE_BUTTON_TITLE = "negative_button_title"

        @JvmStatic
        fun newInstance(
            title: String?,
            message: String?,
            positiveButtonTitle: String?,
            positiveListener: DialogInterface.OnClickListener
        ): AlertDialogFragment = newInstance(
            title, message, positiveButtonTitle, positiveListener, null, null
        )

        @JvmStatic
        fun newInstance(
            title: String?,
            message: String?,
            positiveButtonTitle: String?,
            positiveListener: DialogInterface.OnClickListener?,
            negativeButtonTitle: String?,
            negativeListener: DialogInterface.OnClickListener?
        ): AlertDialogFragment = AlertDialogFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_TITLE, title)
                putString(KEY_MESSAGE, message)
                putString(KEY_POSITIVE_BUTTON_TITLE, positiveButtonTitle)
                putString(KEY_NEGATIVE_BUTTON_TITLE, negativeButtonTitle)
            }
            this.positiveListener = positiveListener
            this.negativeListener = negativeListener
        }
    }

    private var positiveListener: DialogInterface.OnClickListener? = null
    private var negativeListener: DialogInterface.OnClickListener? = null

    fun setPositiveListener(listener: DialogInterface.OnClickListener?) {
        positiveListener = listener
    }

    fun setNegativeListener(listener: DialogInterface.OnClickListener?) {
        negativeListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val builder = AlertDialogWrapper.Builder(requireActivity())
            .setPositiveButton(args.getString(KEY_POSITIVE_BUTTON_TITLE).orEmpty(), positiveListener)
            .setCancelable(false)
        args.getString(KEY_NEGATIVE_BUTTON_TITLE)?.takeUnless(TextUtils::isEmpty)?.let {
            builder.setNegativeButton(it, negativeListener)
        }
        args.getString(KEY_TITLE)?.takeUnless(TextUtils::isEmpty)?.let(builder::setTitle)
        args.getString(KEY_MESSAGE)?.takeUnless(TextUtils::isEmpty)?.let(builder::setMessage)
        return builder.create()
    }
}
