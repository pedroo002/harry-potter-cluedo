package neptun.jxy1vz.hp_cluedo.network.model.message.dice

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiceDataMessage(
    @Json(name = "player_id")
    val playerId: Int,
    @Json(name = "first_value")
    val dice1: Int,
    @Json(name = "second_value")
    val dice2: Int,
    @Json(name = "extra_value")
    val hogwartsDice: Int
)