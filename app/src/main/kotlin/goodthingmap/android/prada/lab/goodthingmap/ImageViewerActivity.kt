package goodthingmap.android.prada.lab.goodthingmap

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.flurry.android.FlurryAgent
import com.squareup.picasso.Picasso

class ImageViewerActivity : BaseActivity() {
    companion object {
        const val EXTRA_PHOTOS = "extra_photos"
        const val PHOTO_INDEX = "photo_index"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.fragment_image_viewer)
        val images = intent.getParcelableArrayListExtra<Uri>(EXTRA_PHOTOS).orEmpty()
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = ImagePagerAdapter(this, images)
        viewPager.currentItem = intent.getIntExtra(PHOTO_INDEX, 0).coerceIn(0, (images.size - 1).coerceAtLeast(0))
    }

    override fun onStart() {
        super.onStart()
        FlurryAgent.logEvent("PageImageViewer", true)
    }

    override fun onStop() {
        FlurryAgent.endTimedEvent("PageImageViewer")
        super.onStop()
    }

    private class ImagePagerAdapter(
        private val context: Context,
        private val images: List<Uri>
    ) : PagerAdapter() {
        override fun getCount(): Int = images.size

        override fun isViewFromObject(view: View, item: Any): Boolean = view === item

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = LayoutInflater.from(context)
                .inflate(R.layout.item_preview_image, container, false) as ImageView
            Picasso.get().load(images[position]).into(imageView)
            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
            container.removeView(item as ImageView)
        }
    }
}
