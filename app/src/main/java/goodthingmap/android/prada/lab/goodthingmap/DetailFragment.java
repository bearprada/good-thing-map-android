package goodthingmap.android.prada.lab.goodthingmap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.UserFavorite;
import android.prada.lab.goodthingmap.model.VendorComment;
import android.prada.lab.goodthingmap.model.VendorData;
import android.prada.lab.goodthingmap.model.VendorLike;
import android.prada.lab.goodthingmap.model.VendorPhoto;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import goodthingmap.android.prada.lab.goodthingmap.component.AlertDialogFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.CommentAdapter;
import goodthingmap.android.prada.lab.goodthingmap.component.ListDialogFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.ListViewForScrollView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by 123 on 8/31/2014.
 */
public class DetailFragment extends BaseServiceFragment implements View.OnClickListener, ViewPager.OnPageChangeListener, TextView.OnEditorActionListener {
    private static final List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions");
    private String mVendorId;
    private VendorData mVendorData;

    private MenuItem mFavoriteItem;
    private boolean mFavorite;

    private TextView mDetailTitle;
    private TextView mDetailAddress;
    private TextView mDetailBusinessHour;
    private TextView mDetailStory;
    private Button mShareBtn;
    private Button mSupportBtn;
    private ListView mList;

    private boolean mSupport;


    private List<VendorPhoto> mPhotos;
    private TextView mCoverIndex;
    private ViewPager mViewPager;
    private ImagePagerAdapter mCoverAdapter;

    private List<VendorComment> mComments;
    private CommentAdapter mAdapter;

    private Location mLocation;

    private UiLifecycleHelper mFBUiHelper;

    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if(bundle != null) {
            mVendorId = bundle.getString(HomeFragment.EXTRA_VENDOR_ID);
            mLocation = bundle.getParcelable(HomeFragment.EXTRA_LOCATION);
        }

        mFBUiHelper = new UiLifecycleHelper(this.getActivity(), null);
        mFBUiHelper.onCreate(savedStateInstance);
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

        mDetailTitle = (TextView)rootView.findViewById(R.id.detail_title);
        mDetailAddress = (TextView)rootView.findViewById(R.id.detail_address);
        mDetailBusinessHour = (TextView)rootView.findViewById(R.id.detail_business_hour);
        mDetailStory = (TextView)rootView.findViewById(R.id.detail_story);
        mAdapter = new CommentAdapter(getActivity());
        //mList = (ListViewForScrollView)rootView.findViewById(R.id.list_detail_comment);
        mList = new ListViewForScrollView(getActivity());
        mList.setAdapter(mAdapter);


        mCoverIndex = (TextView)rootView.findViewById(R.id.detail_num_cover);
        mViewPager = (ViewPager)rootView.findViewById(R.id.detail_cover_page);
        mViewPager.setOnPageChangeListener(this);

        mShareBtn = (Button)rootView.findViewById(R.id.btn_detail_share);
        mSupportBtn = (Button)rootView.findViewById(R.id.btn_detail_support);

        ((ScrollView)rootView.findViewById(R.id.detail_scroll_view)).smoothScrollBy(0, 0);
        ((LinearLayout)rootView.findViewById(R.id.layout_detail_comment)).addView(mList);
        ((EditText)rootView.findViewById(R.id.detail_comment)).setOnEditorActionListener(this);

        rootView.findViewById(R.id.btn_detail_map).setOnClickListener(this);
        rootView.findViewById(R.id.btn_detail_report).setOnClickListener(this);
        rootView.findViewById(R.id.btn_detail_new_image).setOnClickListener(this);

        initialize();

