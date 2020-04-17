package neptun.jxy1vz.cluedo.database.dao

import androidx.room.Dao
import androidx.room.Query
import neptun.jxy1vz.cluedo.database.model.DarkHelperPairDBmodel

@Dao
interface DarkHelperDAO : BaseDAO<DarkHelperPairDBmodel> {
    @Query("SELECT * FROM DarkHelperPairs WHERE id = (:pairId)")
    suspend fun getPairById(pairId: Long): DarkHelperPairDBmodel?

    @Query("SELECT helper_id FROM DarkHelperPairs WHERE dark_id = (:darkId)")
    suspend fun getHelperCardsToDarkCard(darkId: Long): List<Long>?
}