package goodthingmap.android.prada.lab.goodthingmap

import android.content.Intent
import android.prada.lab.goodthingmap.model.CheckinResult
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.GoodThingData
import android.prada.lab.goodthingmap.model.GoodThingsData
import android.prada.lab.goodthingmap.model.LikeResult
import android.prada.lab.goodthingmap.network.GoodThingService
import android.view.View
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.runner.AndroidJUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class MockDataIntegrationTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private lateinit var mockService: MockDataGoodThingService

    @get:Rule
    val activityRule = ActivityTestRule(HomeActivity::class.java, false, false)

    @Before
    fun setUp() {
        mockService = MockDataGoodThingService()
        BaseActivity.serviceFactory = { mockService }
    }

    @After
    fun tearDown() {
        BaseActivity.serviceFactory = null
        if (activityRule.activity != null) activityRule.finishActivity()
    }

    @Test
    fun mockDataFlowsFromHomeThroughListToDetail() {
        val listMonitor = instrumentation.addMonitor(GoodListActivity::class.java.name, null, false)
        val detailMonitor = instrumentation.addMonitor(DetailActivity::class.java.name, null, false)
        try {
            activityRule.launchActivity(Intent())
            assertTrue(mockService.topStoryRequested.await(5, TimeUnit.SECONDS))
            instrumentation.waitForIdleSync()
            assertEquals(
                "Mock story from the integration fixture",
                activityRule.activity.findViewById<TextView>(R.id.cover_text).text.toString()
            )

            activityRule.activity.runOnUiThread {
                activityRule.activity.findViewById<View>(R.id.good_thing_01).performClick()
            }
            val listActivity = instrumentation.waitForMonitorWithTimeout(listMonitor, 5_000)
            assertNotNull(listActivity)
            assertTrue(mockService.listRequested.await(5, TimeUnit.SECONDS))
            instrumentation.waitForIdleSync()
            assertEquals(
                "Mock Cafe",
                listActivity.findViewById<TextView>(R.id.list_title).text.toString()
            )

            listActivity.runOnUiThread {
                val image = listActivity.findViewById<View>(R.id.list_image_view)
                (image.parent.parent as View).performClick()
            }
            val detailActivity = instrumentation.waitForMonitorWithTimeout(detailMonitor, 5_000)
            assertNotNull(detailActivity)
            instrumentation.waitForIdleSync()
            assertEquals(
                "Mock Cafe",
                detailActivity.findViewById<TextView>(R.id.detail_title).text.toString()
            )
        } finally {
            instrumentation.removeMonitor(listMonitor)
            instrumentation.removeMonitor(detailMonitor)
        }
    }

    private class MockDataGoodThingService : GoodThingService {
        val topStoryRequested = CountDownLatch(1)
        val listRequested = CountDownLatch(1)

        private val place = GoodThing().apply {
            id = 42
            title = "Mock Cafe"
            story = "Mock story from the integration fixture"
            address = "42 Test Street"
            memo = "Mock data only"
            imageUrl = "https://example.com/mock-cover.png"
            listImageUrl = "https://example.com/mock-list.png"
            detailImageUrl = "https://example.com/mock-detail.png"
            message = mutableListOf()
        }

        override suspend fun getTopStory(): GoodThingData = GoodThingData().apply {
            goodThing = place
            topStoryRequested.countDown()
        }

        override suspend fun listStory(type: Int): GoodThingsData = mockPlaces()

        override suspend fun listStory(type: Int, latitude: Double, longitude: Double): GoodThingsData = mockPlaces()

        override suspend fun listStory(latitude: Double, longitude: Double): GoodThingsData = mockPlaces()

        override suspend fun listStory(): GoodThingsData = mockPlaces()

        override suspend fun requestLikeNum(rid: Int): LikeResult = LikeResult()

        override suspend fun requestCheckinNum(rid: Int): CheckinResult = CheckinResult()

        override suspend fun reportCheckin(uid: String, rid: Int, checkinId: Int): CheckinResult = CheckinResult()

        override suspend fun likeGoodThing(uid: String, rid: Int): LikeResult = LikeResult()

        override suspend fun postComment(uid: String, rid: Int, message: String): LikeResult = LikeResult()

        private fun mockPlaces(): GoodThingsData {
            listRequested.countDown()
            return GoodThingsData().apply { goodThingList = listOf(place) }
        }
    }
}
