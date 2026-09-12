package android.prada.lab.goodthingmap.model

import android.location.Location
import android.prada.lab.goodthingmap.network.GoodThingService

class GoodThingRepository(private val service: GoodThingService) {
    suspend fun topStory(): GoodThingData = service.getTopStory()

    suspend fun listPlaces(type: GoodThingType, location: Location?): GoodThingsData = when {
        location == null && type == GoodThingType.NEAR -> service.listStory()
        location == null -> service.listStory(type.typeId)
        type == GoodThingType.NEAR -> service.listStory(location.latitude, location.longitude)
        else -> service.listStory(type.typeId, location.latitude, location.longitude)
    }
}
