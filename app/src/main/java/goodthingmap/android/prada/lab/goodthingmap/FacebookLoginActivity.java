package goodthingmap.android.prada.lab.goodthingmap;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.UserSettingsFragment;
import com.flurry.android.FlurryAgent;

import goodthingmap.android.prada.lab.goodthingmap.BaseActivity;
import goodthingmap.android.prada.lab.goodthingmap.R;

/**
 * Created by 123 on 8/9/2014.
 */
public class FacebookLoginActivity extends BaseActivity {

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.logEvent("PageFacebookLogin", true);
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.endTimedEvent("PageFacebookLogin");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_facebook_login);

        FragmentManager fragmentManager = getSupportFragmentManager();
        UserSettingsFragment userSettingsFragment = (UserSettingsFragment)
                fragmentManager.findFragmentById(R.id.facebook_login_fragment);
        userSettingsFragment.setSessionStatusCallback(new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        });
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            finish();
        }
    }
}
