package neptun.jxy1vz.hp_cluedo.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerDBmodel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "player_name") val playerName: String,
    @ColumnInfo(name = "player_id") val playerId: Int,
    @ColumnInfo(name = "character_name") val characterName: String,
    @ColumnInfo(name = "channel_name") val channelName: String
)