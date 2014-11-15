package goodthingmap.android.prada.lab.goodthingmap.util;

import android.content.Context;

import com.amplitude.api.Amplitude;
import com.flurry.android.FlurryAgent;

/**
 * Created by prada on 2014/11/15.
 */
public class LogEventUtils {
    public static void startSession(Context ctx) {
        FlurryAgent.onStartSession(ctx, "D7N4PTHF6BJZK2BBRKFV");
        FlurryAgent.onPageView();
        Amplitude.startSession();
    }

    public static void stopSession(Context ctx) {
        FlurryAgent.onEndSession(ctx);
        Amplitude.endSession();
    }

    public static void init(Context ctx) {
        Amplitude.initialize(ctx, "8db80f23cee61cdc8b0357f3d86a8292");
    }

    public static void sendEvent(String name) {
        FlurryAgent.logEvent(name);
        Amplitude.logEvent(name);
    }
}
