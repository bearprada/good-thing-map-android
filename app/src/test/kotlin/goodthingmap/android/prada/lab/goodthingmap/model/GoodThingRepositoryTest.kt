package android.prada.lab.goodthingmap.model

import android.prada.lab.goodthingmap.network.GoodThingService
import io.reactivex.Observable
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GoodThingRepositoryTest {
    @Test
    fun listPlaces_returnsTheCategoryResults() {
        val service = mock(GoodThingService::class.java)
        val expected = GoodThingsData()
        `when`(service.listStory(GoodThingType.MAIN.typeId))
            .thenReturn(Observable.just(expected))

        val repository = GoodThingRepository(service)

        val actual = repository.listPlaces(GoodThingType.MAIN, null).blockingFirst()

        assertSame(expected, actual)
    }
}
