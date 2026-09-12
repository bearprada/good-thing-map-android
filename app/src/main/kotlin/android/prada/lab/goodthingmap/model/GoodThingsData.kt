package android.prada.lab.goodthingmap.model

import com.google.gson.annotations.SerializedName

class GoodThingsData {
    @field:SerializedName("results")
    var goodThingList: List<GoodThing> = emptyList()
}
