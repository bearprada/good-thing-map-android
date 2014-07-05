package goodthingmap.android.prada.lab.goodthingmap.component;

import android.location.Location;
import android.prada.lab.goodthingmap.model.GoodThing;

/**
 * Created by prada on 2014/7/5.
 */
public class Utility {
    public static String calDistance(Location currentLocation, GoodThing thing) {
        if ( currentLocation == null ){
            return "無法得知距離";
        }
        float distance = currentLocation.distanceTo(thing.getLocation());
        return (distance < 1000f) ? String.format("%.1fm", distance) : String.format("%.1fkm", distance / 1000);
    }
}
