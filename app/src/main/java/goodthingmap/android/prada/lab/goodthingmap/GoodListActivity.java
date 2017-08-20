package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.GoodThingType;
import android.prada.lab.goodthingmap.model.GoodThingsData;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.flurry.android.FlurryAgent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import goodthingmap.android.prada.lab.goodthingmap.component.GTController;
import goodthingmap.android.prada.lab.goodthingmap.component.GTPlaceModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class GoodListActivity extends BaseActivity {

    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_LOCATION = "extra_location";

    private GoodThingType mType;
    private Location mLocation;
    private GTController mController = new GTController(new GTPlaceModel.GTClickListener() {
        @Override
        public void onPlaceClick(View view, long placeId) {
            GoodThing item = findPlaceById(placeId);
            if (item != null) {
                Intent intent = new Intent(GoodListActivity.this, DetailActivity.class);
                intent.putExtra(GoodThing.EXTRA_GOODTHING, item);
                intent.putExtra(EXTRA_LOCATION, mLocation);
                ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(GoodListActivity.this,
                        new Pair<>(view.findViewById(R.id.list_image_view), getString(R.string.trans_cover_image)),
                        new Pair<>(view.findViewById(R.id.list_distance), getString(R.string.trans_distance)),
                        new Pair<>(view.findViewById(R.id.list_title), getString(R.string.trans_title)));
                ActivityCompat.startActivity(GoodListActivity.this, intent, options.toBundle());
            }
        }

        @Override
        public void onFavorClick(View view, GoodThing goodthing) {}
    });

    private GoodThing findPlaceById(long placeId) {
        for(GoodThing place : mPlaces) {
            if (place.getId() == placeId) {
                return place;
            }
        }
        return null;
    }

    private List<GoodThing> mPlaces = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_good_list);
        int typeId = getIntent().getIntExtra(EXTRA_TYPE, 0);
        mType = GoodThingType.values()[typeId];

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mType.getName());


        mLocation = getIntent().getParcelableExtra(EXTRA_LOCATION);
        mController.setLocation(mLocation);
        RecyclerView rv = findViewById(R.id.list_view);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(lm);
        rv.setAdapter(mController.getAdapter());

        Observable<GoodThingsData> obs;
        if (mLocation == null) {
            // FIXME refactor this later
            if (mType == GoodThingType.NEAR)
                obs = mService.listStory(mLocation.getLatitude(), mLocation.getLongitude());
            else
                obs = mService.listStory(mType.getTypeId(), mLocation.getLatitude(), mLocation.getLongitude());
        } else {
            if (mType == GoodThingType.NEAR)
                obs = mService.listStory();
            else
                obs = mService.listStory(mType.getTypeId());
        }
        obs.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<GoodThingsData>() {
            @Override
            public void accept(GoodThingsData data) throws Exception {
                List<GoodThing> goodThings = data.getGoodThingList();
                mPlaces = goodThings;

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
                mController.setData(goodThings);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.logEvent("PageGoodList", true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.endTimedEvent("PageGoodList");
    }

}
