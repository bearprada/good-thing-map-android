package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.GoodThingData;
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

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;


public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_my_fravor:
                intent = new Intent(this, FavorActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                        startActivity(intent);
                    }
                    break;
                case R.id.good_thing_01:
                    FlurryAgent.logEvent("Event_Click_Home_Main", false);
                    moveList(GoodThingType.MAIN);
                    break;
                case R.id.good_thing_02:
                    FlurryAgent.logEvent("Event_Click_Home_Snack", false);
                    moveList(GoodThingType.SNACK);
                    break;
                case R.id.good_thing_03:
                    FlurryAgent.logEvent("Event_Click_Home_Fruit", false);
                    moveList(GoodThingType.FRUIT);
                    break;
                case R.id.good_thing_04:
                    FlurryAgent.logEvent("Event_Click_Home_Other", false);
                    moveList(GoodThingType.OTHER);
                    break;
                case R.id.good_thing_05:
                    FlurryAgent.logEvent("Event_Click_Home_TBI", false);
                    moveList(GoodThingType.TBI);
                    break;
                case R.id.good_thing_06:
                    FlurryAgent.logEvent("Event_Click_Home_Near", false);
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
