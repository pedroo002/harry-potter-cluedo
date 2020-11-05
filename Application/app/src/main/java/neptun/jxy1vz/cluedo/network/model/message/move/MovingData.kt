package neptun.jxy1vz.cluedo.network.model.message.move

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovingData(
    @Json(name = "player_id")
    var playerId: Int,
    @Json(name = "target_position")
    var targetPosition: PosData
)

data class PosData(
    @Json(name = "row")
    var row: Int,
    @Json(name = "col")
    var col: Int
)