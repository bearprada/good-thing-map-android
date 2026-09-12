package goodthingmap.android.prada.lab.goodthingmap.util

import android.location.Location
import android.prada.lab.goodthingmap.model.GoodThing
import java.util.Locale

object LocationUtil {
    private const val UNKNOWN_DISTANCE = "無法得知距離"

    @JvmStatic
    fun calDistance(currentLocation: Location?, thing: GoodThing): String =
        currentLocation?.let { distanceText(it.distanceTo(thing.getLocation())) } ?: UNKNOWN_DISTANCE

    @JvmStatic
    fun distanceText(distanceMeters: Float): String =
        if (distanceMeters < 1000f) {
            String.format(Locale.US, "%.1fm", distanceMeters)
        } else {
            String.format(Locale.US, "%.1fkm", distanceMeters / 1000f)
        }
}
