package neptun.jxy1vz.cluedo.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlayerApiModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("player_id") @Expose var playerId: Int,
    @SerializedName("password_hash") @Expose var password: String
)