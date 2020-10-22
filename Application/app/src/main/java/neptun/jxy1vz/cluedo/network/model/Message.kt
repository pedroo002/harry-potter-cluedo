package neptun.jxy1vz.cluedo.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterSelectionMessage(
    @Json(name = "message")
    val message: MessageBody
)

@JsonClass(generateAdapter = true)
data class MessageBody(
    @Json(name = "player_name")
    val playerName: String,
    @Json(name = "character_name")
    val characterName: String,
    @Json(name = "token_src")
    val token: Int
)