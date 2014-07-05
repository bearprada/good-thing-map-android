package android.prada.lab.goodthingmap.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by prada on 2014/7/5.
 */
public class LikeResult {
    @SerializedName("result")
    private int result;
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
