package neptun.jxy1vz.hp_cluedo.network.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterSelectionMessage(
    @Json(name = "message")
    val message: SelectionMessageBody
)

@JsonClass(generateAdapter = true)
data class SelectionMessageBody(
    @Json(name = "player_name")
    val playerName: String,
    @Json(name = "character_name")
    val characterName: String
)

@JsonClass(generateAdapter = true)
data class CatchUpMessage(
    @Json(name = "target_player")
    val targetPlayer: String,
    @Json(name = "selections")
    val selections: List<SelectionMessageBody>
)