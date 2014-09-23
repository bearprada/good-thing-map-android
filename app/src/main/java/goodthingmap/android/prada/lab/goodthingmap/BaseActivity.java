package goodthingmap.android.prada.lab.goodthingmap;

import android.os.Bundle;
import android.prada.lab.goodthingmap.model.UserAddedVendor;
import android.prada.lab.goodthingmap.model.UserFavorite;
import android.prada.lab.goodthingmap.model.VendorComment;
import android.prada.lab.goodthingmap.model.VendorData;
import android.prada.lab.goodthingmap.model.VendorPhoto;
import android.support.v7.app.ActionBarActivity;

import com.amplitude.api.Amplitude;
import com.flurry.android.FlurryAgent;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Created by prada on 2014/7/5.
 */
public abstract class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        Amplitude.initialize(this, "8db80f23cee61cdc8b0357f3d86a8292");
        initializeParse();
        setupParseUser();
    }

    protected void initializeParse() {
        ParseObject.registerSubclass(VendorData.class);
        ParseObject.registerSubclass(VendorPhoto.class);
        ParseObject.registerSubclass(VendorComment.class);
        ParseObject.registerSubclass(UserFavorite.class);
        ParseObject.registerSubclass(UserAddedVendor.class);

        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        PushService.setDefaultPushCallback(this, HomeActivity.class);

        PushService.subscribe(this, "Dev", HomeActivity.class);
        PushService.subscribe(this, "Taipei", HomeActivity.class);

        ParseAnalytics.trackAppOpened(getIntent());
        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    protected void setupParseUser() {
        if(ParseUser.getCurrentUser() == null) {
            ParseUser.enableAutomaticUser();
            ParseUser.getCurrentUser().saveInBackground();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FlurryAgent.onStartSession(this, getString(R.string.flurry_app_id));
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
