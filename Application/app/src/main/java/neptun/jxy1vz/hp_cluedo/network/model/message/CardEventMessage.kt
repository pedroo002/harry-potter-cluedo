package neptun.jxy1vz.hp_cluedo.network.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CardEventMessage(
    @Json(name = "player_id")
    val playerId: Int,
    @Json(name = "card_name")
    val cardName: String
)