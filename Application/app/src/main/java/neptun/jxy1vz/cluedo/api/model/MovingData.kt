package neptun.jxy1vz.cluedo.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovingData(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("player_id") @Expose var playerId: Int,
    @SerializedName("target_position") @Expose var targetPosition: PositionApiModel
)

data class PositionApiModel(
    @SerializedName("row") @Expose var row: Int,
    @SerializedName("col") @Expose var col: Int
)