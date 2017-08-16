package goodthingmap.android.prada.lab.goodthingmap;


import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amplitude.api.Amplitude;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import goodthingmap.android.prada.lab.goodthingmap.component.AlertDialogFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.ListDialogFragment;
import goodthingmap.android.prada.lab.goodthingmap.util.LocationUtil;
import io.reactivex.functions.Consumer;

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends BaseServiceFragment implements View.OnClickListener {

        public static final int MAX_STORY_TEXT_LINES = 6;

        private GoodThing mGoodThing;
        private LayoutInflater mInflater;
        private TextView mStoryText;
        private View mLikeBtn;
        private Button mLikeBtnText;
        private Button mShareBtnText;
        private Location mLocation;
        // TODO remove this value later.. bad design
        private String mCurrentComment;

        private ShareDialog shareDialog;
        private int mStoryLines;
        private LinearLayout commentList;


        public PlaceholderFragment() {
            super();
        }

        @Override
        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            mGoodThing = getArguments().getParcelable(GoodThing.EXTRA_GOODTHING);
            mLocation = getArguments().getParcelable(GoodListActivity.EXTRA_LOCATION);
            mInflater = LayoutInflater.from(getActivity());

            // Initialize Facebook sdk and ShareDialog
            FacebookSdk.sdkInitialize(getContext());
            shareDialog = new ShareDialog(this);
        }

        private View getCommentView(int i, ViewGroup parent, UserMessage comment) {
            View view = mInflater.inflate(R.layout.item_comment, parent, false);
            ((TextView)view.findViewById(R.id.list_seq_id)).setText("#" + i);
            ((TextView)view.findViewById(R.id.list_comment)).setText(comment.getMessage());
            ((TextView)view.findViewById(R.id.list_time)).setText(String.valueOf(comment.getTime()));
            return view;
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

            // Logs 'install' and 'app activate' App Events.
            AppEventsLogger.activateApp(getContext());
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onPause() {
            super.onPause();

            // Logs 'app deactivate' App Event.
            AppEventsLogger.deactivateApp(getContext());
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            ((TextView)rootView.findViewById(R.id.detail_distance)).setText(LocationUtil.calDistance(mLocation, mGoodThing));
            ((TextView)rootView.findViewById(R.id.detail_title)).setText(mGoodThing.getTitle());
            ((TextView)rootView.findViewById(R.id.detail_memo)).setText(mGoodThing.getMemo());

            mStoryText = rootView.findViewById(R.id.detail_story);
            mStoryText.setText(mGoodThing.getStory());
            mStoryText.setOnClickListener(this);


            LinearLayout hsv = rootView.findViewById(R.id.detail_images);
            setupImages(hsv, mGoodThing.getImages());
            Picasso.with(getActivity()).load(mGoodThing.getDetailImageUrl()).into(
                    ((ImageView) rootView.findViewById(R.id.detail_cover_image)));
            commentList = rootView.findViewById(R.id.detail_list_comments);
            refreshCommentList(container);

            mLikeBtn = rootView.findViewById(R.id.btn_detail_like);
            mLikeBtn.setOnClickListener(this);
            mLikeBtnText = rootView.findViewById(R.id.btn_detail_like_text);
            mShareBtnText = rootView.findViewById(R.id.btn_detail_share_text);

            rootView.findViewById(R.id.btn_detail_comment).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_map).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_new_image).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_report).setOnClickListener(this);
            rootView.findViewById(R.id.btn_detail_share).setOnClickListener(this);

            setupLikeNum();
            setupCheckinNum();

            return rootView;
        }

        private void setupLikeNum() {
            mService.requestLikeNum(mGoodThing.getId())
                .subscribe(new Consumer<LikeResult>() {
                    @Override
                    public void accept(LikeResult likeResult) throws Exception {
                        mLikeBtnText.setText(getString(R.string.like) + "(" + likeResult.getResult() + ")");
                    }
                });
        }

        private void setupCheckinNum() {
            mService.requestCheckinNum(mGoodThing.getId())
                .subscribe(new Consumer<CheckinResult>() {
                    @Override
                    public void accept(CheckinResult checkinResult) throws Exception {
                        mShareBtnText.setText(getString(R.string.share) + "(" + checkinResult.getResult() + ")");
                    }
                });
        }

        private void setupImages(final ViewGroup hsv, List<String> images) {
            int count = (images == null) ? 0 : images.size();
            for (int i = 0 ; i < count ; i++) {
                String url = images.get(i);
                final ImageView iv = (ImageView) mInflater.inflate(R.layout.item_image, hsv, false);
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
                    getString(R.string.share_to_target_error_message),
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
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(mGoodThing.getTitle())
                        .setContentUrl(Uri.parse(getGoogleMapUri()))
                        .setImageUrl(Uri.parse(mGoodThing.getImageUrl()))
                        .setContentDescription(mGoodThing.getMemo() + "   " + mGoodThing.getStory())
                        .build();
                shareDialog.show(linkContent);
            }else {
                shareToAppNotFound("com.facebook.katana", "Facebook");
                return;
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
        public void onClick(View view) {
            Dialog dialog;
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

                    dialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.enter_comment)
                            .positiveText(R.string.confirm)
                            .negativeText(R.string.cancel)
                            .input(null, null, false, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    String comment = input.toString();
                                    mCurrentComment = comment;
                                    mService.postComment(Amplitude.getDeviceId(), mGoodThing.getId(), comment)
                                        .subscribe(new Consumer<LikeResult>() {
                                            @Override
                                            public void accept(LikeResult likeResult) throws Exception {
                                                Toast.makeText(getActivity(), R.string.post_comment_successful, Toast.LENGTH_SHORT).show();
                                                mGoodThing.getMessage().add(0, UserMessage.newInstance(mCurrentComment));
                                                refreshCommentList((ViewGroup) getView());
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                Toast.makeText(getActivity(), R.string.post_comment_fail, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                }
                            })
                            .build();
                    dialog.show();
                    break;
                case R.id.btn_detail_like:
                    FlurryAgent.logEvent("Event_Click_Detail_Like", false);
                    mLikeBtn.setSelected(true);

                    mService.likeGoodThing(Amplitude.getDeviceId(), mGoodThing.getId())
                        .subscribe(new Consumer<LikeResult>() {
                            @Override
                            public void accept(LikeResult likeResult) throws Exception {
                                Toast.makeText(getActivity(), R.string.msg_like_successful, Toast.LENGTH_SHORT).show();
                                mLikeBtnText.setText(getString(R.string.like) + "(" + likeResult.getResult() + ")");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable t) throws Exception {
                                Toast.makeText(getActivity(), R.string.add_like_fail + ":" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    break;
                case R.id.btn_detail_map:
                    FlurryAgent.logEvent("Event_Click_Detail_Map", false);
                    int messageId = mGoodThing.isBigIssue() ? R.string.warning_tbi_navigation : R.string.warning_navigation;
                    dialog = new AlertDialogWrapper.Builder(getActivity())
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

        private void refreshCommentList(ViewGroup container) {
            int count = 1;
            commentList.removeAllViews();
            for (UserMessage message : mGoodThing.getMessage()) {
                commentList.addView(getCommentView(count++, container, message));
            }
        }
    }
}