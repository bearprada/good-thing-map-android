package goodthingmap.android.prada.lab.goodthingmap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.GoodThingData;
import android.prada.lab.goodthingmap.model.GoodThingType;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import goodthingmap.android.prada.lab.goodthingmap.util.LogEventUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class HomeActivity extends BaseActivity implements View.OnClickListener, LocationListener {

    protected final static String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private Location mCurrentLocation = null;
    private LocationManager lm;
    private Animation animAlpha;

    protected final static int REQUEST_PERMISSION_GRANT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);

        final TextView tvF = findViewById(R.id.cover_text);
        final ImageView ivF = findViewById(R.id.cover_image);

        ivF.setOnClickListener(this);
        mService.getTopStory()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<GoodThingData>() {
                @Override
                public void accept(GoodThingData data) throws Exception {
                    tvF.setText(data.goodThing.getStory());
                    ivF.setTag(data.goodThing);
                    Picasso.with(getBaseContext())
                        .load(data.goodThing.getImageUrl())
                        .into(ivF);
                }
            });
        findViewById(R.id.good_thing_01).setOnClickListener(this);
        findViewById(R.id.good_thing_02).setOnClickListener(this);
        findViewById(R.id.good_thing_03).setOnClickListener(this);
        findViewById(R.id.good_thing_04).setOnClickListener(this);
        findViewById(R.id.good_thing_05).setOnClickListener(this);
        findViewById(R.id.good_thing_06).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.logEvent("PageHome", true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.endTimedEvent("PageHome");
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            checkPermission(this);
            lm.removeUpdates(this);
        } catch (IllegalStateException ignored) {}
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.cover_image:
                Object tag = view.getTag();
                if (tag != null && tag instanceof GoodThing) {
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra(GoodThing.EXTRA_GOODTHING, (GoodThing)tag);
                    intent.putExtra(GoodListActivity.EXTRA_LOCATION, mCurrentLocation);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this, view, getString(R.string.trans_cover_image));
                    ActivityCompat.startActivity(this, intent, options.toBundle());
                }
                break;
            case R.id.good_thing_01:
                LogEventUtils.sendEvent("Event_Click_Home_Main");
                moveList(GoodThingType.MAIN);
                break;
            case R.id.good_thing_02:
                LogEventUtils.sendEvent("Event_Click_Home_Snack");
                moveList(GoodThingType.SNACK);
                break;
            case R.id.good_thing_03:
                LogEventUtils.sendEvent("Event_Click_Home_Fruit");
                moveList(GoodThingType.FRUIT);
                break;
            case R.id.good_thing_04:
                LogEventUtils.sendEvent("Event_Click_Home_Other");
                moveList(GoodThingType.OTHER);
                break;
            case R.id.good_thing_05:
                LogEventUtils.sendEvent("Event_Click_Home_TBI");
                moveList(GoodThingType.TBI);
                break;
            case R.id.good_thing_06:
                LogEventUtils.sendEvent("Event_Click_Home_Near");
                moveList(GoodThingType.NEAR);
                break;
//            case R.id.btnLocation:
//                getCurrentLocation(true);
//                break;
        }
    }

    private void moveList(GoodThingType type) {
        Intent intent = new Intent(this, GoodListActivity.class);
        intent.putExtra(GoodListActivity.EXTRA_TYPE, type.ordinal());
        intent.putExtra(GoodListActivity.EXTRA_LOCATION, mCurrentLocation);
        startActivity(intent);
    }

    protected void checkPermission(Context context) {
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
                PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            throw new IllegalStateException("Location permission not granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(REQUEST_PERMISSION_GRANT == requestCode) {
            // TODO
        }
    }

    private void getCurrentLocation(boolean userClick) {

        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!(isGPSEnabled || isNetworkEnabled)){
            if(userClick){// user click R.id.btnLocation open gps_setting page
                displayPromptForEnablingGPS(this);
            }
        }
        else {
            try {
                checkPermission(this);

                if (isNetworkEnabled) {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
                    Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location != null)
                        mCurrentLocation  = location;
                }
                if (isGPSEnabled) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location != null)
                        mCurrentLocation  = location;
                }
            } catch (IllegalStateException e) {
                ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, REQUEST_PERMISSION_GRANT);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        try {
            checkPermission(this);
        } catch (IllegalStateException ignored) {}
        lm.removeUpdates(this);// stop update after get current location
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    public static void displayPromptForEnablingGPS(final Activity activity)
    {
        final MaterialDialog.Builder builder =  new MaterialDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;

        builder.content(R.string.warning_open_gps)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        activity.startActivity(new Intent(action));
                    }
                }).build();
        builder.show();
    }
}