package neptun.jxy1vz.cluedo.network.api

import neptun.jxy1vz.cluedo.network.model.ChannelApiModel
import neptun.jxy1vz.cluedo.network.model.PlayerApiModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CluedoApi {

    //------------------------------------ Channel API requests

    @GET("/channel")
    suspend fun getChannels(): List<ChannelApiModel>?

    @GET("/channel-by-limit")
    suspend fun getChannelsByPlayerLimit(@Query("max_user") limit: Int): List<ChannelApiModel>?

    @GET("/channel/{id}")
    suspend fun getChannel(@Path("id") id: String): ChannelApiModel?

    @POST("/channel")
    suspend fun createChannel(@Body body: RequestBody): ChannelApiModel?

    @PUT("/join-channel/{id}")
    suspend fun joinChannel(@Path("id") id: String, @Body body: RequestBody): ChannelApiModel?

    @PUT("/leave-channel/{id}")
    suspend fun leaveChannel(@Path("id") id: String, @Query("player_name") playerName: String): ChannelApiModel?

    @DELETE("/channel/{id}")
    suspend fun deleteChannel()

    //------------------------------------ Player API requests

    @GET("/player")
    suspend fun getPlayers(): List<PlayerApiModel>?

    @GET("/player/{id}")
    suspend fun getPlayer(@Path("id") id: String, @Body body: RequestBody): PlayerApiModel?

    @POST("/player")
    suspend fun registerPlayer(@Body body: RequestBody): PlayerApiModel?

    @PUT("/login-player")
    suspend fun loginPlayer(@Body body: RequestBody): PlayerApiModel?

    @PUT("/logout-player")
    suspend fun logoutPlayer(@Body body: RequestBody): String?

    @DELETE("/player/{id}")
    suspend fun deletePlayer(@Path("id") id: String)

    //------------------------------------ Pusher event triggers

    @POST("/incriminate")
    suspend fun sendIncrimination(@Query("channel_name") channelName: String)

    @POST("/accuse")
    suspend fun sendAccusation(@Query("channel_name") channelName: String)

    @POST("/move")
    suspend fun sendMovingData(@Query("channel_name") channelName: String)

    @POST("/draw-card")
    suspend fun sendCardEvent(@Query("channel_name") channelName: String)

    @POST("/ready")
    suspend fun notifyGameReady(@Query("channel_name") channelName: String)

    @POST("/character-selected")
    suspend fun notifyCharacterSelected(@Query("channel_name") channelName: String, @Query("player_name") playerName: String, @Query("character_name") characterName: String, @Query("token_src") tokenImageSource: Int)
}