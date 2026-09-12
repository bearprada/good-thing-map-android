package goodthingmap.android.prada.lab.goodthingmap.model

import android.prada.lab.goodthingmap.model.GoodThing
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GoodThingModelTest {
    @Test
    fun gsonMapsApiFieldsAndKeepsImageCollectionAvailable() {
        val goodThing = Gson().fromJson(
            "{\"gid\":7,\"title\":\"A good thing\",\"images\":[\"image.jpg\"]}",
            GoodThing::class.java
        )

        assertEquals(7, goodThing.id)
        assertEquals("A good thing", goodThing.title)
        assertEquals(listOf("image.jpg"), goodThing.images)
        assertNotNull(goodThing.images)
    }
}
