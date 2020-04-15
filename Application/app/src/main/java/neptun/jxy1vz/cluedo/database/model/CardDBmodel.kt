package neptun.jxy1vz.cluedo.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Cards",
    foreignKeys = [ForeignKey(
        entity = PlayerDBmodel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("ownerId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CardDBmodel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "obverse")
    val imageRes: Int,
    @ColumnInfo(name = "verso")
    val versoRes: Int,
    @ColumnInfo(name = "type")
    val cardType: CardType,
    @ColumnInfo(name = "owner")
    val ownerId: Long? = null,
    @ColumnInfo(name = "loss")
    val lossType: LossType? = null
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

enum class LossType {
    HP,
    TOOL_CARD,
    SPELL_CARD,
    ALLY_CARD
}