package neptun.jxy1vz.hp_cluedo.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cards")
data class CardDBmodel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "obverse")
    val imageRes: String,
    @ColumnInfo(name = "verso")
    val versoRes: String,
    @ColumnInfo(name = "type")
    val cardType: String,
    @ColumnInfo(name = "owner")
    val ownerId: Int? = null,
    @ColumnInfo(name = "loss")
    val lossType: String? = null,
    @ColumnInfo(name = "hp_loss")
    val hpLoss: Int? = null
)

enum class CardType {
    HELPER_TOOL,
    HELPER_SPELL,
    HELPER_ALLY,
    DARK_CORRIDOR,
    DARK_PLAYER_IN_TURN,
    DARK_ROOM_BAGOLYHAZ,
    DARK_ROOM_BAJITALTAN,
    DARK_ROOM_GYENGELKEDO,
    DARK_ROOM_JOSLASTAN,
    DARK_ROOM_KONYVTAR,
    DARK_ROOM_NAGYTEREM,
    DARK_ROOM_SERLEG_TEREM,
    DARK_ROOM_SVK,
    DARK_ROOM_SZUKSEG_SZOBAJA,
    DARK_ALL_PLAYERS,
    DARK_MEN,
    DARK_WOMEN,
    MYSTERY_VENUE,
    MYSTERY_TOOL,
    MYSTERY_SUSPECT,
    PLAYER
}

fun CardType.string(): String {
    return when (this) {
        CardType.HELPER_TOOL -> "HELPER_TOOL"
        CardType.HELPER_SPELL -> "HELPER_SPELL"
        CardType.HELPER_ALLY -> "HELPER_ALLY"
        CardType.DARK_CORRIDOR -> "DARK_CORRIDOR"
        CardType.DARK_PLAYER_IN_TURN -> "DARK_PLAYER_IN_TURN"
        CardType.DARK_ROOM_BAGOLYHAZ -> "DARK_ROOM_BAGOLYHAZ"
        CardType.DARK_ROOM_BAJITALTAN -> "DARK_ROOM_BAJITALTAN"
        CardType.DARK_ROOM_GYENGELKEDO -> "DARK_ROOM_GYENGELKEDO"
        CardType.DARK_ROOM_JOSLASTAN -> "DARK_ROOM_JOSLASTAN"
        CardType.DARK_ROOM_KONYVTAR -> "DARK_ROOM_KONYVTAR"
        CardType.DARK_ROOM_NAGYTEREM -> "DARK_ROOM_NAGYTEREM"
        CardType.DARK_ROOM_SERLEG_TEREM -> "DARK_ROOM_SERLEG_TEREM"
        CardType.DARK_ROOM_SVK -> "DARK_ROOM_SVK"
        CardType.DARK_ROOM_SZUKSEG_SZOBAJA -> "DARK_ROOM_SZUKSEG_SZOBAJA"
        CardType.DARK_ALL_PLAYERS -> "DARK_ALL_PLAYERS"
        CardType.DARK_MEN -> "DARK_MEN"
        CardType.DARK_WOMEN -> "DARK_WOMEN"
        CardType.MYSTERY_VENUE -> "MYSTERY_VENUE"
        CardType.MYSTERY_TOOL -> "MYSTERY_TOOL"
        CardType.MYSTERY_SUSPECT -> "MYSTERY_SUSPECT"
        CardType.PLAYER -> "PLAYER"
    }
}

fun toCardType(s: String): CardType {
    return when (s) {
        "HELPER_TOOL" -> CardType.HELPER_TOOL
        "HELPER_SPELL" -> CardType.HELPER_SPELL
        "HELPER_ALLY" -> CardType.HELPER_ALLY
        "DARK_CORRIDOR" -> CardType.DARK_CORRIDOR
        "DARK_PLAYER_IN_TURN" -> CardType.DARK_PLAYER_IN_TURN
        "DARK_ROOM_BAGOLYHAZ" -> CardType.DARK_ROOM_BAGOLYHAZ
        "DARK_ROOM_BAJITALTAN" -> CardType.DARK_ROOM_BAJITALTAN
        "DARK_ROOM_GYENGELKEDO" -> CardType.DARK_ROOM_GYENGELKEDO
        "DARK_ROOM_JOSLASTAN" -> CardType.DARK_ROOM_JOSLASTAN
        "DARK_ROOM_KONYVTAR" -> CardType.DARK_ROOM_KONYVTAR
        "DARK_ROOM_NAGYTEREM" -> CardType.DARK_ROOM_NAGYTEREM
        "DARK_ROOM_SERLEG_TEREM" -> CardType.DARK_ROOM_SERLEG_TEREM
        "DARK_ROOM_SVK" -> CardType.DARK_ROOM_SVK
        "DARK_ROOM_SZUKSEG_SZOBAJA" -> CardType.DARK_ROOM_SZUKSEG_SZOBAJA
        "DARK_ALL_PLAYERS" -> CardType.DARK_ALL_PLAYERS
        "DARK_MEN" -> CardType.DARK_MEN
        "DARK_WOMEN" -> CardType.DARK_WOMEN
        "MYSTERY_VENUE" -> CardType.MYSTERY_VENUE
        "MYSTERY_TOOL" -> CardType.MYSTERY_TOOL
        "MYSTERY_SUSPECT" -> CardType.MYSTERY_SUSPECT
        else -> CardType.PLAYER
    }
}

enum class LossType {
    HP,
    TOOL_CARD,
    SPELL_CARD,
    ALLY_CARD
}

fun LossType.string(): String {
    return when (this) {
        LossType.HP -> "HP"
        LossType.TOOL_CARD -> "TOOL_CARD"
        LossType.SPELL_CARD -> "SPELL_CARD"
        LossType.ALLY_CARD -> "ALLY_CARD"
    }
}

fun toLossType(s: String): LossType {
    return when (s) {
        "TOOL_CARD" -> LossType.TOOL_CARD
        "SPELL_CARD" -> LossType.SPELL_CARD
        "ALLY_CARD" -> LossType.ALLY_CARD
        else -> LossType.HP
    }
}