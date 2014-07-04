package android.prada.lab.goodthingmap.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prada on 6/28/14.
 */
public class GoodThing implements Parcelable {

    public static final String EXTRA_GOODTHING = "extra_good_thing";

    @SerializedName("gid")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("messages")
    private List<UserMessage> message;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("list_image_url")
    private String listImageUrl;
    @SerializedName("detail_image_url")
    private String detailImageUrl;
    @SerializedName("story")
    private String story;
    @SerializedName("address")
    private String address;
    @SerializedName("memo")
    private String memo;
    @SerializedName("longtitude")
    private float longtitude;
    @SerializedName("latitude")
    private float latitude;
    @SerializedName("time")
    private long time;
    @SerializedName("business_time")
    private String businessTime;
    @SerializedName("content")
    private String content;
    @SerializedName("can_post")
    private boolean canPost;
    @SerializedName("is_big_issue")
    private boolean isBigIssue;
    @SerializedName("images")
    private List<String> images = new ArrayList<String>();

    public GoodThing() {
    }

    @Override
    public String toString() {
        return "id " + getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<UserMessage> getMessage() {
        return message;
    }

    public void setMessage(List<UserMessage> message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getListImageUrl() {
        return listImageUrl;
    }

    public void setListImageUrl(String listImageUrl) {
        this.listImageUrl = listImageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getDetailImageUrl() {
        return detailImageUrl;
    }

    public void setDetailImageUrl(String detailImageUrl) {
        this.detailImageUrl = detailImageUrl;
    }

    public float getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(long longtitude) {
        this.longtitude = longtitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getBusinessTime() {
        return businessTime;
    }

    public void setBusinessTime(String businessTime) {
        this.businessTime = businessTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCanPost() {
        return canPost;
    }

    public void setCanPost(boolean canPost) {
        this.canPost = canPost;
    }

    public boolean isBigIssue() {
        return isBigIssue;
    }

    public void setBigIssue(boolean isBigIssue) {
        this.isBigIssue = isBigIssue;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    protected GoodThing(Parcel in) {
        id = in.readInt();
        title = in.readString();
        if (in.readByte() == 0x01) {
            message = new ArrayList<UserMessage>();
            in.readList(message, UserMessage.class.getClassLoader());
        } else {
            message = null;
        }
        imageUrl = in.readString();
        listImageUrl = in.readString();
        detailImageUrl = in.readString();
        story = in.readString();
        address = in.readString();
        memo = in.readString();
        longtitude = in.readFloat();
        latitude = in.readFloat();
        time = in.readLong();
        businessTime = in.readString();
        content = in.readString();
        canPost = in.readByte() != 0x00;
        isBigIssue = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            images = new ArrayList<String>();
            in.readList(images, String.class.getClassLoader());
        } else {
            images = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        if (message == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(message);
        }
        dest.writeString(imageUrl);
        dest.writeString(listImageUrl);
        dest.writeString(detailImageUrl);
        dest.writeString(story);
        dest.writeString(address);
        dest.writeString(memo);
        dest.writeFloat(longtitude);
        dest.writeFloat(latitude);
        dest.writeLong(time);
        dest.writeString(businessTime);
        dest.writeString(content);
        dest.writeByte((byte) (canPost ? 0x01 : 0x00));
        dest.writeByte((byte) (isBigIssue ? 0x01 : 0x00));
        if (images == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(images);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GoodThing> CREATOR = new Parcelable.Creator<GoodThing>() {
        @Override
        public GoodThing createFromParcel(Parcel in) {
            return new GoodThing(in);
        }

        @Override
        public GoodThing[] newArray(int size) {
            return new GoodThing[size];
        }
    };
}
