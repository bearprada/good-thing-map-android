package goodthingmap.android.prada.lab.goodthingmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.LikeResult;
import android.prada.lab.goodthingmap.model.UserMessage;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.squareup.picasso.Picasso;

import java.util.List;

import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.CommentAdapter;
import goodthingmap.android.prada.lab.goodthingmap.component.ListDialogFragment;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailActivity extends BaseActivity {

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
    public static class PlaceholderFragment extends BaseServiceFragment implements View.OnClickListener {

        private GoodThing mGoodThing;
        private CommentAdapter mCommentAdapter;
        private LayoutInflater mInflater;
        private View mLikeBtn;
        private Button mLikeBtnText;

        public PlaceholderFragment() {
            super();
        }

        @Override
        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            mGoodThing = getArguments().getParcelable(GoodThing.EXTRA_GOODTHING);
            mInflater = LayoutInflater.from(getActivity());
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            // ((TextView)rootView.findViewById(R.id.detail_distance)); // FIXME
            ((TextView)rootView.findViewById(R.id.detail_title)).setText(mGoodThing.getTitle());
            ((TextView)rootView.findViewById(R.id.detail_story)).setText(mGoodThing.getStory());
            ((TextView)rootView.findViewById(R.id.detail_memo)).setText(mGoodThing.getMemo());
            LinearLayout hsv = (LinearLayout) rootView.findViewById(R.id.detail_images);
            setupImages(hsv, mGoodThing.getImages());
            Picasso.with(getActivity()).load(mGoodThing.getDetailImageUrl()).into(
                    ((ImageView) rootView.findViewById(R.id.detail_cover_image)));
            ListView commentList = (ListView) rootView.findViewById(R.id.detail_list_comments);
            mCommentAdapter = new CommentAdapter(getActivity());
            commentList.setAdapter(mCommentAdapter);
            for (UserMessage message : mGoodThing.getMessage()) {
                mCommentAdapter.add(message);
            }
            mCommentAdapter.notifyDataSetChanged();

            rootView.findViewById(R.id.btn_detail_comment).setOnClickListener(this);
            mLikeBtn = rootView.findViewById(R.id.btn_detail_like);
            mLikeBtn.setOnClickListener(this);
            mLikeBtnText = (Button) rootView.findViewById(R.id.btn_detail_like_text);
            rootView.findViewById(R.id.btn_detail_map).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_new_image).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_report).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_share).setOnClickListener(this);
            return rootView;
        }

        private void setupImages(final ViewGroup hsv, List<String> images) {
            if (images != null) {
                for (String url : images) {
                    final ImageView iv = (ImageView) mInflater.inflate(R.layout.item_image, null);
                    Picasso.with(getActivity()).load(url).placeholder(R.drawable.btn_new_image)
                            .error(R.drawable.btn_new_image).into(iv);
                    hsv.addView(iv);
                }
            }
            int count = (images == null) ? 0 : images.size();
            for (int i = count ; i < 5 ; i++) {
                ImageView iv = new ImageView(getActivity());
                iv.setImageResource(R.drawable.btn_new_image);
                hsv.addView(iv);
            }
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_detail_report:
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.fromParts("mailto", "goodmaps2013@gmail.com", null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, R.string.subject_report);
                    Intent mailer = Intent.createChooser(intent, null);
                    startActivity(mailer);
                    break;
                case R.id.btn_detail_new_image:
                    ListDialogFragment fragment = ListDialogFragment.newInstance();
                    fragment.show(getFragmentManager(), "");
                    break;
                case R.id.btn_detail_comment:
                    // show input dialog
                    break;
                case R.id.btn_detail_like:
                    mLikeBtn.setSelected(true);
                    mService.likeGoodThing(Amplitude.getDeviceId(), mGoodThing.getId(), new retrofit.Callback<LikeResult>() {
                        @Override
                        public void success(LikeResult s, Response response) {
                            Toast.makeText(getActivity(), "喜歡好事成功", Toast.LENGTH_SHORT).show();
                            mLikeBtnText.setText("喜歡(" + s.getResult() + ")");
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getActivity(), "喜歡好事失敗:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.btn_detail_map:
                    int messageId = mGoodThing.isBigIssue() ? R.string.warning_tbi_navigation : R.string.warning_navigation;
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.warning_navigation_title)
                            .setMessage(messageId)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                            // FIXME correct the geo location
                                            Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
                                    PlaceholderFragment.this.startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                    break;
                case R.id.btn_detail_share:
                    // TODO add facebook sdk
                    break;
                default:
                    break;
            }
        }
    }
}
