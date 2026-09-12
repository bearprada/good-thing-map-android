package android.prada.lab.goodthingmap.model

import com.google.gson.annotations.SerializedName

class LikeResult {
    @field:SerializedName("result")
    var result: Int = 0

    @field:SerializedName("message")
    var message: String? = null
}
