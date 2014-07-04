package android.prada.lab.goodthingmap.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prada on 6/29/14.
 */
public class GoodThingsData {
    @SerializedName("results")
    private List<GoodThing> goodThingList = new ArrayList<GoodThing>();

    public List<GoodThing> getGoodThingList() {
        return goodThingList;
    }
}
