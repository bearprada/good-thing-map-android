package android.prada.lab.goodthingmap.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by prada on 6/28/14.
 */
public class UserMessage implements Parcelable {

    @SerializedName("messageId")
    private int id;
    @SerializedName("userId")
    private String userId;
    @SerializedName("message")
    private String message;
    @SerializedName("time")
    private long time;

    private UserMessage(String comment) {
        id = -1;
        userId = "";
        time = System.currentTimeMillis();
        message = comment;
    }

    public static UserMessage newInstance(String comment) {
        UserMessage instance = new UserMessage(comment);
        return instance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected UserMessage(Parcel in) {
        id = in.readInt();
        userId = in.readString();
        message = in.readString();
        time = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(userId);
        dest.writeString(message);
        dest.writeLong(time);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserMessage> CREATOR = new Parcelable.Creator<UserMessage>() {
        @Override
        public UserMessage createFromParcel(Parcel in) {
            return new UserMessage(in);
        }

        @Override
        public UserMessage[] newArray(int size) {
            return new UserMessage[size];
        }
    };
}
