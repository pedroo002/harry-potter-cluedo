package neptun.jxy1vz.hp_cluedo.database.dao

import androidx.room.Dao
import androidx.room.Query
import neptun.jxy1vz.hp_cluedo.database.model.PlayerDBmodel

@Dao
interface PlayerDAO : BaseDAO<PlayerDBmodel> {
    @Query("SELECT * FROM players")
    suspend fun getPlayers(): List<PlayerDBmodel>?

    @Query("DELETE FROM players")
    suspend fun deletePlayers()
}