package goodthingmap.android.prada.lab.goodthingmap.viewmodel

import android.location.Location
import android.prada.lab.goodthingmap.model.GoodThingType
import android.prada.lab.goodthingmap.model.GoodThingsData
import android.prada.lab.goodthingmap.network.GoodThingService
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun homeViewModel_publishesTopStory() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val expected = Gson().fromJson(
                "{\"result\":{\"gid\":7,\"story\":\"A good thing\"}}",
                android.prada.lab.goodthingmap.model.GoodThingData::class.java
            )
            val service = Mockito.mock(GoodThingService::class.java)
            Mockito.`when`(service.getTopStory()).thenReturn(expected)

            val viewModel = HomeViewModel(service, dispatcher)
            viewModel.loadTopStory()
            advanceUntilIdle()

            assertEquals("A good thing", viewModel.topStory.value?.story)
            assertEquals(7, viewModel.topStory.value?.id)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun goodListViewModel_sortsPlacesByDistance() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val expected = Gson().fromJson(
                "{\"results\":[{\"gid\":1,\"latitude\":0.1,\"longtitude\":0.1},{\"gid\":2,\"latitude\":0.01,\"longtitude\":0.01}]}",
                GoodThingsData::class.java
            )
            val service = Mockito.mock(GoodThingService::class.java)
            Mockito.`when`(service.listStory(GoodThingType.MAIN.typeId, 0.0, 0.0)).thenReturn(expected)
            val currentLocation = Mockito.mock(Location::class.java)
            Mockito.`when`(currentLocation.latitude).thenReturn(0.0)
            Mockito.`when`(currentLocation.longitude).thenReturn(0.0)

            val viewModel = GoodListViewModel(
                repository = android.prada.lab.goodthingmap.model.GoodThingRepository(service),
                ioDispatcher = dispatcher,
                distanceCalculator = { _, place -> if (place.id == 2) 1f else 10f }
            )
            viewModel.load(GoodThingType.MAIN, currentLocation)
            advanceUntilIdle()

            assertEquals(listOf(2, 1), viewModel.places.value?.map { it.id })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun homeViewModel_publishesRequestFailure() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val failure = IllegalStateException("network unavailable")
            val service = Mockito.mock(GoodThingService::class.java)
            Mockito.`when`(service.getTopStory()).thenThrow(failure)

            val viewModel = HomeViewModel(service, dispatcher)
            viewModel.loadTopStory()
            advanceUntilIdle()

            assertEquals(failure.message, viewModel.error.value?.message)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
