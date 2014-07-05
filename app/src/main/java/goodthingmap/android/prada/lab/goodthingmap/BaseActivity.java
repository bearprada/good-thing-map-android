package goodthingmap.android.prada.lab.goodthingmap;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.amplitude.api.Amplitude;

/**
 * Created by prada on 2014/7/5.
 */
public abstract class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        Amplitude.initialize(this, "8db80f23cee61cdc8b0357f3d86a8292");
    }
    @Override
    public void onPause() {
        super.onPause();
        Amplitude.endSession();
    }

    @Override
    public void onResume() {
        super.onResume();
        Amplitude.startSession();
    }
}
