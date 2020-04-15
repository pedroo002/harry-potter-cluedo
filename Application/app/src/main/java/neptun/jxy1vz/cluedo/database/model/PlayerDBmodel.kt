package neptun.jxy1vz.cluedo.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Players",
    foreignKeys = [ForeignKey(
        entity = CardDBmodel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("cardId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PlayerDBmodel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "card_id")
    val cardId: Long,
    val gender: Gender,
    @ColumnInfo(name = "tile")
    val tileRes: Int
)

enum class Gender {
    MAN,
    WOMAN
}