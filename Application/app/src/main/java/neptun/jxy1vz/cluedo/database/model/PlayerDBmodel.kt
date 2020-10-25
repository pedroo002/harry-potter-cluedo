package neptun.jxy1vz.cluedo.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerDBmodel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val playerName: String,
    val playerId: Int,
    val characterName: String
)