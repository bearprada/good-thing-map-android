package goodthingmap.android.prada.lab.goodthingmap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.GoodThingRepository
import android.prada.lab.goodthingmap.network.GoodThingService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: GoodThingRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    constructor(service: GoodThingService, ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : this(
        GoodThingRepository(service), ioDispatcher
    )

    private val _topStory = MutableLiveData<GoodThing>()
    private val _error = MutableLiveData<Throwable>()

    val topStory: LiveData<GoodThing> = _topStory
    val error: LiveData<Throwable> = _error

    fun loadTopStory() {
        if (_topStory.value != null) return
        viewModelScope.launch {
            try {
                val data = withContext(ioDispatcher) { repository.topStory() }
                data.goodThing?.let(_topStory::setValue)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _error.value = error
            }
        }
    }
}
