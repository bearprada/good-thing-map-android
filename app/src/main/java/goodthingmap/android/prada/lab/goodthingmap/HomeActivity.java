package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.GoodThingData;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import goodthingmap.android.prada.lab.goodthingmap.util.LogEventUtils;


public class HomeActivity extends BaseActivity {

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
        private Location mCurrentLocation;
        private LocationManager lm;
        private String provider;

        public PlaceholderFragment() {
            super();
        }

        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = lm.getBestProvider(criteria, false);
            mCurrentLocation = lm.getLastKnownLocation(provider);
            // lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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
            lm.requestLocationUpdates(provider, 10000, 0, this);
        }

        public void onPause() {
            super.onPause();
            lm.removeUpdates(this);
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
                        @Override public void onSuccess() {}
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
            }
        }

        private void moveList(GoodThingType type) {
            Intent intent = new Intent(getActivity(), GoodListActivity.class);
            intent.putExtra(GoodListActivity.EXTRA_TYPE, type.ordinal());
            intent.putExtra(GoodListActivity.EXTRA_LOCATION, mCurrentLocation);
            startActivity(intent);
        }

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
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
