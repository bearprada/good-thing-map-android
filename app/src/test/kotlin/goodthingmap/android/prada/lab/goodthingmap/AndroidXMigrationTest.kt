package goodthingmap.android.prada.lab.goodthingmap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AndroidXMigrationTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun lifecycleLiveData_exposesTheLatestValue() {
        val values = MutableLiveData<String>()

        values.value = "migrated"

        assertEquals("migrated", values.value)
    }
}
