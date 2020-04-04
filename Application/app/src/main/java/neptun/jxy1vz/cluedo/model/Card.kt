package neptun.jxy1vz.cluedo.model

import androidx.annotation.DrawableRes

interface Card {
    val id: Int
    val name: String
    val imageRes: DrawableRes
    val verso: DrawableRes
}

data class HelperCard(
    val id: Int,
    val name: String,
    val imageRes: DrawableRes,
    val verso: DrawableRes,
    val type: HelperType,
    val count: Int = 1,
    val extra: String? = null
)

data class MysteryCard(
    val id: Int,
    val name: String,
    val imageRes: DrawableRes,
    val verso: DrawableRes,
    val type: MysteryType
)

data class DarkCard(
    val id: Int,
    val name: String,
    val imageRes: DrawableRes,
    val verso: DrawableRes,
    val type: DarkType,
    val lossType: LossType,
    val hpLoss: Int,
    val helper: List<HelperCard>? = null
)

data class PlayerCard(
    val id: Int,
    val name: String,
    val imageRes: DrawableRes,
    val verso: DrawableRes,
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