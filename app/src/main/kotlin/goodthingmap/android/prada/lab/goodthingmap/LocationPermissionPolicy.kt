package goodthingmap.android.prada.lab.goodthingmap

import android.content.pm.PackageManager

internal object LocationPermissionPolicy {
    @JvmStatic
    fun hasLocationPermission(finePermission: Int, coarsePermission: Int): Boolean =
        finePermission == PackageManager.PERMISSION_GRANTED ||
            coarsePermission == PackageManager.PERMISSION_GRANTED
}
