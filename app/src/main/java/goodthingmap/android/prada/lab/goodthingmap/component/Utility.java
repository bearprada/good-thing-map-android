package goodthingmap.android.prada.lab.goodthingmap.component;

import android.content.Context;
import android.location.Location;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.util.DisplayMetrics;

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

    public static float convertDpToPixel(float dp, Context context){
        float px = dp * getDensity(context);
        return px;
    }

    public static float convertPixelToDp(float px, Context context){
        float dp = px / getDensity(context);
        return dp;
    }

    public static float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
}
