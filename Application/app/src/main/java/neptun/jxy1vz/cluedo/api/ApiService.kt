package neptun.jxy1vz.cluedo.api

import neptun.jxy1vz.cluedo.api.model.AccusationApiModel
import neptun.jxy1vz.cluedo.api.model.ChannelApiModel
import neptun.jxy1vz.cluedo.api.model.PlayerApiModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //------------------------------------ Channel API requests

    @GET("/channel")
    fun getChannels(): Call<List<ChannelApiModel>>

    @GET("/channel/:id")
    fun getChannel(): Call<ChannelApiModel>

    @POST("/channel")
    fun createChannel(@Body body: RequestBody): Call<ChannelApiModel>

    @PUT("/join-channel/:id")
    fun joinChannel(@Body body: RequestBody): Call<ChannelApiModel>

    @PUT("/leave-channel/:id")
    fun leaveChannel(@Body body: RequestBody): Call<ChannelApiModel>

    @DELETE("/channel/:id")
    fun deleteChannel()

    //------------------------------------ Player API requests

    @GET("/player")
    fun getPlayers(): Call<List<PlayerApiModel>>

    @GET("/player/:id")
    fun getPlayer(@Body body: RequestBody): Call<PlayerApiModel>

    @POST("/player")
    fun registerPlayer(@Body body: RequestBody): Call<PlayerApiModel>

    @POST("/login-player")
    fun loginPlayer(@Body body: RequestBody)

    @DELETE("/player/:id")
    fun deletePlayer()

    //------------------------------------ Pusher event triggers

    @POST("/incriminate")
    fun sendIncrimination(@Body body: RequestBody)

    @POST("/accuse")
    fun sendAccusation(@Body body: RequestBody)

    @POST("/move")
    fun sendMovingData(@Body body: RequestBody)

    @POST("/draw-card")
    fun sendCardEvent(@Body body: RequestBody)
}