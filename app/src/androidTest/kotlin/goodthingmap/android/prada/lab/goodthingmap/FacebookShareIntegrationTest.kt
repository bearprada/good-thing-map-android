package goodthingmap.android.prada.lab.goodthingmap

import android.content.Intent
import android.graphics.Bitmap
import android.prada.lab.goodthingmap.model.CheckinResult
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.GoodThingData
import android.prada.lab.goodthingmap.model.GoodThingsData
import android.prada.lab.goodthingmap.model.LikeResult
import android.prada.lab.goodthingmap.network.GoodThingService
import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.facebook.FacebookException
import goodthingmap.android.prada.lab.goodthingmap.component.AlertDialogFragment
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class FacebookShareIntegrationTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @get:Rule
    val activityRule = ActivityTestRule(DetailActivity::class.java, false, false)

    @After
    fun tearDown() {
        DetailActivity.facebookShareAvailability = null
        DetailActivity.facebookShareLauncher = null
        BaseActivity.serviceFactory = null
        activityRule.finishActivity()
    }

    @Test
    fun facebookUnavailableShowsFallbackInsteadOfCrashing() {
        DetailActivity.facebookShareAvailability = { false }
        BaseActivity.serviceFactory = { FakeGoodThingService() }
        val detailActivity = activityRule.launchActivity(Intent().apply {
            putExtra(GoodThing.EXTRA_GOODTHING, mockGoodThing())
        })
        instrumentation.waitForIdleSync()

        detailActivity.runOnUiThread {
            detailActivity.findViewById<View>(R.id.btn_detail_share).performClick()
        }
        instrumentation.waitForIdleSync()

        assertFalse(detailActivity.isFinishing)
        assertNotNull(
            detailActivity.supportFragmentManager.findFragmentByTag("download_warning") as? AlertDialogFragment
        )
        saveScreenshot(detailActivity)
    }

    @Test
    fun facebookShareSdkFailureShowsFallbackInsteadOfCrashing() {
        DetailActivity.facebookShareAvailability = { true }
        DetailActivity.facebookShareLauncher = { throw FacebookException("Facebook app is unavailable") }
        BaseActivity.serviceFactory = { FakeGoodThingService() }
        val detailActivity = activityRule.launchActivity(Intent().apply {
            putExtra(GoodThing.EXTRA_GOODTHING, mockGoodThing())
        })
        instrumentation.waitForIdleSync()

        detailActivity.runOnUiThread {
            detailActivity.findViewById<View>(R.id.btn_detail_share).performClick()
        }
        instrumentation.waitForIdleSync()

        assertFalse(detailActivity.isFinishing)
        assertNotNull(
            detailActivity.supportFragmentManager.findFragmentByTag("download_warning") as? AlertDialogFragment
        )
    }

    private fun mockGoodThing() = GoodThing().apply {
        id = 3
        title = "Mock Cafe"
        story = "Mock story"
        imageUrl = "https://example.com/mock-cover.png"
        detailImageUrl = "https://example.com/mock-detail.png"
    }

    private fun saveScreenshot(activity: DetailActivity) {
        val screenshot = instrumentation.uiAutomation.takeScreenshot()
        val file = File(activity.getExternalFilesDir(null), "issue3-facebook-fallback.png")
        FileOutputStream(file).use { output ->
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
    }

    private class FakeGoodThingService : GoodThingService {
        override suspend fun getTopStory() = GoodThingData()
        override suspend fun listStory(type: Int) = GoodThingsData()
        override suspend fun listStory(type: Int, latitude: Double, longitude: Double) = GoodThingsData()
        override suspend fun listStory(latitude: Double, longitude: Double) = GoodThingsData()
        override suspend fun listStory() = GoodThingsData()
        override suspend fun requestLikeNum(rid: Int) = LikeResult()
        override suspend fun requestCheckinNum(rid: Int) = CheckinResult()
        override suspend fun reportCheckin(uid: String, rid: Int, checkinId: Int) = CheckinResult()
        override suspend fun likeGoodThing(uid: String, rid: Int) = LikeResult()
        override suspend fun postComment(uid: String, rid: Int, message: String) = LikeResult()
    }
}
