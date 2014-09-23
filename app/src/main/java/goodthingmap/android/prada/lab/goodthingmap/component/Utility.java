package goodthingmap.android.prada.lab.goodthingmap.component;

import android.content.Context;
import android.location.Location;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.util.DisplayMetrics;

import com.parse.ParseGeoPoint;

/**
 * Created by prada on 2014/7/5.
 */
public class Utility {
    public static String calDistance(Location currentLocation, GoodThing thing) {
       return calDistance(currentLocation, thing.getLocation());
    }

    public static String calDistance(Location currentLocation, Location location) {
        if ( currentLocation == null ){
            return "無法得知距離";
        }
        float distance = currentLocation.distanceTo(location);
        return (distance < 1000f) ? String.format("%.1fm", distance) : String.format("%.1fkm", distance / 1000);
    }

}
