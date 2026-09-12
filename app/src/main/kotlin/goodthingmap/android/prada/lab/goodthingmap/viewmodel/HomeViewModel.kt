package goodthingmap.android.prada.lab.goodthingmap.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.network.GoodThingService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.Scheduler

class HomeViewModel(
    private val service: GoodThingService,
    private val subscribeScheduler: Scheduler = Schedulers.io(),
    private val observeScheduler: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel() {
    constructor(service: GoodThingService) : this(
        service,
        Schedulers.io(),
        AndroidSchedulers.mainThread()
    )

    private val disposables = CompositeDisposable()
    private val _topStory = MutableLiveData<GoodThing>()
    private val _error = MutableLiveData<Throwable>()

    val topStory: LiveData<GoodThing> = _topStory
    val error: LiveData<Throwable> = _error

    fun loadTopStory() {
        if (_topStory.value != null) return

        disposables.add(
            service.topStory
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
