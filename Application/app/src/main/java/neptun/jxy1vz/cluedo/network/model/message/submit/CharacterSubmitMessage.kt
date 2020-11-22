package neptun.jxy1vz.cluedo.network.model.message.submit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterSubmitMessage(
    @Json(name = "message")
    val message: SubmitMessageBody
)

@JsonClass(generateAdapter = true)
data class SubmitMessageBody(
    @Json(name = "player_name")
    val playerName: String
)