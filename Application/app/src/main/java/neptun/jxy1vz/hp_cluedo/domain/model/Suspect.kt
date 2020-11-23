package neptun.jxy1vz.hp_cluedo.domain.model

data class Suspect(
    val playerId: Int,
    var room: String,
    var tool: String,
    var suspect: String
)