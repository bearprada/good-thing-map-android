package goodthingmap.android.prada.lab.goodthingmap.component

import android.location.Location
import android.prada.lab.goodthingmap.model.GoodThing
import com.airbnb.epoxy.TypedEpoxyController
import goodthingmap.android.prada.lab.goodthingmap.util.LocationUtil

class GTController(
    private val itemClickListener: GTPlaceModel.GTClickListener
) : TypedEpoxyController<List<GoodThing>>() {
    private var currentLocation: Location? = null

    override fun buildModels(places: List<GoodThing>) {
        for (place in places) {
            GTPlaceModel_()
                .id(place.id.toLong())
                .title(place.title.orEmpty())
                .address(place.address.orEmpty())
                .distance(LocationUtil.calDistance(currentLocation, place))
                .imageUrl(place.listImageUrl.orEmpty())
                .clickListener(itemClickListener)
                .addTo(this)
        }
    }

    fun setLocation(location: Location?) {
        currentLocation = location
    }
}
