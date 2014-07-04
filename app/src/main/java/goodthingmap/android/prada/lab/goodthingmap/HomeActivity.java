package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Intent;
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

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;


public class HomeActivity extends ActionBarActivity {

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
    public static class PlaceholderFragment extends BaseServiceFragment implements View.OnClickListener {
        public PlaceholderFragment() {
            super();
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
                        startActivity(intent);
                    }
                    break;
                case R.id.good_thing_01:
                    moveList(GoodThingType.MAIN);
                    break;
                case R.id.good_thing_02:
                    moveList(GoodThingType.SNACK);
                    break;
                case R.id.good_thing_03:
                    moveList(GoodThingType.FRUIT);
                    break;
                case R.id.good_thing_04:
                    moveList(GoodThingType.OTHER);
                    break;
                case R.id.good_thing_05:
                    moveList(GoodThingType.TBI);
                    break;
                case R.id.good_thing_06:
                    moveList(GoodThingType.NEAR);
                    break;
            }
        }

        private void moveList(GoodThingType type) {
            Intent intent = new Intent(getActivity(), GoodListActivity.class);
            intent.putExtra(GoodListActivity.EXTRA_TYPE, type.ordinal());
            startActivity(intent);
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
