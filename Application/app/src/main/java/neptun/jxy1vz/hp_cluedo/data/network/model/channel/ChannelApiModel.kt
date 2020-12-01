package neptun.jxy1vz.hp_cluedo.data.network.model.channel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChannelApiModel(
    @Json(name = "_id") var id: String,
    @Json(name = "channel_name") var channelName: String,
    @Json(name = "auth_key") var authorizationKey: Int,
    @Json(name = "max_user") var maxUser: Int,
    @Json(name = "subscribed_users") var subscribedUsers: List<String>,
    @Json(name = "is_waiting") var isWaiting: Boolean,
    @Json(name = "__v") @Transient var version: Int = 0
)