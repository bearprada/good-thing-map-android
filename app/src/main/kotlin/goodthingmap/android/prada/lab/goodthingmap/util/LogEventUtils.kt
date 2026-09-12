package goodthingmap.android.prada.lab.goodthingmap.util

import android.content.Context
import com.amplitude.api.Amplitude
import com.flurry.android.FlurryAgent

object LogEventUtils {
    @JvmStatic
    fun startSession(context: Context) {
        FlurryAgent.onStartSession(context, "D7N4PTHF6BJZK2BBRKFV")
        FlurryAgent.onPageView()
        Amplitude.startSession()
    }

    @JvmStatic
    fun stopSession(context: Context) {
        FlurryAgent.onEndSession(context)
        Amplitude.endSession()
    }

    @JvmStatic
    fun init(context: Context) {
        Amplitude.initialize(context, "8db80f23cee61cdc8b0357f3d86a8292")
    }

    @JvmStatic
    fun sendEvent(name: String) {
        FlurryAgent.logEvent(name)
        Amplitude.logEvent(name)
    }
}
