package goodthingmap.android.prada.lab.goodthingmap;

import android.app.ActivityOptions;
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
import android.prada.lab.goodthingmap.model.CheckinResult;
import android.prada.lab.goodthingmap.model.UserMessage;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.util.Log;
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
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

        private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

        public static final int MAX_STORY_TEXT_LINES = 6;

        private GoodThing mGoodThing;
        private CommentAdapter mCommentAdapter;
        private LayoutInflater mInflater;
        private TextView mStoryText;
        private View mLikeBtn;
        private Button mLikeBtnText;
        private View mShareBtn;
        private Button mShareBtnText;
        private Location mLocation;
        // TODO remove this value later.. bad design
        private String mCurrentComment;

        private UiLifecycleHelper mFBUiHelper;

        private int mStoryLines;


        public PlaceholderFragment() {
            super();
        }

        @Override
        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            mGoodThing = getArguments().getParcelable(GoodThing.EXTRA_GOODTHING);
            mLocation = getArguments().getParcelable(GoodListActivity.EXTRA_LOCATION);
            mInflater = LayoutInflater.from(getActivity());

            mFBUiHelper = new UiLifecycleHelper(this.getActivity(), null);
            mFBUiHelper.onCreate(savedStateInstance);
        }

        @Override
        public void onStart() {
            super.onStart();
            FlurryAgent.logEvent("PageDetail", true);
        }

        @Override
        public void onStop() {
            super.onStop();
            FlurryAgent.endTimedEvent("PageDetail");
        }

        @Override
        public void onResume() {
            super.onResume();
            mFBUiHelper.onResume();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            mFBUiHelper.onSaveInstanceState(outState);
        }

        @Override
        public void onPause() {
            super.onPause();
            mFBUiHelper.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mFBUiHelper.onDestroy();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ((TextView)rootView.findViewById(R.id.detail_distance)).setText(Utility.calDistance(mLocation, mGoodThing));
            ((TextView)rootView.findViewById(R.id.detail_title)).setText(mGoodThing.getTitle());
            ((TextView)rootView.findViewById(R.id.detail_memo)).setText(mGoodThing.getMemo());

            mStoryText =  ((TextView)rootView.findViewById(R.id.detail_story));
            mStoryText.setText(mGoodThing.getStory());
            mStoryText.setOnClickListener(this);


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
            mShareBtn = rootView.findViewById(R.id.btn_detail_share);
            mShareBtnText =  (Button) rootView.findViewById(R.id.btn_detail_share_text);

            rootView.findViewById(R.id.btn_detail_map).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_new_image).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_report).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_share).setOnClickListener(this);

            setupLikeNum();
            setupCheckinNum();

            return rootView;
        }

        private void setupLikeNum() {
            mService.requestLikeNum(mGoodThing.getId(), new retrofit.Callback<LikeResult>() {
                @Override
                public void success(LikeResult s, Response response) {
                    mLikeBtnText.setText(getString(R.string.like) + "(" +  s.getResult() + ")");
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
        }

        private void setupCheckinNum() {
            mService.requestCheckinNum(mGoodThing.getId(), new retrofit.Callback<CheckinResult>() {
                @Override
                public void success(CheckinResult s, Response response) {
                    mShareBtnText.setText(getString(R.string.share) + "(" + s.getResult() + ")");
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
        }


        private ArrayList<Uri> getImageUris() {
            ArrayList<Uri> uris = new ArrayList<Uri>();
            for (String url : mGoodThing.getImages()) {
                uris.add(Uri.parse(url));
            }
            return uris;
        }

        private void setupImages(final ViewGroup hsv, List<String> images) {
            int count = (images == null) ? 0 : images.size();
            for (int i = 0 ; i < count ; i++) {
                String url = images.get(i);
                final ImageView iv = (ImageView) mInflater.inflate(R.layout.item_image, null);
                final int index = i;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
                        intent.putExtra(ImageViewerActivity.PHOTO_INDEX, index);
                        intent.putParcelableArrayListExtra(ImageViewerActivity.EXTRA_PHOTOS, getImageUris());
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                    }
                });
                Picasso.with(getActivity()).load(url).placeholder(R.drawable.btn_new_image)
                        .error(R.drawable.btn_new_image).into(iv);
                hsv.addView(iv);
            }
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
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_to_target_share));
            intent.putExtra(Intent.EXTRA_TEXT, uri);
            intent.setClassName(info.packageName, info.name);
            startActivity(intent);
        }

        private void shareTo(String packageName, String appName) {
            ActivityInfo info = getActivityInfo(packageName);
            if (info != null) {
                shareToTarget(info, getGoogleMapUri());
            } else {
                shareToAppNotFound(packageName, appName);
            }
        }

        private void shareToAppNotFound(String packageName, String appName) {
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

        private void shareToFacebook() {
            if (!FacebookDialog.canPresentShareDialog(getActivity(),
                    FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                shareToAppNotFound("market://details?id=com.facebook.katana", "Facebook");
                return;
            }

            Session facebookSession = Session.getActiveSession();
            if (facebookSession != null && facebookSession.isOpened()) {
                List<String> permissions = facebookSession.getPermissions();

                if (!isSubsetOf(PERMISSIONS, permissions)) {
                    Session.NewPermissionsRequest newPermissionsRequest = new Session
                            .NewPermissionsRequest(this, PERMISSIONS);
                    facebookSession.requestNewPublishPermissions(newPermissionsRequest);
                    return;
                }

                FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
                        .setLink(getGoogleMapUri())
                        .setPicture(mGoodThing.getImageUrl())
                        .setName(mGoodThing.getTitle())
                        .setCaption(mGoodThing.getMemo())
                        .setDescription(mGoodThing.getStory())
                        .build();
                mFBUiHelper.trackPendingDialogCall(shareDialog.present());
            } else {
                Intent intent = new Intent(getActivity(), FacebookLoginActivity.class);
                startActivity(intent);
            }
        }


        private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
            for (String string : subset) {
                if (!superset.contains(string)) {
                    return false;
                }
            }
            return true;
        }

        private String getGoogleMapUri() {
            return String.format("https://maps.google.com/maps?q=%f,%f", mGoodThing.getLatitude(), mGoodThing.getLongtitude());
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            mFBUiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
                @Override
                public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                    Log.e("Activity", String.format("Error: %s", error.toString()));
                }

                @Override
                public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                    boolean didComplete = FacebookDialog.getNativeDialogDidComplete(data);
                    String completionGesture = FacebookDialog.getNativeDialogCompletionGesture(data);
                    String postId = FacebookDialog.getNativeDialogPostId(data);

                    if(didComplete && postId != null) {
                        Toast.makeText(getActivity(), R.string.share_successful, Toast.LENGTH_SHORT).show();

                        mService.reportCheckin(Amplitude.getDeviceId(), mGoodThing.getId(), Integer.parseInt(postId), new retrofit.Callback<CheckinResult>() {

                            @Override
                            public void success(CheckinResult checkinResult, Response response) {
                            }

                            @Override
                            public void failure(RetrofitError error) {
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            AlertDialog dialog;
            switch(view.getId()) {
                case R.id.btn_detail_report:
                    FlurryAgent.logEvent("Event_Click_Detail_Report", false);
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.fromParts("mailto", "goodmaps2013@gmail.com", null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, R.string.subject_report);
                    Intent mailer = Intent.createChooser(intent, null);
                    startActivity(mailer);
                    break;
                case R.id.btn_detail_new_image:
                    FlurryAgent.logEvent("Event_Click_Detail_New_Image", false);
                    ListDialogFragment fragment = ListDialogFragment.newInstance();
                    fragment.show(getFragmentManager(), "");
                    break;
                case R.id.btn_detail_comment:
                    FlurryAgent.logEvent("Event_Click_Detail_Comment", false);
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
                    FlurryAgent.logEvent("Event_Click_Detail_Like", false);
                    mLikeBtn.setSelected(true);

                    mService.likeGoodThing(Amplitude.getDeviceId(), mGoodThing.getId(), new retrofit.Callback<LikeResult>() {
                        @Override
                        public void success(LikeResult s, Response response) {
                            Toast.makeText(getActivity(), R.string.msg_like_successful, Toast.LENGTH_SHORT).show();
                            mLikeBtnText.setText(getString(R.string.like) + "(" + s.getResult() + ")");
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getActivity(), R.string.add_like_fail + ":" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.btn_detail_map:
                    FlurryAgent.logEvent("Event_Click_Detail_Map", false);
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
                                    String url = (mLocation == null) ? getGoogleMapUri() :
                                            String.format("http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                                                    mLocation.getLatitude(), mLocation.getLongitude(), mGoodThing.getLatitude(),
                                                    mGoodThing.getLongtitude());
                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                                    PlaceholderFragment.this.startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                    break;
                case R.id.btn_detail_share:
                    FlurryAgent.logEvent("Event_Click_Detail_Share", false);
                    shareToFacebook();
                    break;
                case R.id.detail_story:
                    FlurryAgent.logEvent("Event_Click_Detail_Story", false);
                    LinearLayout.LayoutParams oldLayoutParams = (LinearLayout.LayoutParams)
                            mStoryText.getLayoutParams();

                    int currentLines = mStoryText.getLineCount();
                    mStoryLines = (currentLines > mStoryLines) ? currentLines : mStoryLines;

                    if(mStoryLines <= MAX_STORY_TEXT_LINES) {
                        return;
                    }

                    if(currentLines > MAX_STORY_TEXT_LINES) {
                        mStoryText.setMaxLines(MAX_STORY_TEXT_LINES);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                mStoryText.getLineHeight() * (MAX_STORY_TEXT_LINES + 1));
                        layoutParams.setMargins(oldLayoutParams.leftMargin, oldLayoutParams.topMargin, 0, 0);

                        mStoryText.setLayoutParams(layoutParams);
                    } else {
                        mStoryText.setMaxLines(mStoryLines + 1);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                mStoryText.getLineHeight() * (mStoryLines + 1));
                        layoutParams.setMargins(oldLayoutParams.leftMargin, oldLayoutParams.topMargin, 0, 0);

                        mStoryText.setLayoutParams(layoutParams);
                    }
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
