package goodthingmap.android.prada.lab.goodthingmap.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.location.Location
import android.prada.lab.goodthingmap.model.GoodThingType
import android.prada.lab.goodthingmap.model.GoodThingRepository
import android.prada.lab.goodthingmap.network.GoodThingService
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.Proxy

class ViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun homeViewModel_publishesTopStory() {
        val service = fakeService { method ->
            if (method.name == "getTopStory") {
                Observable.just(Gson().fromJson("{\"result\":{\"gid\":7,\"story\":\"A good thing\"}}", android.prada.lab.goodthingmap.model.GoodThingData::class.java))
            } else error("Unexpected call: ${method.name}")
        }
        val viewModel = HomeViewModel(service, Schedulers.trampoline(), Schedulers.trampoline())

        viewModel.loadTopStory()

        assertEquals("A good thing", viewModel.topStory.value?.story)
        assertEquals(7, viewModel.topStory.value?.id)
    }

    @Test
    fun goodListViewModel_sortsPlacesByDistance() {
        val service = fakeService { method ->
            if (method.name == "listStory") {
                Observable.just(Gson().fromJson(
                    "{\"results\":[{\"gid\":1,\"latitude\":0.1,\"longtitude\":0.1},{\"gid\":2,\"latitude\":0.01,\"longtitude\":0.01}]}",
                    android.prada.lab.goodthingmap.model.GoodThingsData::class.java
                ))
            } else error("Unexpected call: ${method.name}")
        }
        val currentLocation = Mockito.mock(Location::class.java)
        Mockito.`when`(currentLocation.latitude).thenReturn(0.0)
        Mockito.`when`(currentLocation.longitude).thenReturn(0.0)
        val viewModel = GoodListViewModel(
            service,
            Schedulers.trampoline(),
            Schedulers.trampoline()
        ) { _, place -> if (place.id == 2) 1f else 10f }

        viewModel.load(GoodThingType.MAIN, currentLocation)

        assertEquals(listOf(2, 1), viewModel.places.value?.map { it.id })
    }

    @Test
    fun homeViewModel_publishesRequestErrors() {
        val failure = IllegalStateException("network unavailable")
        val service = fakeService { method ->
            if (method.name == "getTopStory") Observable.error<android.prada.lab.goodthingmap.model.GoodThingData>(failure)
            else error("Unexpected call: ${method.name}")
        }
        val viewModel = HomeViewModel(service, Schedulers.trampoline(), Schedulers.trampoline())

        viewModel.loadTopStory()

        assertSame(failure, viewModel.error.value)
    }

    @Test
    fun goodListViewModel_doesNotReloadCompletedResults() {
        var requests = 0
        val service = fakeService { method ->
            if (method.name == "listStory") {
                requests += 1
                Observable.just(android.prada.lab.goodthingmap.model.GoodThingsData())
            } else {
                error("Unexpected call: ${method.name}")
            }
        }
        val viewModel = GoodListViewModel(
            GoodThingRepository(service),
            Schedulers.trampoline(),
            Schedulers.trampoline()
        ) { _, _ -> 0f }

        viewModel.load(GoodThingType.MAIN, null)
        viewModel.load(GoodThingType.MAIN, null)

        assertEquals(1, requests)
    }

    private fun fakeService(handler: (java.lang.reflect.Method) -> Any): GoodThingService {
        return Proxy.newProxyInstance(
            GoodThingService::class.java.classLoader,
            arrayOf(GoodThingService::class.java)
        ) { _, method, _ -> handler(method) } as GoodThingService
    }
}
