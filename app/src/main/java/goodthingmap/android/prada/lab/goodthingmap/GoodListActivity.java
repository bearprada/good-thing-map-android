package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Intent;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.GoodThingsData;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.GoodThingAdapter;

public class GoodListActivity extends ActionBarActivity {

    public static final String EXTRA_TYPE = "extra_type";

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
            case android.R.id.home:
                finish();
                return true;
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
    public static class PlaceholderFragment extends BaseServiceFragment implements AdapterView.OnItemClickListener {

        private HomeActivity.PlaceholderFragment.GoodThingType mType;
        private GoodThingAdapter mAdapter;

        public PlaceholderFragment() {
            super();
        }

        @Override
        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            int typeId = getArguments().getInt(EXTRA_TYPE, 0);
            mType = HomeActivity.PlaceholderFragment.GoodThingType.values()[typeId];
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_good_list, container, false);
            mAdapter = new GoodThingAdapter(getActivity());
            ListView lv = (ListView)rootView.findViewById(R.id.list_view);
            lv.setAdapter(mAdapter);
            lv.setOnItemClickListener(this);
            Task.callInBackground(new Callable<GoodThingsData>() {
                @Override
                public GoodThingsData call() throws Exception {
                    return mService.listStory(mType.getTypeId());
                }
            }).onSuccess(new Continuation<GoodThingsData, Object>() {
                @Override
                public Object then(Task<GoodThingsData> task) throws Exception {
                    for (GoodThing thing : task.getResult().getGoodThingList()) {
                        mAdapter.add(thing);
                    }
                    mAdapter.notifyDataSetChanged();
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR).continueWith(new Continuation<Object, Object>() {
                @Override
                public Object then(Task<Object> task) throws Exception {
                    if (task.isFaulted() || task.isCancelled()) {
                        task.getError().printStackTrace();
                    }
                    return null;
                }
            });
            return rootView;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            GoodThing item = mAdapter.getItem(i);
            if (item != null) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(GoodThing.EXTRA_GOODTHING, item);
                startActivity(intent);
            }
        }
    }
}
