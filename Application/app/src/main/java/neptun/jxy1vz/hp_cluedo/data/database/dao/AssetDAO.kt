package neptun.jxy1vz.hp_cluedo.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetDBmodel

@Dao
interface AssetDAO : BaseDAO<AssetDBmodel> {
    @Query("SELECT * FROM Assets WHERE tag = (:tag)")
    suspend fun getAssetByTag(tag: String): AssetDBmodel?

    @Query("SELECT * FROM Assets WHERE url = (:url)")
    suspend fun getAssetByUrl(url: String): AssetDBmodel?

    @Query("SELECT * FROM Assets WHERE tag LIKE (:prefix)")
    suspend fun getAssetsByPrefix(prefix: String): List<AssetDBmodel>?

    @Query("DELETE FROM Assets")
    suspend fun removeAssets()
}