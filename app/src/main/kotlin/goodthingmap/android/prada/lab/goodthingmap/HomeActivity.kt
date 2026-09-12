package goodthingmap.android.prada.lab.goodthingmap

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.GoodThingRepository
import android.prada.lab.goodthingmap.model.GoodThingType
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.annotation.SuppressLint
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.flurry.android.FlurryAgent
import com.squareup.picasso.Picasso
import goodthingmap.android.prada.lab.goodthingmap.util.LogEventUtils
import goodthingmap.android.prada.lab.goodthingmap.viewmodel.HomeViewModel

class HomeActivity : BaseActivity(), View.OnClickListener, LocationListener {
    companion object {
        private const val REQUEST_PERMISSION_GRANT = 1
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        @JvmStatic
        fun displayPromptForEnablingGps(activity: Activity) {
            MaterialDialog.Builder(activity)
                .content(R.string.warning_open_gps)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive { _, _: DialogAction ->
                    activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()
        }
    }

    private lateinit var locationManager: LocationManager
    private lateinit var homeViewModel: HomeViewModel
    private var currentLocation: Location? = null
    private var animation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha)

        val storyText = findViewById<TextView>(R.id.cover_text)
        val coverImage = findViewById<ImageView>(R.id.cover_image)
        homeViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                HomeViewModel(GoodThingRepository(mService)) as T
        }).get(HomeViewModel::class.java)
        homeViewModel.topStory.observe(this) { goodThing ->
            storyText.text = goodThing.story
            coverImage.tag = goodThing
            Picasso.with(this@HomeActivity).load(goodThing.imageUrl).into(coverImage)
        }
        homeViewModel.loadTopStory()

        listOf(
            R.id.good_thing_01,
            R.id.good_thing_02,
            R.id.good_thing_03,
            R.id.good_thing_04,
            R.id.good_thing_05,
            R.id.good_thing_06,
            R.id.cover_image
        ).forEach { findViewById<View>(it).setOnClickListener(this) }
    }

    override fun onStart() {
        super.onStart()
        FlurryAgent.logEvent("PageHome", true)
        if (hasLocationPermission()) getCurrentLocation(false)
    }

    override fun onStop() {
        FlurryAgent.endTimedEvent("PageHome")
        super.onStop()
    }

    override fun onPause() {
        locationManager.removeUpdates(this)
        super.onPause()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cover_image -> (view.tag as? GoodThing)?.let { goodThing ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra(GoodThing.EXTRA_GOODTHING, goodThing)
                    putExtra(GoodListActivity.EXTRA_LOCATION, currentLocation)
                }
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, view, getString(R.string.trans_cover_image)
                )
                ActivityCompat.startActivity(this, intent, options.toBundle())
            }
            R.id.good_thing_01 -> moveList(GoodThingType.MAIN, "Event_Click_Home_Main")
            R.id.good_thing_02 -> moveList(GoodThingType.SNACK, "Event_Click_Home_Snack")
            R.id.good_thing_03 -> moveList(GoodThingType.FRUIT, "Event_Click_Home_Fruit")
            R.id.good_thing_04 -> moveList(GoodThingType.OTHER, "Event_Click_Home_Other")
            R.id.good_thing_05 -> moveList(GoodThingType.TBI, "Event_Click_Home_TBI")
            R.id.good_thing_06 -> {
                LogEventUtils.sendEvent("Event_Click_Home_Near")
                getCurrentLocation(true)
                moveList(GoodThingType.NEAR)
            }
        }
    }

    private fun moveList(type: GoodThingType, event: String? = null) {
        event?.let(LogEventUtils::sendEvent)
        startActivity(Intent(this, GoodListActivity::class.java).apply {
            putExtra(GoodListActivity.EXTRA_TYPE, type.ordinal)
            putExtra(GoodListActivity.EXTRA_LOCATION, currentLocation)
        })
    }

    private fun hasLocationPermission(): Boolean = LocationPermissionPolicy.hasLocationPermission(
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION),
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    )

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_GRANT && hasLocationPermission()) getCurrentLocation(false)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(userClick: Boolean) {
        if (!hasLocationPermission()) {
            if (userClick) ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, REQUEST_PERMISSION_GRANT)
            return
        }
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!gpsEnabled && !networkEnabled) {
            if (userClick) displayPromptForEnablingGps(this)
            return
        }
        if (networkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10_000L, 0f, this, Looper.getMainLooper())
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let(::onLocationChanged)
        }
        if (gpsEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10_000L, 0f, this, Looper.getMainLooper())
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let(::onLocationChanged)
        }
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        locationManager.removeUpdates(this)
    }

    @Deprecated("Deprecated by the Android framework")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

    override fun onProviderEnabled(provider: String) = Unit

    override fun onProviderDisabled(provider: String) = Unit

}
