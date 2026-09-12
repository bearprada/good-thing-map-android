package android.prada.lab.goodthingmap.network

import android.prada.lab.goodthingmap.model.CheckinResult
import android.prada.lab.goodthingmap.model.GoodThingData
import android.prada.lab.goodthingmap.model.GoodThingsData
import android.prada.lab.goodthingmap.model.LikeResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GoodThingService {
    @GET("/good_thing/mobile/findTopStory")
    fun getTopStory(): Observable<GoodThingData>

    @GET("/good_thing/mobile/findGoodThings")
    fun listStory(@Query("type") type: Int): Observable<GoodThingsData>

    @GET("/good_thing/mobile/findGoodThings")
    fun listStory(
        @Query("type") type: Int,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Observable<GoodThingsData>

    @GET("/good_thing/mobile/findGoodThings")
    fun listStory(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Observable<GoodThingsData>

    @GET("/good_thing/mobile/findGoodThings")
    fun listStory(): Observable<GoodThingsData>

    @GET("/good_thing/mobile/getLikeNum")
    fun requestLikeNum(@Query("rid") rid: Int): Observable<LikeResult>

    @GET("/good_thing/mobile/getCheckinNum")
    fun requestCheckinNum(@Query("rid") rid: Int): Observable<CheckinResult>

    @POST("/good_thing/mobile/addCheckin")
    fun reportCheckin(
        @Query("uid") uid: String,
        @Query("rid") rid: Int,
        @Query("cid") checkinId: Int
    ): Observable<CheckinResult>

    @POST("/good_thing/mobile/addLike")
    fun likeGoodThing(@Query("uid") uid: String, @Query("rid") rid: Int): Observable<LikeResult>

    @POST("/good_thing/mobile/post")
    fun postComment(
        @Query("uid") uid: String,
        @Query("rid") rid: Int,
        @Query("message") message: String
    ): Observable<LikeResult>
}
