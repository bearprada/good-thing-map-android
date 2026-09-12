package goodthingmap.android.prada.lab.goodthingmap.component

import android.content.Intent
import android.provider.MediaStore
import org.junit.Assert.assertEquals
import org.junit.Test

class ImagePickerIntentFactoryTest {
    @Test
    fun actionForAndroid13AndLater_usesPhotoPicker() {
        assertEquals(
            MediaStore.ACTION_PICK_IMAGES,
            ImagePickerIntentFactory.actionForSdk(33)
        )
    }

    @Test
    fun actionBeforeAndroid13_usesOpenDocument() {
        assertEquals(
            Intent.ACTION_OPEN_DOCUMENT,
            ImagePickerIntentFactory.actionForSdk(32)
        )
    }
}
