package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import goodthingmap.android.prada.lab.goodthingmap.R;

public class ImageViewerActivity extends ActionBarActivity {

    public static final String EXTRA_PHOTOS = "extra_photos";
    public static final String PHOTO_INDEX = "photo_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
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
    public static class PlaceholderFragment extends Fragment {

        private ImagePagerAdapter mAdapter;
        private ViewPager mViewPager;
        private List<Uri> mImages = new ArrayList<Uri>();
        private int mPhotoIndex;

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
            mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
            mAdapter = new ImagePagerAdapter(getActivity(), mImages);
            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(mPhotoIndex);

            return rootView;
        }

        public static class ImagePagerAdapter extends PagerAdapter {
            private final Context mContext;
            private final List<Uri> mImages;

            public ImagePagerAdapter(Context ctx, List<Uri> images) {
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
                        R.dimen.padding_medium);
                imageView.setPadding(padding, padding, padding, padding);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Uri url = mImages.get(position);
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
}
