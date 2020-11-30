package neptun.jxy1vz.hp_cluedo.data.network.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MysteryCardsMessage(
    @Json(name = "message")
    val message: MysteryCardMessageBody
)

@JsonClass(generateAdapter = true)
data class MysteryCardMessageBody(
    val pairs: List<MysteryCardPlayerPair>
)

@JsonClass(generateAdapter = true)
data class MysteryCardPlayerPair(
    @Json(name = "card_name")
    val cardName: String,
    @Json(name = "player_id")
    val ownerPlayerId: Int
)