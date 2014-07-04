package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.UserMessage;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_detail);
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
    public static class PlaceholderFragment extends Fragment {

        private GoodThing mGoodThing;
        private CommentAdapter mCommentAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            mGoodThing = getArguments().getParcelable(GoodThing.EXTRA_GOODTHING);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            // ((TextView)rootView.findViewById(R.id.detail_distance)); // FIXME
            ((TextView)rootView.findViewById(R.id.detail_title)).setText(mGoodThing.getTitle());
            ((TextView)rootView.findViewById(R.id.detail_story)).setText(mGoodThing.getStory());
            ((TextView)rootView.findViewById(R.id.detail_memo)).setText(mGoodThing.getMemo());

            Picasso.with(getActivity()).load(mGoodThing.getDetailImageUrl()).into(
                    ((ImageView) rootView.findViewById(R.id.detail_cover_image)), new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                        }
                    }
            );
            ListView commentList = (ListView) rootView.findViewById(R.id.detail_list_comments);
            mCommentAdapter = new CommentAdapter(getActivity());
            commentList.setAdapter(mCommentAdapter);
            for (UserMessage message : mGoodThing.getMessage()) {
                mCommentAdapter.add(message);
            }
            mCommentAdapter.notifyDataSetChanged();
            return rootView;
        }

        public static class CommentAdapter extends ArrayAdapter<UserMessage> {

            private final LayoutInflater mInflater;

            public CommentAdapter(Context context) {
                super(context, 0);
                mInflater = LayoutInflater.from(context);
            }

            @Override
            public View getView(int i, View convertView, ViewGroup viewGroup) {
                View view;
                if (convertView == null) {
                    view = mInflater.inflate(R.layout.item_comment, null);
                } else {
                    view = convertView;
                }
                UserMessage comment = getItem(i);
                ((TextView)view.findViewById(R.id.list_comment)).setText(comment.getMessage());
                ((TextView)view.findViewById(R.id.list_user_id)).setText(comment.getUserId());
                ((TextView)view.findViewById(R.id.list_time)).setText(String.valueOf(comment.getTime()));
                return view;
            }

        }
    }
}
