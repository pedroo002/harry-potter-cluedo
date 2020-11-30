package neptun.jxy1vz.hp_cluedo.data.network.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerPresenceMessage(
    @Json(name = "message")
    val message: PresenceMessageBody
)

@JsonClass(generateAdapter = true)
data class PresenceMessageBody(
    @Json(name = "player_name")
    val playerName: String
)