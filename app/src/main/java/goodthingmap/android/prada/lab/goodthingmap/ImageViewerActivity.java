package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageViewerActivity extends BaseActivity {

    public static final String EXTRA_PHOTOS = "extra_photos";
    public static final String PHOTO_INDEX = "photo_index";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.fragment_image_viewer);

        List<Uri> images = getIntent().getParcelableArrayListExtra(EXTRA_PHOTOS);
        int photoIndex = getIntent().getIntExtra(PHOTO_INDEX, 0);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(photoIndex);
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
            ImageView imageView = (ImageView) LayoutInflater.from(mContext).inflate(
                R.layout.item_preview_image,container, false);
            Picasso.with(mContext)
                .load(mImages.get(position))
                .into(imageView);
            container.addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }
}
