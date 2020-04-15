package neptun.jxy1vz.cluedo.database.dao

import androidx.room.Dao
import androidx.room.Query
import neptun.jxy1vz.cluedo.database.model.Gender
import neptun.jxy1vz.cluedo.database.model.PlayerDBmodel

@Dao
interface PlayerDAO : BaseDAO<PlayerDBmodel> {
    @Query("SELECT * FROM Players")
    suspend fun getPlayers(): List<PlayerDBmodel>?

    @Query("SELECT * FROM Players WHERE id = (:playerId)")
    suspend fun getPlayerById(playerId: Long): PlayerDBmodel?

    @Query("SELECT * FROM Players WHERE gender = (:gender)")
    suspend fun getPlayersByGender(gender: Gender): List<PlayerDBmodel>?
}