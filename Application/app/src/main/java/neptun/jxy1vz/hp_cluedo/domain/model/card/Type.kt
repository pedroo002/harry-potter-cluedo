package neptun.jxy1vz.hp_cluedo.domain.model.card

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