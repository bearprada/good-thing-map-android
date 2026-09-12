package goodthingmap.android.prada.lab.goodthingmap

import androidx.test.rule.ActivityTestRule
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class HomeActivityLaunchTest {
    @get:Rule
    val activityRule = ActivityTestRule(HomeActivity::class.java)

    @Test
    fun homeActivityLaunchesWithoutLifecycleInitializationCrash() {
        assertNotNull(activityRule.activity)
    }
}
