package goodthingmap.android.prada.lab.goodthingmap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.GoodThingData;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.PermissionException;
import goodthingmap.android.prada.lab.goodthingmap.util.LogEventUtils;


public class HomeActivity extends BaseActivity {

    protected final static String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    protected final static int REQUEST_PERMISSION_GRANT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends BaseServiceFragment implements View.OnClickListener
            , LocationListener {
        private Location mCurrentLocation = null;
        private LocationManager lm;
        private ImageButton btn_location;
        private Animation animAlpha;

        public PlaceholderFragment() {
            super();
        }

        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            animAlpha = AnimationUtils.loadAnimation(getActivity(),R.anim.anim_alpha);
        }

        @Override
        public void onStart() {
            super.onStart();
            FlurryAgent.logEvent("PageHome", true);

        }

        @Override
        public void onStop() {
            super.onStop();
            FlurryAgent.endTimedEvent("PageHome");
        }

        public void onResume() {
            super.onResume();
            getCurrentLocation(false); // auto get location
        }

        public void onPause() {
            super.onPause();
            try {
                checkPermission(getActivity());
                lm.removeUpdates(this);
            } catch (PermissionException e) {
                e.printStackTrace();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);

            final TextView tvF = (TextView) view.findViewById(R.id.cover_text);
            final ImageView ivF = (ImageView) view.findViewById(R.id.cover_image);

            ivF.setOnClickListener(this);
            Task.callInBackground(new Callable<GoodThingData>() {
                @Override
                public GoodThingData call() throws Exception {
                    return mService.getTopStory();
                }
            }).onSuccess(new Continuation<GoodThingData, Object>() {
                @Override
                public Object then(Task<GoodThingData> task) throws Exception {
                    GoodThingData data = task.getResult();
                    tvF.setText(data.goodThing.getStory());
                    ivF.setTag(data.goodThing);
                    Picasso.with(getActivity()).load(data.goodThing.getImageUrl()).into(ivF, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                        }
                    });
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);
            view.findViewById(R.id.good_thing_01).setOnClickListener(this);
            view.findViewById(R.id.good_thing_02).setOnClickListener(this);
            view.findViewById(R.id.good_thing_03).setOnClickListener(this);
            view.findViewById(R.id.good_thing_04).setOnClickListener(this);
            view.findViewById(R.id.good_thing_05).setOnClickListener(this);
            view.findViewById(R.id.good_thing_06).setOnClickListener(this);
            btn_location = (ImageButton)view.findViewById(R.id.btnLocation);
            btn_location.setOnClickListener(this);
            return view;
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.cover_image:
                    Object tag = view.getTag();
                    if (tag != null && tag instanceof GoodThing) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra(GoodThing.EXTRA_GOODTHING, (GoodThing)tag);
                        intent.putExtra(GoodListActivity.EXTRA_LOCATION, mCurrentLocation);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(), view, getString(R.string.trans_cover_image));
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
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
                case R.id.btnLocation:
                    getCurrentLocation(true);
                    break;
            }
        }

        private void moveList(GoodThingType type) {
            Intent intent = new Intent(getActivity(), GoodListActivity.class);
            intent.putExtra(GoodListActivity.EXTRA_TYPE, type.ordinal());
            intent.putExtra(GoodListActivity.EXTRA_LOCATION, mCurrentLocation);
            startActivity(intent);
        }

        protected void checkPermission(Context context) throws PermissionException {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                throw new PermissionException("Location permission not granted");
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(REQUEST_PERMISSION_GRANT == requestCode) {
                for(int result : grantResults) {

                }
            }
        }

        private void getCurrentLocation(boolean userClick) {

            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!(isGPSEnabled || isNetworkEnabled)){
                if(userClick){
                    displayPromptForEnablingGPS(getActivity());
                }
            }
            else {
                try {
                    checkPermission(getActivity());

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
                    if(mCurrentLocation != null){
                        btn_location.setBackgroundResource(R.drawable.location_success);
                        btn_location.clearAnimation();
                    }else if(userClick){
                        btn_location.startAnimation(animAlpha);
                    }
                } catch (PermissionException e) {
                    e.printStackTrace();
                    ActivityCompat.requestPermissions(getActivity(), LOCATION_PERMISSIONS, REQUEST_PERMISSION_GRANT);
                }

            }
        }

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            if(mCurrentLocation != null){
                btn_location.clearAnimation();
                btn_location.setBackgroundResource(R.drawable.location_success);
            }
            try {
                checkPermission(getActivity());
            } catch (PermissionException e) {
                e.printStackTrace();
            }
            lm.removeUpdates(this);
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

        public enum GoodThingType {
            MAIN,
            SNACK,
            FRUIT,
            OTHER,
            TBI,
            NEAR;

            public int getTypeId() {
                return ordinal() + 1;
            }

            public String getName() {
                switch(this) {
                    case MAIN:
                        return "主食";
                    case SNACK:
                        return "小吃";
                    case FRUIT:
                        return "冰品/水果";
                    case OTHER:
                        return "其他";
                    case TBI:
                        return "大誌雜誌";
                    case NEAR:
                    default:
                        return "綜合搜尋";
                }
            }
        }
    }
}