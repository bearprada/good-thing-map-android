package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.GoodThingsData;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flurry.android.FlurryAgent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.GoodThingAdapter;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoodListActivity extends BaseActivity {

    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_LOCATION = "extra_location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends BaseServiceFragment
            implements GoodThingAdapter.GoodThingItemListener {

        private HomeActivity.PlaceholderFragment.GoodThingType mType;
        private GoodThingAdapter mAdapter;
        private Location mLocation;

        public PlaceholderFragment() {
            super();
        }

        @Override
        public void onStart() {
            super.onStart();
            FlurryAgent.logEvent("PageGoodList", true);
        }

        @Override
        public void onStop() {
            super.onStop();
            FlurryAgent.endTimedEvent("PageGoodList");
        }

        @Override
        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            int typeId = getArguments().getInt(EXTRA_TYPE, 0);
            mType = HomeActivity.PlaceholderFragment.GoodThingType.values()[typeId];
            mLocation = getArguments().getParcelable(EXTRA_LOCATION);
            getActivity().setTitle(mType.getName());

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_good_list, container, false);
            mAdapter = new GoodThingAdapter(getActivity(), this);
            mAdapter.setLocation(mLocation);
            RecyclerView rv = rootView.findViewById(R.id.list_view);
            LinearLayoutManager lm = new LinearLayoutManager(getActivity());
            lm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(lm);
            rv.setAdapter(mAdapter);

            Observable<GoodThingsData> obs;
            if (mLocation == null) {
                // FIXME refactor this later
                if (mType == HomeActivity.PlaceholderFragment.GoodThingType.NEAR)
                    obs = mService.listStory(mLocation.getLatitude(), mLocation.getLongitude());
                else
                    obs = mService.listStory(mType.getTypeId(), mLocation.getLatitude(), mLocation.getLongitude());
            } else {
                if (mType == HomeActivity.PlaceholderFragment.GoodThingType.NEAR)
                    obs = mService.listStory();
                else
                    obs = mService.listStory(mType.getTypeId());
            }
            obs.subscribe(new Consumer<GoodThingsData>() {
                @Override
                public void accept(GoodThingsData data) throws Exception {
                    List<GoodThing> goodThings = data.getGoodThingList();

                    // temp: sorted by distance
                    if(mLocation != null) {
                        Collections.sort(goodThings, new Comparator<GoodThing>() {
                            @Override
                            public int compare(GoodThing goodThing, GoodThing goodThing2) {
                                float dist = mLocation.distanceTo(goodThing.getLocation());
                                float dist2 = mLocation.distanceTo(goodThing2.getLocation());
                                return (int) (dist - dist2);
                            }
                        });
                    }

                    mAdapter.addAll(goodThings);
                    mAdapter.notifyDataSetChanged();
                }
            });

            return rootView;
        }

        @Override
        public void onItemClick(View view, GoodThing item) {
            if (item != null) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(GoodThing.EXTRA_GOODTHING, item);
                intent.putExtra(EXTRA_LOCATION, mLocation);
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(getActivity(),
                                new Pair<>(view.findViewById(R.id.list_image_view), getString(R.string.trans_cover_image)),
                                new Pair<>(view.findViewById(R.id.list_distance), getString(R.string.trans_distance)),
                                new Pair<>(view.findViewById(R.id.list_title), getString(R.string.trans_title)));
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        }

        @Override
        public void onFavorClick(View view, GoodThing goodthing) {
            // TODO
        }
    }
}
