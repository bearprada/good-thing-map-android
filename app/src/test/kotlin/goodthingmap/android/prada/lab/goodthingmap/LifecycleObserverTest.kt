package goodthingmap.android.prada.lab.goodthingmap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LifecycleObserverTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun observer_receivesValuesOnlyWhileOwnerIsStarted() {
        val owner = TestOwner()
        val values = MutableLiveData<Int>()
        val received = mutableListOf<Int>()

        values.observe(owner, Observer { value -> value?.let(received::add) })
        owner.start()
        values.value = 1
        owner.stop()
        values.value = 2

        assertEquals(listOf(1), received)
    }

    private class TestOwner : LifecycleOwner {
        private val registry = LifecycleRegistry(this)

        override val lifecycle: Lifecycle
            get() = registry

        fun start() = registry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        fun stop() = registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }
}
