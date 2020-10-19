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

    @GET("/channel/:id")
    suspend fun getChannel(): ChannelApiModel?

    @POST("/channel")
    suspend fun createChannel(@Body body: RequestBody): ChannelApiModel?

    @PUT("/join-channel/:id")
    suspend fun joinChannel(@Body body: RequestBody): ChannelApiModel?

    @PUT("/leave-channel/:id")
    suspend fun leaveChannel(@Body body: RequestBody): ChannelApiModel?

    @DELETE("/channel/:id")
    suspend fun deleteChannel()

    //------------------------------------ Player API requests

    @GET("/player")
    suspend fun getPlayers(): List<PlayerApiModel>?

    @GET("/player/:id")
    suspend fun getPlayer(@Body body: RequestBody): PlayerApiModel?

    @POST("/player")
    suspend fun registerPlayer(@Body body: RequestBody): PlayerApiModel?

    @POST("/login-player")
    suspend fun loginPlayer(@Body body: RequestBody): PlayerApiModel?

    @DELETE("/player/:id")
    suspend fun deletePlayer()

    //------------------------------------ Pusher event triggers

    @POST("/incriminate")
    suspend fun sendIncrimination(@Body body: RequestBody)

    @POST("/accuse")
    suspend fun sendAccusation(@Body body: RequestBody)

    @POST("/move")
    suspend fun sendMovingData(@Body body: RequestBody)

    @POST("/draw-card")
    suspend fun sendCardEvent(@Body body: RequestBody)
}