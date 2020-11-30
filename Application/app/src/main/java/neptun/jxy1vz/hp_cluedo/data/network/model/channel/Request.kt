package neptun.jxy1vz.hp_cluedo.data.network.model.channel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChannelRequest(
    @Json(name = "channel_name")
    val name: String,
    @Json(name = "auth_key")
    val auth: String,
    @Json(name = "max_user")
    val maxUser: String
)

@JsonClass(generateAdapter = true)
data class JoinRequest(
    @Json(name = "player_name")
    val playerName: String,
    @Json(name = "auth_key")
    val authKey: String
)