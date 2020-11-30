package neptun.jxy1vz.hp_cluedo.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import neptun.jxy1vz.hp_cluedo.data.database.model.CardDBmodel

@Dao
interface CardDAO : BaseDAO<CardDBmodel> {
    @Query("SELECT * FROM Cards")
    suspend fun getCards(): List<CardDBmodel>?

    @Query("SELECT * FROM Cards WHERE id = (:cardId)")
    suspend fun getCardById(cardId: Long): CardDBmodel?

    @Query("SELECT id FROM Cards WHERE name = (:name)")
    suspend fun getCardIdByName(name: String): Long?

    @Query("SELECT * FROM Cards WHERE name = (:name)")
    suspend fun getCardByName(name: String): CardDBmodel?

    @Query("SELECT * FROM Cards WHERE type = (:cardType) AND owner IS NULL")
    suspend fun getCardsByType(cardType: String): List<CardDBmodel>?

    @Query("SELECT * FROM Cards WHERE owner = (:ownerId) and type = (:cardType)")
    suspend fun getCardToOwnerByType(ownerId: Int, cardType: String): CardDBmodel?

    @Query("SELECT * FROM Cards WHERE owner IS NULL AND type LIKE (:prefix)")
    suspend fun getCardBySuperType(prefix: String): List<CardDBmodel>?

    @Query("SELECT * FROM Cards WHERE owner IS NOT NULL AND type LIKE 'MYSTERY_%'")
    suspend fun getUsedMysteryCards(): List<CardDBmodel>?

    @Query("SELECT * FROM Cards WHERE type LIKE 'MYSTERY_%'")
    suspend fun getAllMysteryCards(): List<CardDBmodel>?

    @Query("DELETE FROM Cards")
    suspend fun removeCards()
}