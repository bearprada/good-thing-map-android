package goodthingmap.android.prada.lab.goodthingmap.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Location
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.GoodThingType
import android.prada.lab.goodthingmap.network.GoodThingService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.Scheduler

class GoodListViewModel(
    private val service: GoodThingService,
    private val subscribeScheduler: Scheduler = Schedulers.io(),
    private val observeScheduler: Scheduler = AndroidSchedulers.mainThread(),
    private val distanceCalculator: (Location, GoodThing) -> Float = { current, place ->
        current.distanceTo(place.location)
    }
) : ViewModel() {
    constructor(service: GoodThingService) : this(
        service,
        Schedulers.io(),
        AndroidSchedulers.mainThread()
    )

    private val disposables = CompositeDisposable()
    private val _places = MutableLiveData<List<GoodThing>>()
    private val _error = MutableLiveData<Throwable>()

    val places: LiveData<List<GoodThing>> = _places
    val error: LiveData<Throwable> = _error

    fun load(type: GoodThingType, location: Location?) {
        val request = when {
            location == null && type == GoodThingType.NEAR -> service.listStory()
            location == null -> service.listStory(type.typeId)
            type == GoodThingType.NEAR -> service.listStory(location.latitude, location.longitude)
            else -> service.listStory(type.typeId, location.latitude, location.longitude)
        }

        disposables.add(
            request.subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(
                    { data ->
                        val result = data.goodThingList.toMutableList()
                        location?.let { current ->
                            result.sortBy { distanceCalculator(current, it) }
                        }
                        _places.value = result
                    },
                    _error::setValue
                )
        )
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
