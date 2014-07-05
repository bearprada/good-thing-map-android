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
    void listStory(@Query("type") int type, Callback<GoodThingsData> callback);

    @GET("/good_thing/mobile/findGoodThings")
    void listStory(@Query("type") int type, @Query("lat") double latitude, @Query("lon") double longitude
        ,Callback<GoodThingsData> callback);

    @GET("/good_thing/mobile/findGoodThings")
    void listStory(@Query("lat") double latitude, @Query("lon") double longitude, Callback<GoodThingsData> callback);

    @GET("/good_thing/mobile/findGoodThings")
    void listStory(Callback<GoodThingsData> callback);

    @POST("/good_thing/mobile/addLike")
    void likeGoodThing(@Query("uid") String uid, @Query("rid") int rid, Callback<LikeResult> callback);
}
