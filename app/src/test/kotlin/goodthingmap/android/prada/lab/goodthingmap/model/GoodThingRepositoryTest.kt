package android.prada.lab.goodthingmap.model

import android.prada.lab.goodthingmap.network.GoodThingService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.Mockito

class GoodThingRepositoryTest {
    @Test
    fun listPlaces_returnsSuspendingServiceResult() = runBlocking {
        val expected = GoodThingsData()
        val service = Mockito.mock(GoodThingService::class.java)
        Mockito.`when`(service.listStory(GoodThingType.MAIN.typeId)).thenReturn(expected)

        assertSame(expected, GoodThingRepository(service).listPlaces(GoodThingType.MAIN, null))
    }
}
