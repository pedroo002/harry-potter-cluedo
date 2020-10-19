package neptun.jxy1vz.cluedo.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChannelApiModel(
    @Json(name = "_id") var id: String,
    @Json(name = "channel_name") var channelName: String,
    @Json(name = "auth_key") var authorizationKey: Int,
    @Json(name = "max_user") var maxUser: Int,
    @Json(name = "subscribed_users") var subscribedUsers: List<PlayerApiModel>
)

@JsonClass(generateAdapter = true)
data class ChannelRequest(
    @Json(name = "channel_name")
    val name: String,
    @Json(name = "auth_key")
    val auth: String,
    @Json(name = "mak_user")
    val maxUser: String
)