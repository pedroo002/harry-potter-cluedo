package neptun.jxy1vz.cluedo.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerApiModel(
    @Json(name = "player_id")
    var playerId: Int,
    @Json(name = "logged_in")
    val loggedIn: Boolean,
    @Json(name = "_id")
    var id: String,
    @Json(name = "player_name")
    var name: String,
    @Json(name = "password")
    @Transient
    var password: String = "",
    @Json(name = "__v")
    @Transient
    var version: Int = 0
)

@JsonClass(generateAdapter = true)
data class PlayerRequest(
    @Json(name = "player_name")
    val playerName: String,
    @Json(name = "password")
    val password: String
)

data class PlayerDomainModel(
    val playerName: String,
    var selectedCharacter: String,
    var playerId: Int
)