        return rootView;
    }

    private void initialize() {
        if(mVendorId == null) {
            return;
        }


        updateInfo(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        updateCover(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        updateComments(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        updateSupport(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detail, menu);
            super.onCreateOptionsMenu(menu, inflater);
            mFavoriteItem = menu.getItem(0);
            mFavoriteItem.setVisible(false);
            updateFavorite(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add_to_favorite:
                mService.setFavorite(mUser.getObjectId(), mVendorId, !mFavorite, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        if(!mFavorite) {
                            Toast.makeText(getActivity(), "已加入我的最愛", Toast.LENGTH_SHORT).show();
                        }

                        updateFavorite(ParseQuery.CachePolicy.NETWORK_ONLY);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
                return true;
        }

        return false;
    }


    private void updateInfo(ParseQuery.CachePolicy cachePolicy) {
        ParseQuery<VendorData> query = ParseQuery.getQuery(VendorData.class);
        query.setCachePolicy(cachePolicy);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo("objectId", mVendorId);
        query.findInBackground(new FindCallback<VendorData>() {
            @Override
            public void done(List<VendorData> vendorsData, ParseException e) {
                if(e == null) {
                    mVendorData = vendorsData.get(0);
                    setView();
                }
            }
        });
    }

    private void updateCover(ParseQuery.CachePolicy cachePolicy) {
        ParseQuery<VendorPhoto> query = ParseQuery.getQuery(VendorPhoto.class);
        query.setCachePolicy(cachePolicy);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo(VendorPhoto.VENDOR_ID, mVendorId);
        query.findInBackground(new FindCallback<VendorPhoto>() {
            @Override
            public void done(List<VendorPhoto> photos, ParseException e) {
                if(e == null) {
                    mPhotos = photos;
                    if(mPhotos.size() > 0) {
                        setCoverView();
                    }
                }
            }
        });
    }

    private void updateComments(ParseQuery.CachePolicy cachePolicy) {
        ParseQuery<VendorComment> query = ParseQuery.getQuery(VendorComment.class);
        query.setCachePolicy(cachePolicy);
        query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(10));
        query.whereEqualTo(VendorComment.VENDOR_ID, mVendorId);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<VendorComment>() {
            @Override
            public void done(List<VendorComment> comments, ParseException e) {
                if (e == null) {
                    mComments = comments;
                    setCommentView();
                }
            }
        });
    }

    private void updateFavorite(ParseQuery.CachePolicy cachePolicy) {
        ParseQuery<UserFavorite> query = ParseQuery.getQuery(UserFavorite.class);
        query.setCachePolicy(cachePolicy);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo(UserFavorite.USER_ID, mUser.getObjectId());
        query.whereEqualTo(UserFavorite.VENDOR_ID, mVendorId);
        query.whereEqualTo("cancel", false);
        query.findInBackground(new FindCallback<UserFavorite>() {
            @Override
            public void done(List<UserFavorite> favorites, ParseException e) {
                if(e == null) {
                    if(!favorites.isEmpty()) {
                        mFavoriteItem.setIcon(R.drawable.icon_favorite_true);
                        mFavorite = true;
                    } else {
                        mFavoriteItem.setIcon(R.drawable.icon_favorite_false);
                        mFavorite = false;
                    }

                    if(!mFavoriteItem.isVisible()) {
                        mFavoriteItem.setVisible(true);
                    }
                }
            }
        });
    }

    private void updateSupport(ParseQuery.CachePolicy cachePolicy) {
        ParseQuery<VendorLike> query = ParseQuery.getQuery(VendorLike.class);
        query.setCachePolicy(cachePolicy);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo(VendorLike.USER_ID, mUser.getObjectId());
        query.whereEqualTo(VendorLike.VENDOR_ID, mVendorId);
        query.whereEqualTo("cancel", false);
        query.findInBackground(new FindCallback<VendorLike>() {
            @Override
            public void done(List<VendorLike> likes, ParseException e) {
                if (e == null) {
                    mSupport = !likes.isEmpty();
                }
            }
        });
    }

    private void setView() {
        mDetailTitle.setText(mVendorData.getTitle());
        mDetailAddress.setText(mVendorData.getAddress());
        mDetailBusinessHour.setText(mVendorData.getBusinessHour());
        mDetailStory.setText(mVendorData.getDescription());
        mShareBtn.setText("分享(" + mVendorData.getNumShare() + ")");
        mSupportBtn.setText("支持(" + mVendorData.getNumLike() + ")");

        mShareBtn.setOnClickListener(this);
        mSupportBtn.setOnClickListener(this);
    }

    private void setCoverView() {
        mCoverAdapter = new ImagePagerAdapter(getActivity(), mPhotos);
        mViewPager.setAdapter(mCoverAdapter);
        mCoverIndex.setText(1 + "/" + mCoverAdapter.getCount());
    }

    private void setCommentView() {
        mAdapter.clear();

        for(VendorComment comment : mComments) {
            mAdapter.add(comment);
        }
    }


    private String getGoogleMapUri() {
        Location location = mVendorData.getLocation();
        return String.format("https://maps.google.com/maps?q=%f,%f", location.getLatitude(), location.getLongitude());
    }

    private void shareToFacebook() {
        Session facebookSession = Session.getActiveSession();
        if (isFacebookLogin()) {
            List<String> permissions = facebookSession.getPermissions();

            if (!isSubsetOf(PUBLISH_PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PUBLISH_PERMISSIONS);
                facebookSession.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }

            if (FacebookDialog.canPresentShareDialog(getActivity(),
                    FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
                        .setLink(getGoogleMapUri())
                        .setPicture(mVendorData.getIconUrl())
                        .setName(mVendorData.getTitle())
                        .setCaption(mVendorData.getBusinessHour())
                        .setDescription(mVendorData.getBriefDescription())
                        .build();
                mFBUiHelper.trackPendingDialogCall(shareDialog.present());
            } else {
                AlertDialogFragment mAlertDialogFragment = AlertDialogFragment.newInstance(
                        null,
                        getString(R.string.share_to_target_error_message, "Facebook", "Facebook"),
                        getString(android.R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=com.facebook.katana"));
                                startActivity(intent);
                            }
                        },
                        getString(android.R.string.no), null);
                try {
                    mAlertDialogFragment.show(getFragmentManager(), "download_warning");
                } catch (IllegalStateException e) {
                }
            }
        } else {
            facebookLogin();
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



    @Override
    public void onClick(View view) {
        AlertDialog dialog;
        switch(view.getId()) {
            case R.id.btn_detail_support:
                if(mSupport) {
                   Toast.makeText(getActivity(), "已支持！", Toast.LENGTH_SHORT).show();
                } else {
                    mService.setLike(mUser.getObjectId(), mVendorId, true, new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            Toast.makeText(getActivity(), "支持" + mVendorData.getTitle() + "成功！！", Toast.LENGTH_LONG).show();
                            updateSupport(ParseQuery.CachePolicy.NETWORK_ONLY);
                            updateInfo(ParseQuery.CachePolicy.NETWORK_ONLY);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                }
                break;

            case R.id.btn_detail_share:
                shareToFacebook();
                break;

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

            case R.id.btn_detail_map:

                int messageId = R.string.warning_navigation;
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
                                Location location = mVendorData.getLocation();
                                String url = (mLocation == null) ? getGoogleMapUri() :
                                        String.format("http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                                                mLocation.getLatitude(), mLocation.getLongitude(), location.getLatitude(),
                                                location.getLongitude());
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                                DetailFragment.this.startActivity(intent);
                            }
                        }).create();
                dialog.show();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCoverIndex.setText((position + 1) + "/" + mCoverAdapter.getCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFBUiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                boolean didComplete = FacebookDialog.getNativeDialogDidComplete(data);
                String postId = FacebookDialog.getNativeDialogPostId(data);

                if(didComplete && postId != null) {
                    mService.setShare(mUser.getObjectId(), mVendorId, 1, postId, new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            updateInfo(ParseQuery.CachePolicy.NETWORK_ONLY);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, 0);
    }

    @Override
    public boolean onEditorAction(final TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String comment = textView.getText().toString();

            hideKeyboard();

            if(comment == null || comment.length() == 0) {
                Toast.makeText(getActivity(), "留言不能為空白", Toast.LENGTH_SHORT).show();
                return false;
            }

            mService.addComment(mUser.getObjectId(), mVendorId, comment, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    textView.setText("");
                    Toast.makeText(getActivity(), "留言成功", Toast.LENGTH_SHORT).show();
                    updateComments(ParseQuery.CachePolicy.NETWORK_ONLY);
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), "留言失敗", Toast.LENGTH_SHORT).show();
                }
            });


            return true;
        }

        return false;
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private final Context mContext;
        private final List<VendorPhoto> mImages;

        public ImagePagerAdapter(Context ctx, List<VendorPhoto> images) {
            mContext = ctx;
            mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            int padding = mContext.getResources().getDimensionPixelSize(
                    R.dimen.padding_small);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String url = mImages.get(position).getUrl();
            Picasso.with(mContext).load(url).into(imageView);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
}
