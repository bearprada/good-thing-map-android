package goodthingmap.android.prada.lab.goodthingmap

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
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

        override fun getLifecycle(): Lifecycle = registry

        fun start() = registry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        fun stop() = registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }
}
