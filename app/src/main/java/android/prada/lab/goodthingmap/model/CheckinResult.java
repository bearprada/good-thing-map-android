package android.prada.lab.goodthingmap.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 123 on 8/5/2014.
 */
public class CheckinResult {
    @SerializedName("result")
    private int result;

    public int getResult() {
        return result;
    }
}
