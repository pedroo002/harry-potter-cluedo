package neptun.jxy1vz.cluedo.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChannelApiModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("channel_name") @Expose var channelName: String,
    @SerializedName("auth_key") @Expose var authorizationKey: Int,
    @SerializedName("max_user") @Expose var maxUser: Int,
    @SerializedName("subscribed_users") @Expose var subscribedUsers: List<PlayerApiModel> //List<String>
)