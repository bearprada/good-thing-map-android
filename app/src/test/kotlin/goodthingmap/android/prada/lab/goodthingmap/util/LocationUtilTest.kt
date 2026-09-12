package goodthingmap.android.prada.lab.goodthingmap.util

import org.junit.Assert.assertEquals
import org.junit.Test

class LocationUtilTest {
    @Test
    fun distanceText_formatsMetersAndKilometers() {
        assertEquals("50.0m", LocationUtil.distanceText(50f))
        assertEquals("1.5km", LocationUtil.distanceText(1500f))
    }
}
