package android.prada.lab.goodthingmap.network;

import android.prada.lab.goodthingmap.model.GoodThingData;
import android.prada.lab.goodthingmap.model.GoodThingsData;
import android.prada.lab.goodthingmap.model.LikeResult;
import android.prada.lab.goodthingmap.model.CheckinResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by prada on 6/28/14.
 */
public interface GoodThingService {
    @GET("/good_thing/mobile/findTopStory")
    Call<GoodThingData> getTopStory();

    @GET("/good_thing/mobile/findGoodThings")
    Call<GoodThingsData> listStory(@Query("type") int type);

    @GET("/good_thing/mobile/findGoodThings")
    Call<GoodThingsData> listStory(@Query("type") int type, @Query("lat") double latitude, @Query("lon") double longitude);

    @GET("/good_thing/mobile/findGoodThings")
    Call<GoodThingsData> listStory(@Query("lat") double latitude, @Query("lon") double longitude);

    @GET("/good_thing/mobile/findGoodThings")
    Call<GoodThingsData> listStory();

    @GET("/good_thing/mobile/getLikeNum")
    Call<LikeResult> requestLikeNum(@Query("rid") int rid);

    @GET("/good_thing/mobile/getCheckinNum")
    Call<CheckinResult> requestCheckinNum(@Query("rid") int rid);

    @POST("/good_thing/mobile/addCheckin")
    Call<CheckinResult> reportCheckin(@Query("uid") String uid, @Query("rid") int rid, @Query("cid") int checkinId);

    @POST("/good_thing/mobile/addLike")
    Call<LikeResult> likeGoodThing(@Query("uid") String uid, @Query("rid") int rid);

    @POST("/good_thing/mobile/post")
    Call<LikeResult> postComment(@Query("uid") String uid, @Query("rid") int rid, @Query("message") String message);
}
