package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.GoodThingsData;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import goodthingmap.android.prada.lab.goodthingmap.R;
import goodthingmap.android.prada.lab.goodthingmap.component.GoodThingAdapter;

public class FavorActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fravor);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fravor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements AdapterView.OnItemClickListener {

        private GoodThingAdapter mAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fravor, container, false);
            mAdapter = new GoodThingAdapter(getActivity());
            ListView lv = (ListView)rootView.findViewById(R.id.list_view);
            lv.setAdapter(mAdapter);
            lv.setOnItemClickListener(this);
            Uri uri = Uri.parse("content://" + FavorContentProvider.AUTHORITY);
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                for (;cursor.isAfterLast(); cursor.moveToNext()) {
                    // TODO convert content values to GoodThing
                }
            }
            return rootView;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            GoodThing thing = mAdapter.getItem(i);
            if (thing != null) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(GoodThing.EXTRA_GOODTHING, thing);
                startActivity(intent);
            }
        }
    }
}
