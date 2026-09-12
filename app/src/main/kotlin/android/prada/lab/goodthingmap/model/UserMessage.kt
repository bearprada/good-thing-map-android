package android.prada.lab.goodthingmap.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserMessage() : Parcelable {
    companion object {
        @JvmStatic
        fun newInstance(comment: String): UserMessage = UserMessage().apply {
            id = -1
            userId = ""
            time = System.currentTimeMillis()
            message = comment
        }

        @JvmField
        val CREATOR: Parcelable.Creator<UserMessage> = object : Parcelable.Creator<UserMessage> {
            override fun createFromParcel(source: Parcel): UserMessage = UserMessage(source)
            override fun newArray(size: Int): Array<UserMessage?> = arrayOfNulls(size)
        }
    }

    @field:SerializedName("messageId")
    var id: Int = 0
    @field:SerializedName("userId")
    var userId: String? = null
    @field:SerializedName("message")
    var message: String? = null
    @field:SerializedName("time")
    var time: Long = 0L

    private constructor(source: Parcel) : this() {
        id = source.readInt()
        userId = source.readString()
        message = source.readString()
        time = source.readLong()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(destination: Parcel, flags: Int) {
        destination.writeInt(id)
        destination.writeString(userId)
        destination.writeString(message)
        destination.writeLong(time)
    }
}
