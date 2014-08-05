package android.prada.lab.goodthingmap.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 123 on 8/5/2014.
 */
public class Result {
    @SerializedName("result")
    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
