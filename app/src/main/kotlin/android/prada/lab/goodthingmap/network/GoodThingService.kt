package android.prada.lab.goodthingmap.network

import android.prada.lab.goodthingmap.model.CheckinResult
import android.prada.lab.goodthingmap.model.GoodThingData
import android.prada.lab.goodthingmap.model.GoodThingsData
import android.prada.lab.goodthingmap.model.LikeResult
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GoodThingService {
    @GET("/good_thing/mobile/findTopStory")
    suspend fun getTopStory(): GoodThingData

    @GET("/good_thing/mobile/findGoodThings")
    suspend fun listStory(@Query("type") type: Int): GoodThingsData

    @GET("/good_thing/mobile/findGoodThings")
    suspend fun listStory(
        @Query("type") type: Int,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): GoodThingsData

    @GET("/good_thing/mobile/findGoodThings")
    suspend fun listStory(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): GoodThingsData

    @GET("/good_thing/mobile/findGoodThings")
    suspend fun listStory(): GoodThingsData

    @GET("/good_thing/mobile/getLikeNum")
    suspend fun requestLikeNum(@Query("rid") rid: Int): LikeResult

    @GET("/good_thing/mobile/getCheckinNum")
    suspend fun requestCheckinNum(@Query("rid") rid: Int): CheckinResult

    @POST("/good_thing/mobile/addCheckin")
    suspend fun reportCheckin(
        @Query("uid") uid: String,
        @Query("rid") rid: Int,
        @Query("cid") checkinId: Int
    ): CheckinResult

    @POST("/good_thing/mobile/addLike")
    suspend fun likeGoodThing(@Query("uid") uid: String, @Query("rid") rid: Int): LikeResult

    @POST("/good_thing/mobile/post")
    suspend fun postComment(
        @Query("uid") uid: String,
        @Query("rid") rid: Int,
        @Query("message") message: String
    ): LikeResult
}
