package neptun.jxy1vz.hp_cluedo.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DarkHelperPairs")
data class DarkHelperPairDBmodel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "dark_id")
    val darkId: Long,
    @ColumnInfo(name = "helper_id")
    val helperId: Long
)