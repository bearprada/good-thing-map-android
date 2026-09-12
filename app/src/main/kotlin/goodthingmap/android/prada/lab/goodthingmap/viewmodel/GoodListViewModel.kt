package goodthingmap.android.prada.lab.goodthingmap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.location.Location
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.GoodThingRepository
import android.prada.lab.goodthingmap.model.GoodThingType
import android.prada.lab.goodthingmap.network.GoodThingService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoodListViewModel(
    private val repository: GoodThingRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val distanceCalculator: (Location, GoodThing) -> Float = { current, place ->
        current.distanceTo(place.getLocation())
    }
) : ViewModel() {
    constructor(service: GoodThingService, ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : this(
        GoodThingRepository(service), ioDispatcher
    )

    private val _places = MutableLiveData<List<GoodThing>>()
    private val _error = MutableLiveData<Throwable>()

    val places: LiveData<List<GoodThing>> = _places
    val error: LiveData<Throwable> = _error

    fun load(type: GoodThingType, location: Location?) {
        if (_places.value != null) return
        viewModelScope.launch {
            try {
                val data = withContext(ioDispatcher) { repository.listPlaces(type, location) }
                val result = data.goodThingList.toMutableList()
                location?.let { current -> result.sortBy { distanceCalculator(current, it) } }
                _places.value = result
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _error.value = error
            }
        }
    }
}
