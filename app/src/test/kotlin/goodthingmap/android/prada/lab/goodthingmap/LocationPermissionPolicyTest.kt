package goodthingmap.android.prada.lab.goodthingmap

import android.content.pm.PackageManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationPermissionPolicyTest {
    @Test
    fun coarsePermissionIsEnoughForLocationFeatures() {
        assertTrue(
            LocationPermissionPolicy.hasLocationPermission(
                PackageManager.PERMISSION_DENIED,
                PackageManager.PERMISSION_GRANTED
            )
        )
    }

    @Test
    fun deniedPermissionsRequireRequest() {
        assertFalse(
            LocationPermissionPolicy.hasLocationPermission(
                PackageManager.PERMISSION_DENIED,
                PackageManager.PERMISSION_DENIED
            )
        )
    }
}
