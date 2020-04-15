package neptun.jxy1vz.cluedo.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "DarkHelperPairs",
    foreignKeys = [ForeignKey(
        entity = CardDBmodel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("darkId"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = CardDBmodel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("helperId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class DarkHelperPairDBmodel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "dark_id")
    val darkId: Long,
    @ColumnInfo(name = "helper_id")
    val helperId: Long
)