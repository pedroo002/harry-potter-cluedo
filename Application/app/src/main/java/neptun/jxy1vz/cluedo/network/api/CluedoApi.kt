package neptun.jxy1vz.cluedo.network.api

import neptun.jxy1vz.cluedo.network.model.channel.ChannelApiModel
import neptun.jxy1vz.cluedo.network.model.player.PlayerApiModel
import okhttp3.RequestBody
import retrofit2.http.*

interface CluedoApi {

    //------------------------------------ Channel API requests

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

    @POST("/player")
    suspend fun registerPlayer(@Body body: RequestBody): PlayerApiModel?

    @PUT("/login-player")
    suspend fun loginPlayer(@Body body: RequestBody): PlayerApiModel?

    @PUT("/logout-player")
    suspend fun logoutPlayer(@Body body: RequestBody): String?

    //------------------------------------ Pusher event triggers

    @POST("/incriminate")
    suspend fun sendIncrimination(@Query("channel_name") channelName: String, @Body incrimination: RequestBody)

    @POST("/accuse")
    suspend fun sendAccusation(@Query("channel_name") channelName: String, @Body accusation: RequestBody)

    @POST("/move")
    suspend fun sendMovingData(@Query("channel_name") channelName: String, @Body movingData: RequestBody)

    @POST("/dice")
    suspend fun sendDiceEvent(@Query("channel_name") channelName: String, @Body diceData: RequestBody)

    @POST("/draw-card")
    suspend fun sendCardEvent(@Query("channel_name") channelName: String, @Query("player_id") playerId: Int, @Query("card_name") cardName: String)

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
    suspend fun readyToLoadMap(@Query("channel_name") channelName: String, @Query("player_name") playerName: String)

    @POST("/map-loaded")
    suspend fun notifyMapLoaded(@Query("channel_name") channelName: String)

    @POST("/mystery-cards-activity-loaded")
    suspend fun notifyMysteryCardsLoaded(@Query("channel_name") channelName: String)

    @POST("/dark-cards-ready")
    suspend fun notifyDarkCardsReady(@Query("channel_name") channelName: String)

    @POST("/dark-cards-close")
    suspend fun notifyDarkCardsClose(@Query("channel_name") channelName: String)

    @POST("/throw-helper-card")
    suspend fun sendCardThrowEvent(@Query("channel_name") channelName: String, @Query("player_id") playerId: Int, @Query("card_name") cardName: String)

    @POST("/incrimination-details-ready")
    suspend fun notifyIncriminationDetailsReady(@Query("channel_name") channelName: String)

    @POST("/trigger-card-reveal")
    suspend fun triggerPlayerToReveal(@Query("channel_name") channelName: String, @Query("player_id") playerId: Int)

    @POST("/skip-reveal")
    suspend fun skipCardReveal(@Query("channel_name") channelName: String, @Query("player_id") playerId: Int)

    @POST("/show-helper-card")
    suspend fun showCard(@Query("channel_name") channelName: String, @Query("player_id") playerId: Int, @Query("card_name") cardName: String)

    @POST("/no-card")
    suspend fun notifyNobodyCouldShow(@Query("channel_name") channelName: String)

    @POST("/incrimination-finished")
    suspend fun notifyIncriminationFinished(@Query("channel_name") channelName: String)

    @POST("/note-closed")
    suspend fun notifyNoteClosed(@Query("channel_name") channelName: String)
}