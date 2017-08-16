package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageViewerActivity extends BaseActivity {

    public static final String EXTRA_PHOTOS = "extra_photos";
    public static final String PHOTO_INDEX = "photo_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_image_viewer);
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
    public static class PlaceholderFragment extends Fragment {

        private ImagePagerAdapter mAdapter;
        private ViewPager mViewPager;
        private List<Uri> mImages = new ArrayList<>();
        private int mPhotoIndex;

        @Override
        public void onStart() {
            super.onStart();
            FlurryAgent.logEvent("PageImageViewer", true);
        }

        @Override
        public void onStop() {
            super.onStop();
            FlurryAgent.endTimedEvent("PageImageViewer");
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedStateInstance) {
            super.onCreate(savedStateInstance);
            mImages = getArguments().getParcelableArrayList(EXTRA_PHOTOS);
            mPhotoIndex = getArguments().getInt(PHOTO_INDEX);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);
            mViewPager = rootView.findViewById(R.id.view_pager);
            mAdapter = new ImagePagerAdapter(getActivity(), mImages);
            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(mPhotoIndex);

            return rootView;
        }

        public static class ImagePagerAdapter extends PagerAdapter {
            private final Context mContext;
            private final List<Uri> mImages;

            ImagePagerAdapter(Context ctx, List<Uri> images) {
                mContext = ctx;
                mImages = images;
            }

            @Override
            public int getCount() {
                return mImages.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(mContext);
                int padding = mContext.getResources().getDimensionPixelSize(
                        R.dimen.padding_medium);
                imageView.setPadding(padding, padding, padding, padding);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Uri url = mImages.get(position);
                Picasso.with(mContext).load(url).into(imageView);
                container.addView(imageView, 0);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((ImageView) object);
            }
        }
    }
}
