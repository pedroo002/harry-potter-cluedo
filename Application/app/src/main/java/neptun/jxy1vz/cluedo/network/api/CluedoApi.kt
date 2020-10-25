package neptun.jxy1vz.cluedo.network.api

import neptun.jxy1vz.cluedo.network.model.channel.ChannelApiModel
import neptun.jxy1vz.cluedo.network.model.player.PlayerApiModel
import okhttp3.RequestBody
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

    @PUT("/stop-waiting/{id}")
    suspend fun stopChannelWaiting(@Path("id") channelId: String): ChannelApiModel?

    @PUT("/join-channel/{id}")
    suspend fun joinChannel(@Path("id") id: String, @Body body: RequestBody): ChannelApiModel?

    @PUT("/leave-channel/{id}")
    suspend fun leaveChannel(@Path("id") id: String, @Query("player_name") playerName: String): ChannelApiModel?

    @DELETE("/channel/{id}")
    suspend fun deleteChannel(@Path("id") channelId: String)

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

    @POST("/game-ready")
    suspend fun notifyGameReady(@Query("channel_name") channelName: String)

    @POST("/character-selected")
    suspend fun notifyCharacterSelected(@Query("channel_name") channelName: String, @Query("player_name") playerName: String, @Query("character_name") characterName: String, @Query("token_src") tokenImageSource: Int)

    @POST("/character-submit")
    suspend fun notifyCharacterSubmit(@Query("channel_name") channelName: String, @Query("player_name") playerName: String)

    @POST("/channel-removed-before-join")
    suspend fun notifyChannelRemovedBeforeJoin(@Query("channel_name") channelName: String)

    @POST("/channel-removed-after-join")
    suspend fun notifyChannelRemovedAfterJoin(@Query("channel_name") channelName: String)

    @POST("/player-leaves")
    suspend fun notifyPlayerLeaves(@Query("channel_name") channelName: String, @Query("player_name") playerName: String)

    @POST("/player-arrives")
    suspend fun notifyPlayerArrives(@Query("channel_name") channelName: String, @Query("player_name") playerName: String)

    @POST("/mystery-pairs")
    suspend fun sendMysteryCardPairs(@Query("channel_name") channelName: String, @Body body: RequestBody)

    @POST("/ready-to-game")
    suspend fun readyToLoadMap(@Query("channel_name") channelName: String)
}