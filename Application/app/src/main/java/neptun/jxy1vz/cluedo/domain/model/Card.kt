package neptun.jxy1vz.cluedo.domain.model

abstract class Card(
    open val id: Int,
    open val name: String,
    open val imageRes: Int,
    open val verso: Int
)

data class HelperCard(
    override val id: Int,
    override val name: String,
    override val imageRes: Int,
    override val verso: Int,
    val type: CardType,
    var count: Int = 1,
    var numberOfHelpingCases: Int = 0
) : Card(id, name, imageRes, verso)

data class MysteryCard(
    override val id: Int,
    override val name: String,
    override val imageRes: Int,
    override val verso: Int,
    val type: CardType
) : Card(id, name, imageRes, verso)

data class DarkCard(
    override val id: Int,
    override val name: String,
    override val imageRes: Int,
    override val verso: Int,
    val type: CardType,
    val lossType: LossType,
    val hpLoss: Int,
    var helperIds: List<Int>? = null
) : Card(id, name, imageRes, verso)

data class PlayerCard(
    override val id: Int,
    override val name: String,
    override val imageRes: Int,
    override val verso: Int
) : Card(id, name, imageRes, verso)

interface CardType

enum class HelperType : CardType {
    TOOL,
    ALLY,
    SPELL
}

enum class MysteryType : CardType {
    TOOL,
    SUSPECT,
    VENUE
}

enum class DarkType : CardType {
    CORRIDOR,
    PLAYER_IN_TURN,
    ROOM_BAGOLYHAZ,
    ROOM_BAJITALTAN,
    ROOM_GYENGELKEDO,
    ROOM_JOSLASTAN,
    ROOM_KONYVTAR,
    ROOM_NAGYTEREM,
    ROOM_SERLEG,
    ROOM_SVK,
    ROOM_SZUKSEG_SZOBAJA,
    ALL_PLAYERS,
    GENDER_MEN,
    GENDER_WOMEN
}

enum class LossType {
    HP,
    TOOL,
    ALLY,
    SPELL
}

fun HelperType.compareTo(lossType: LossType): Boolean {
    return (this == HelperType.TOOL && lossType == LossType.TOOL) || (this == HelperType.SPELL && lossType == LossType.SPELL) || (this == HelperType.ALLY && lossType == LossType.ALLY)
}