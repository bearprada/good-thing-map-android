package android.prada.lab.goodthingmap.model

import android.prada.lab.goodthingmap.network.GoodThingService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.Mockito

class CoroutineRepositoryTest {
    @Test
    fun topStory_isExposedAsSuspendingResult() = runBlocking {
        val expected = GoodThingData().apply { goodThing = GoodThing() }
        val service = Mockito.mock(GoodThingService::class.java)
        Mockito.`when`(service.getTopStory()).thenReturn(expected)

        assertSame(expected, GoodThingRepository(service).topStory())
    }
}
