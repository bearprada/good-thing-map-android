package goodthingmap.android.prada.lab.goodthingmap;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.amplitude.api.Amplitude;
import com.flurry.android.FlurryAgent;

import com.parse.Parse;
import com.parse.ParseAnalytics;

/**
 * Created by prada on 2014/7/5.
 */
public abstract class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        Amplitude.initialize(this, "8db80f23cee61cdc8b0357f3d86a8292");
        Parse.initialize(this, "vRgutJGNaqr9sgjrCp5z6ltoyOr47QOqkiwQD9q2", "JKY8xoJeJa1vEK7h4dma6aJuTZrWm00E589BTwFC");
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        FlurryAgent.onStartSession(this, "D7N4PTHF6BJZK2BBRKFV");
    }

    @Override
    public void onResume() {
        super.onResume();
        Amplitude.startSession();
    }

    @Override
    public void onPause() {
        super.onPause();
        Amplitude.endSession();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }


}
