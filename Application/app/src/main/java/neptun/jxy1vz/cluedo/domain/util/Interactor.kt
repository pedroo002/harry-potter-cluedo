package neptun.jxy1vz.cluedo.domain.util

import neptun.jxy1vz.cluedo.database.CluedoDatabase
import neptun.jxy1vz.cluedo.database.model.CardDBmodel
import neptun.jxy1vz.cluedo.database.model.DarkHelperPairDBmodel

class Interactor(private val db: CluedoDatabase) {

    suspend fun insertIntoCards(card: CardDBmodel): Long {
        return db.cardDao().insertIntoTable(card)
    }

    suspend fun insertIntoCards(cards: List<CardDBmodel>?) {
        db.cardDao().insertIntoTable(cards)
    }

    suspend fun updateCards(card: CardDBmodel) {
        db.cardDao().updateTable(card)
    }

    suspend fun deleteCard(card: CardDBmodel) {
        db.cardDao().deleteItem(card)
    }

    suspend fun insertIntoDarkHelperPairs(pair: DarkHelperPairDBmodel): Long {
        return db.darkHelperDao().insertIntoTable(pair)
    }

    suspend fun insertIntoDarkHelperPairs(pairs: List<DarkHelperPairDBmodel>?) {
        db.darkHelperDao().insertIntoTable(pairs)
    }

    suspend fun updateDarkHelperPairs(pair: DarkHelperPairDBmodel) {
        db.darkHelperDao().updateTable(pair)
    }

    suspend fun deleteDarkHelperPair(pair: DarkHelperPairDBmodel) {
        db.darkHelperDao().deleteItem(pair)
    }

    suspend fun getCards(): List<CardDBmodel>? {
        return db.cardDao().getCards()
    }

    suspend fun getCardById(cardId: Long): CardDBmodel? {
        return db.cardDao().getCardById(cardId)
    }

    suspend fun getCardByName(name: String): CardDBmodel? {
        return db.cardDao().getCardByName(name)
    }

    suspend fun getCardIdByName(name: String): Long? {
        return db.cardDao().getCardIdByName(name)
    }

    suspend fun getCardsByType(cardType: String): List<CardDBmodel>? {
        return db.cardDao().getCardsByType(cardType)
    }

    suspend fun getCardToOwnerByType(ownerId: Int, cardType: String): CardDBmodel? {
        return db.cardDao().getCardToOwnerByType(ownerId, cardType)
    }

    suspend fun getCardBySuperType(prefix: String): CardDBmodel? {
        return db.cardDao().getCardBySuperType(prefix)
    }

    suspend fun getCurrentPlayerIds(): List<Int>? {
        return db.cardDao().getCurrentPlayerIds()
    }

    suspend fun getSolution(): List<CardDBmodel>? {
        return db.cardDao().getSolution()
    }

    suspend fun getPairs(): List<DarkHelperPairDBmodel>? {
        return db.darkHelperDao().getPairs()
    }

    suspend fun getPairById(pairId: Long): DarkHelperPairDBmodel? {
        return db.darkHelperDao().getPairById(pairId)
    }

    suspend fun getHelperCardsToDarkCard(darkId: Long): List<Long>? {
        return db.darkHelperDao().getHelperCardsToDarkCard(darkId)
    }
}