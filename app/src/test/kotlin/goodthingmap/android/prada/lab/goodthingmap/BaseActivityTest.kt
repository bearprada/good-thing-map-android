package goodthingmap.android.prada.lab.goodthingmap

import org.junit.Assert.assertTrue
import org.junit.Test

class BaseActivityTest {
    @Test
    fun apiAuthority_usesHttps() {
        assertTrue(BaseActivity.AUTHORITY.startsWith("https://"))
    }
}
