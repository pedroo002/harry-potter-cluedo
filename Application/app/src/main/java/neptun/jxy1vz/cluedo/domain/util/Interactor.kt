package neptun.jxy1vz.cluedo.domain.util

import neptun.jxy1vz.cluedo.database.CluedoDatabase
import neptun.jxy1vz.cluedo.database.model.CardDBmodel
import neptun.jxy1vz.cluedo.database.model.DarkHelperPairDBmodel
import neptun.jxy1vz.cluedo.database.model.NoteDBmodel

class Interactor(private val db: CluedoDatabase) {

    suspend fun insertIntoNotes(notes: List<NoteDBmodel>) {
        return db.noteDao().insertIntoTable(notes)
    }

    suspend fun getNotes(): List<NoteDBmodel>? {
        return db.noteDao().getNotes()
    }

    suspend fun eraseNotes() {
        return db.noteDao().eraseNotes()
    }

    suspend fun insertIntoCards(card: CardDBmodel): Long {
        return db.cardDao().insertIntoTable(card)
    }

    suspend fun updateCards(card: CardDBmodel) {
        db.cardDao().updateTable(card)
    }

    suspend fun insertIntoDarkHelperPairs(pair: DarkHelperPairDBmodel): Long {
        return db.darkHelperDao().insertIntoTable(pair)
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

    suspend fun getCardBySuperType(prefix: String): List<CardDBmodel>? {
        return db.cardDao().getCardBySuperType(prefix)
    }

    suspend fun getUsedMysteryCards(): List<CardDBmodel>? {
        return db.cardDao().getUsedMysteryCards()
    }

    suspend fun getHelperCardsToDarkCard(darkId: Long): List<Long>? {
        return db.darkHelperDao().getHelperCardsToDarkCard(darkId)
    }
}