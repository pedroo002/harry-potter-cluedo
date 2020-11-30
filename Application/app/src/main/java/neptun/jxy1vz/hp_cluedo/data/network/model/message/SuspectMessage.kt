package neptun.jxy1vz.hp_cluedo.data.network.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SuspectMessage(
    @Json(name = "player_id")
    val playerId: Int,
    @Json(name = "room")
    val room: String,
    @Json(name = "tool")
    val tool: String,
    @Json(name = "suspect")
    val suspect: String
)