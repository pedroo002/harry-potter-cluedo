package neptun.jxy1vz.hp_cluedo.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import neptun.jxy1vz.hp_cluedo.data.database.model.DarkHelperPairDBmodel

@Dao
interface DarkHelperDAO : BaseDAO<DarkHelperPairDBmodel> {
    @Query("SELECT helper_id FROM DarkHelperPairs WHERE dark_id = (:darkId)")
    suspend fun getHelperCardsToDarkCard(darkId: Long): List<Long>?
}