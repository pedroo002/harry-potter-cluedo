package neptun.jxy1vz.hp_cluedo.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteDBmodel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val row: Int,
    val col: Int,
    val res: Int
)