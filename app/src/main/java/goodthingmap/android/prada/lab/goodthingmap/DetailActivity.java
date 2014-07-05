package goodthingmap.android.prada.lab.goodthingmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.LikeResult;
import android.prada.lab.goodthingmap.model.UserMessage;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import goodthingmap.android.prada.lab.goodthingmap.component.AlertDialogFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.CommentAdapter;
import goodthingmap.android.prada.lab.goodthingmap.component.ListDialogFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.Utility;
import retrofit.Callback;
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
    public static class PlaceholderFragment extends BaseServiceFragment implements View.OnClickListener, Callback<LikeResult> {

        public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";

        private GoodThing mGoodThing;
        private CommentAdapter mCommentAdapter;
        private LayoutInflater mInflater;
        private View mLikeBtn;
        private Button mLikeBtnText;
        private Location mLocation;
        // TODO remove this value later.. bad design
        private String mCurrentComment;

        public PlaceholderFragment() {
            super();
        }

        @Override
        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            mGoodThing = getArguments().getParcelable(GoodThing.EXTRA_GOODTHING);
            mLocation = getArguments().getParcelable(GoodListActivity.EXTRA_LOCATION);
            mInflater = LayoutInflater.from(getActivity());
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ((TextView)rootView.findViewById(R.id.detail_distance)).setText(Utility.calDistance(mLocation, mGoodThing));
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

        private ArrayList<Uri> getImageUris() {
            ArrayList<Uri> uris = new ArrayList<Uri>();
            for (String url : mGoodThing.getImages()) {
                uris.add(Uri.parse(url));
            }
            return uris;
        }

        private void setupImages(final ViewGroup hsv, List<String> images) {
            if (images != null) {
                for (String url : images) {
                    final ImageView iv = (ImageView) mInflater.inflate(R.layout.item_image, null);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
                            intent.putParcelableArrayListExtra(ImageViewerActivity.EXTRA_PHOTOS, getImageUris());
                            startActivity(intent);
                        }
                    });
                    Picasso.with(getActivity()).load(url).placeholder(R.drawable.btn_new_image)
                            .error(R.drawable.btn_new_image).into(iv);
                    hsv.addView(iv);
                }
            }
            int count = (images == null) ? 0 : images.size();
            for (int i = count ; i < 5 ; i++) {
                ImageView iv = new ImageView(getActivity());
                iv.setImageResource(R.drawable.btn_new_image);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ListDialogFragment fragment = ListDialogFragment.newInstance();
                        fragment.show(getFragmentManager(), "");
                    }
                });
                hsv.addView(iv);
            }
        }


        private ActivityInfo getActivityInfo(String packageName) {
            PackageManager pm = getActivity().getPackageManager();
            final Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            final List<ResolveInfo> otherActs = pm.queryIntentActivities(sendIntent, 0);
            for (ResolveInfo resolveInfo : otherActs) {
                ActivityInfo info = resolveInfo.activityInfo;
                String string = info.packageName;
                if (string.equalsIgnoreCase(packageName)) {
                    return info;
                }
            }
            return null;
        }

        private void shareToTarget(ActivityInfo info, String uri) {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享好事");
            intent.putExtra(Intent.EXTRA_TEXT, uri);
            intent.setClassName(info.packageName, info.name);
            startActivity(intent);
        }

        private void shareTo(String packageName, String appName) {
            ActivityInfo info = getActivityInfo(packageName);
            if (info != null) {
                shareToTarget(info, getGoogleMapUri());
            } else {
                final String packageNameF = packageName;
                AlertDialogFragment mAlertDialogFragment = AlertDialogFragment.newInstance(
                        null,
                        getString(R.string.share_to_target_error_message, appName, appName),
                        getString(android.R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=" + packageNameF));
                                startActivity(intent);
                            }
                        },
                        getString(android.R.string.no), null);
                try {
                    mAlertDialogFragment.show(getFragmentManager(), "download_warning");
                } catch (IllegalStateException e) {
                }
            }
        }

        private String getGoogleMapUri() {
            return String.format("https://maps.google.com/maps?q=%f,%f", mGoodThing.getLatitude(), mGoodThing.getLongtitude());
        }

        @Override
        public void onClick(View view) {
            AlertDialog dialog;
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
                    final EditText input = new EditText(getActivity());
                    dialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.enter_comment)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String comment = input.getText().toString();
                                    if (TextUtils.isEmpty(comment) == false) {
                                        mCurrentComment = comment;
                                        mService.postComment(Amplitude.getDeviceId(), mGoodThing.getId(), comment, PlaceholderFragment.this);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, null).create();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    dialog.setView(input); // uncomment this line
                    dialog.show();
                    break;
                case R.id.btn_detail_like:
                    mLikeBtn.setSelected(true);
                    mService.likeGoodThing(Amplitude.getDeviceId(), mGoodThing.getId(), new retrofit.Callback<LikeResult>() {
                        @Override
                        public void success(LikeResult s, Response response) {
                            Toast.makeText(getActivity(), R.string.msg_like_successful, Toast.LENGTH_SHORT).show();
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
                    dialog = new AlertDialog.Builder(getActivity())
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
                                    String url = String.format("http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                                            mLocation.getLatitude(), mLocation.getLongitude(), mGoodThing.getLatitude(),
                                            mGoodThing.getLongtitude());
                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                                    PlaceholderFragment.this.startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                    break;
                case R.id.btn_detail_share:
                    shareTo(FACEBOOK_PACKAGE_NAME, "Facebook");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void success(LikeResult likeResult, Response response) {
            Toast.makeText(getActivity(), R.string.post_comment_successful, Toast.LENGTH_SHORT).show();
            mCommentAdapter.add(UserMessage.newInstance(mCurrentComment));
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(getActivity(), R.string.post_comment_fail, Toast.LENGTH_SHORT).show();
        }
    }
}
