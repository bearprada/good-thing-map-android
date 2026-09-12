package goodthingmap.android.prada.lab.goodthingmap.component

import android.content.Intent
import android.os.Build
import android.provider.MediaStore

internal object ImagePickerIntentFactory {
    @JvmStatic
    fun actionForSdk(sdkInt: Int): String =
        if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            MediaStore.ACTION_PICK_IMAGES
        } else {
            Intent.ACTION_OPEN_DOCUMENT
        }

    @JvmStatic
    fun create(): Intent = Intent(actionForSdk(Build.VERSION.SDK_INT)).apply {
        type = "image/*"
        if (action == Intent.ACTION_OPEN_DOCUMENT) {
            addCategory(Intent.CATEGORY_OPENABLE)
        }
    }
}
