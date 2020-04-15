package neptun.jxy1vz.cluedo.database.dao

import androidx.room.Dao
import androidx.room.Query
import neptun.jxy1vz.cluedo.database.model.CardDBmodel
import neptun.jxy1vz.cluedo.database.model.CardType

@Dao
interface CardDAO : BaseDAO<CardDBmodel> {
    @Query("SELECT * FROM Cards")
    suspend fun getCards(): List<CardDBmodel>?

    @Query("SELECT * FROM Cards WHERE id = (:cardId)")
    suspend fun getCardById(cardId: Long): CardDBmodel?

    @Query("SELECT * FROM Cards WHERE type = (:cardType) AND owner = null")
    suspend fun getCardByType(cardType: CardType): CardDBmodel?

    @Query("SELECT * FROM Cards WHERE owner = (:ownerId) and type = (:cardType)")
    suspend fun getCardToOwnerByType(ownerId: Long, cardType: CardType): CardDBmodel?
}