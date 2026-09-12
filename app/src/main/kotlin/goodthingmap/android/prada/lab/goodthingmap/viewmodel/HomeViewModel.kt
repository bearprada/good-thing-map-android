package goodthingmap.android.prada.lab.goodthingmap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.GoodThingRepository
import android.prada.lab.goodthingmap.network.GoodThingService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.Scheduler

class HomeViewModel(
    private val repository: GoodThingRepository,
    private val subscribeScheduler: Scheduler = Schedulers.io(),
    private val observeScheduler: Scheduler = AndroidSchedulers.mainThread()
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
        observeScheduler: Scheduler
    ) : this(GoodThingRepository(service), subscribeScheduler, observeScheduler)

    private val disposables = CompositeDisposable()
    private val _topStory = MutableLiveData<GoodThing>()
    private val _error = MutableLiveData<Throwable>()

    val topStory: LiveData<GoodThing> = _topStory
    val error: LiveData<Throwable> = _error

    fun loadTopStory() {
        if (_topStory.value != null) return

        disposables.add(
            repository.topStory()
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(
                    { data -> data.goodThing?.let(_topStory::setValue) },
                    _error::setValue
                )
        )
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
