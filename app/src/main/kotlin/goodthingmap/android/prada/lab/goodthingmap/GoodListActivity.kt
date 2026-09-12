package goodthingmap.android.prada.lab.goodthingmap

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.GoodThingRepository
import android.prada.lab.goodthingmap.model.GoodThingType
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flurry.android.FlurryAgent
import goodthingmap.android.prada.lab.goodthingmap.component.GTController
import goodthingmap.android.prada.lab.goodthingmap.component.GTPlaceModel
import goodthingmap.android.prada.lab.goodthingmap.viewmodel.GoodListViewModel

class GoodListActivity : BaseActivity() {
    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_LOCATION = "extra_location"
    }

    private lateinit var type: GoodThingType
    private var location: Location? = null
    private var places: List<GoodThing> = emptyList()
    private lateinit var listViewModel: GoodListViewModel

    private val controller = GTController(object : GTPlaceModel.GTClickListener {
        override fun onPlaceClick(view: View, placeId: Long) {
            places.firstOrNull { it.id.toLong() == placeId }?.let { place ->
                val intent = Intent(this@GoodListActivity, DetailActivity::class.java).apply {
                    putExtra(GoodThing.EXTRA_GOODTHING, place)
                    putExtra(EXTRA_LOCATION, location)
                }
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@GoodListActivity,
                    Pair(view.findViewById(R.id.list_image_view), getString(R.string.trans_cover_image)),
                    Pair(view.findViewById(R.id.list_distance), getString(R.string.trans_distance)),
                    Pair(view.findViewById(R.id.list_title), getString(R.string.trans_title))
                )
                ActivityCompat.startActivity(this@GoodListActivity, intent, options.toBundle())
            }
        }

        override fun onFavorClick(view: View, goodthing: GoodThing) = Unit
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_good_list)
        type = GoodThingType.values()[intent.getIntExtra(EXTRA_TYPE, 0).coerceIn(0, GoodThingType.values().lastIndex)]
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = type.getName()
        }
        location = intent.getParcelableExtra(EXTRA_LOCATION)

        findViewById<RecyclerView>(R.id.list_view).apply {
            layoutManager = LinearLayoutManager(this@GoodListActivity)
            adapter = controller.adapter
        }
        listViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                GoodListViewModel(GoodThingRepository(mService)) as T
        }).get(GoodListViewModel::class.java)
        listViewModel.places.observe(this) { loadedPlaces ->
            places = loadedPlaces ?: emptyList()
            controller.setData(places)
        }
        listViewModel.load(type, location)
    }

    override fun onStart() {
        super.onStart()
        FlurryAgent.logEvent("PageGoodList", true)
    }

    override fun onStop() {
        FlurryAgent.endTimedEvent("PageGoodList")
        super.onStop()
    }
}
