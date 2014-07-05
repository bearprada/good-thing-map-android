package android.prada.lab.goodthingmap.network;

import android.prada.lab.goodthingmap.model.GoodThing;
import android.prada.lab.goodthingmap.model.GoodThingData;
import android.prada.lab.goodthingmap.model.GoodThingsData;
import android.prada.lab.goodthingmap.model.LikeResult;

import org.json.JSONObject;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by prada on 6/28/14.
 */
public interface GoodThingService {
    @GET("/good_thing/mobile/findTopStory")
    GoodThingData getTopStory();

    @GET("/good_thing/mobile/findGoodThings")
    GoodThingsData listStory(@Query("type") int type);

    @POST("/good_thing/mobile/addLike")
    void likeGoodThing(@Query("uid") String uid, @Query("rid") int rid, Callback<LikeResult> callback);
}
