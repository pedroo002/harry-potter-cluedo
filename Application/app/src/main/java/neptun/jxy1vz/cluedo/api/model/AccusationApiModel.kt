package neptun.jxy1vz.cluedo.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AccusationApiModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("player_id") @Expose var playerId: Int,
    @SerializedName("suspect_details") @Expose var suspectDetails: SuspectApiModel,
    @SerializedName("player_who_shows") @Expose var playerWhoShows: String
)

data class SuspectApiModel(
    @SerializedName("room") @Expose var room: String,
    @SerializedName("tool") @Expose var tool: String,
    @SerializedName("suspect") @Expose var suspect: String
)