package neptun.jxy1vz.cluedo.network.model.message.selection

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
    val characterName: String,
    @Json(name = "token_src")
    val token: Int
)

@JsonClass(generateAdapter = true)
data class CatchUpMessage(
    @Json(name = "selection_count")
    val selectionCount: Int,
    @Json(name = "selections")
    val selections: List<SelectionMessageBody>
)