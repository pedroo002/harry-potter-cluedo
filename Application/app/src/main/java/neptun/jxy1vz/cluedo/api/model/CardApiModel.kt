package neptun.jxy1vz.cluedo.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CardApiModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("player_id") @Expose var playerId: Int,
    @SerializedName("card_name") @Expose var cardName: String,
    @SerializedName("card_type") @Expose var cardType: String
)