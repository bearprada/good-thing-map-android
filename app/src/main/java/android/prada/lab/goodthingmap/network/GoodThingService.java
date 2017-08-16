package android.prada.lab.goodthingmap.network;

import android.prada.lab.goodthingmap.model.GoodThingData;
import android.prada.lab.goodthingmap.model.GoodThingsData;
import android.prada.lab.goodthingmap.model.LikeResult;
import android.prada.lab.goodthingmap.model.CheckinResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by prada on 6/28/14.
 */
public interface GoodThingService {
    @GET("/good_thing/mobile/findTopStory")
    Observable<GoodThingData> getTopStory();

    @GET("/good_thing/mobile/findGoodThings")
    Observable<GoodThingsData> listStory(@Query("type") int type);

    @GET("/good_thing/mobile/findGoodThings")
    Observable<GoodThingsData> listStory(@Query("type") int type, @Query("lat") double latitude, @Query("lon") double longitude);

    @GET("/good_thing/mobile/findGoodThings")
    Observable<GoodThingsData> listStory(@Query("lat") double latitude, @Query("lon") double longitude);

    @GET("/good_thing/mobile/findGoodThings")
    Observable<GoodThingsData> listStory();

    @GET("/good_thing/mobile/getLikeNum")
    Observable<LikeResult> requestLikeNum(@Query("rid") int rid);

    @GET("/good_thing/mobile/getCheckinNum")
    Observable<CheckinResult> requestCheckinNum(@Query("rid") int rid);

    @POST("/good_thing/mobile/addCheckin")
    Observable<CheckinResult> reportCheckin(@Query("uid") String uid, @Query("rid") int rid, @Query("cid") int checkinId);

    @POST("/good_thing/mobile/addLike")
    Observable<LikeResult> likeGoodThing(@Query("uid") String uid, @Query("rid") int rid);

    @POST("/good_thing/mobile/post")
    Observable<LikeResult> postComment(@Query("uid") String uid, @Query("rid") int rid, @Query("message") String message);
}
