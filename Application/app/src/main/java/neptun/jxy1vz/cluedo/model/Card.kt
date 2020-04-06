package neptun.jxy1vz.cluedo.model

interface Card {
    val id: Int
    val name: String
    val imageRes: Int
    val verso: Int
}

data class HelperCard(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val verso: Int,
    val type: HelperType,
    var count: Int = 1,
    val extra: String? = null
)

data class MysteryCard(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val verso: Int,
    val type: MysteryType
)

data class DarkCard(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val verso: Int,
    val type: DarkType,
    val lossType: LossType,
    val hpLoss: Int,
    val helperIds: List<Int>? = null
)

data class PlayerCard(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val verso: Int,
    val sex: Sex,
    val color: PlayerColor
)

enum class HelperType {
    TOOL,
    ALLY,
    SPELL
}

enum class MysteryType {
    TOOL,
    SUSPECT,
    VENUE
}

enum class DarkType {
    CORRIDOR,
    PLAYER_IN_TURN,
    ROOM,
    ALL_PLAYERS,
    SEX
}

enum class LossType {
    HP,
    TOOL,
    ALLY,
    SPELL
}

enum class Sex {
    MAN,
    WOMAN
}

enum class PlayerColor {
    BLUE,
    PURPLE,
    RED,
    WHITE,
    GREEN,
    YELLOW
}

fun HelperType.compareTo(lossType: LossType): Boolean {
    return (this == HelperType.TOOL && lossType == LossType.TOOL) || (this == HelperType.SPELL && lossType == LossType.SPELL) || (this == HelperType.ALLY && lossType == LossType.ALLY)
}