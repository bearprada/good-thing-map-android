package goodthingmap.android.prada.lab.goodthingmap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.location.Location
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.GoodThingType
import android.prada.lab.goodthingmap.model.GoodThingRepository
import android.prada.lab.goodthingmap.network.GoodThingService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.Scheduler

class GoodListViewModel(
    private val repository: GoodThingRepository,
    private val subscribeScheduler: Scheduler = Schedulers.io(),
    private val observeScheduler: Scheduler = AndroidSchedulers.mainThread(),
    private val distanceCalculator: (Location, GoodThing) -> Float = { current, place ->
        current.distanceTo(place.location)
    }
) : ViewModel() {
    constructor(repository: GoodThingRepository) : this(
        repository,
        Schedulers.io(),
        AndroidSchedulers.mainThread()
    )

    constructor(service: GoodThingService) : this(GoodThingRepository(service))

    constructor(
        service: GoodThingService,
        subscribeScheduler: Scheduler,
        observeScheduler: Scheduler,
        distanceCalculator: (Location, GoodThing) -> Float
    ) : this(
        GoodThingRepository(service),
        subscribeScheduler,
        observeScheduler,
        distanceCalculator
    )

    private val disposables = CompositeDisposable()
    private val _places = MutableLiveData<List<GoodThing>>()
    private val _error = MutableLiveData<Throwable>()

    val places: LiveData<List<GoodThing>> = _places
    val error: LiveData<Throwable> = _error

    fun load(type: GoodThingType, location: Location?) {
        if (_places.value != null) return

        val request = repository.listPlaces(type, location)

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
