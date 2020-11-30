package neptun.jxy1vz.hp_cluedo.data.network.model.player

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerRequest(
    @Json(name = "player_name")
    val playerName: String,
    @Json(name = "password")
    val password: String
)