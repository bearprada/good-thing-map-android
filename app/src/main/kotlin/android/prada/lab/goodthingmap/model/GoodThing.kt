package android.prada.lab.goodthingmap.model

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class GoodThing() : Parcelable {
    companion object {
        const val EXTRA_GOODTHING = "extra_good_thing"

        @JvmField
        val CREATOR: Parcelable.Creator<GoodThing> = object : Parcelable.Creator<GoodThing> {
            override fun createFromParcel(source: Parcel): GoodThing = GoodThing(source)
            override fun newArray(size: Int): Array<GoodThing?> = arrayOfNulls(size)
        }
    }

    @field:SerializedName("gid")
    var id: Int = 0
    @field:SerializedName("title")
    var title: String? = null
    @field:SerializedName("messages")
    var message: MutableList<UserMessage>? = null
    @field:SerializedName("imageUrl")
    var imageUrl: String? = null
    @field:SerializedName("list_image_url")
    var listImageUrl: String? = null
    @field:SerializedName("detail_image_url")
    var detailImageUrl: String? = null
    @field:SerializedName("story")
    var story: String? = null
    @field:SerializedName("address")
    var address: String? = null
    @field:SerializedName("memo")
    var memo: String? = null
    @field:SerializedName("longtitude")
    var longtitude: Float = 0f
    @field:SerializedName("latitude")
    var latitude: Float = 0f
    @field:SerializedName("time")
    var time: Long = 0L
    @field:SerializedName("business_time")
    var businessTime: String? = null
    @field:SerializedName("content")
    var content: String? = null
    @field:SerializedName("can_post")
    var isCanPost: Boolean = false
    @field:SerializedName("is_big_issue")
    var isBigIssue: Boolean = false
    @field:SerializedName("images")
    var images: MutableList<String> = mutableListOf()

    fun getLocation(): Location = Location(title ?: "").apply {
        longitude = longtitude.toDouble()
        latitude = this@GoodThing.latitude.toDouble()
    }

    override fun toString(): String = "id $id"

    private constructor(source: Parcel) : this() {
        id = source.readInt()
        title = source.readString()
        message = source.createTypedArrayList(UserMessage.CREATOR)
        imageUrl = source.readString()
        listImageUrl = source.readString()
        detailImageUrl = source.readString()
        story = source.readString()
        address = source.readString()
        memo = source.readString()
        longtitude = source.readFloat()
        latitude = source.readFloat()
        time = source.readLong()
        businessTime = source.readString()
        content = source.readString()
        isCanPost = source.readByte().toInt() != 0
        isBigIssue = source.readByte().toInt() != 0
        images = source.createStringArrayList() ?: mutableListOf()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(destination: Parcel, flags: Int) {
        destination.writeInt(id)
        destination.writeString(title)
        destination.writeTypedList(message)
        destination.writeString(imageUrl)
        destination.writeString(listImageUrl)
        destination.writeString(detailImageUrl)
        destination.writeString(story)
        destination.writeString(address)
        destination.writeString(memo)
        destination.writeFloat(longtitude)
        destination.writeFloat(latitude)
        destination.writeLong(time)
        destination.writeString(businessTime)
        destination.writeString(content)
        destination.writeByte(if (isCanPost) 1 else 0)
        destination.writeByte(if (isBigIssue) 1 else 0)
        destination.writeStringList(images)
    }
}
