package neptun.jxy1vz.hp_cluedo.domain.util

import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetDBmodel
import neptun.jxy1vz.hp_cluedo.data.database.model.CardDBmodel
import neptun.jxy1vz.hp_cluedo.data.database.model.DarkHelperPairDBmodel
import neptun.jxy1vz.hp_cluedo.data.database.model.NoteDBmodel
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.data.network.model.asset_list.AssetCount
import neptun.jxy1vz.hp_cluedo.data.network.model.asset_list.AssetList

class Interactor(private val db: CluedoDatabase, private val retrofit: RetrofitInstance) {

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

    suspend fun getAllMysteryCards(): List<CardDBmodel>? {
        return db.cardDao().getAllMysteryCards()
    }

    suspend fun insertIntoAssets(asset: AssetDBmodel) {
        db.assetDao().insertIntoTable(asset)
    }

    suspend fun getAssetsByPrefix(prefix: String): List<AssetDBmodel>? {
        return db.assetDao().getAssetsByPrefix(prefix)
    }

    suspend fun getAssetByUrl(url: String): AssetDBmodel? {
        return db.assetDao().getAssetByUrl(url)
    }

    suspend fun getDarkCardsFromServer(): AssetList? {
        return retrofit.cluedo.getDarkCardsFromServer()
    }

    suspend fun getHelperCardsFromServer(): AssetList? {
        return retrofit.cluedo.getHelperCardsFromServer()
    }

    suspend fun getMysteryCardsFromServer(): AssetList? {
        return retrofit.cluedo.getMysteryCardsFromServer()
    }

    suspend fun getPlayerCardsFromServer(): AssetList? {
        return retrofit.cluedo.getPlayerCardsFromServer()
    }

    suspend fun getDarkMarkAssetsFromServer(): AssetList? {
        return retrofit.cluedo.getDarkMarkAssetsFromServer()
    }

    suspend fun getDarkCardFragmentAssetsFromServer(): AssetList? {
        return retrofit.cluedo.getDarkCardFragmentAssetsFromServer()
    }

    suspend fun getDiceAssetsFromServer(): AssetList? {
        return retrofit.cluedo.getDiceAssetsFromServer()
    }

    suspend fun getDoorAssetsFromServer(): AssetList? {
        return retrofit.cluedo.getDoorAssetsFromServer()
    }

    suspend fun getFootprintsFromServer(): AssetList? {
        return retrofit.cluedo.getFootprintsFromServer()
    }

    suspend fun getGatewaysFromServer(): AssetList? {
        return retrofit.cluedo.getGatewaysFromServer()
    }

    suspend fun getNoteAssetsFromServer(): AssetList? {
        return retrofit.cluedo.getNoteAssetsFromServer()
    }

    suspend fun getMapRelatedAssetsFromServer(): AssetList? {
        return retrofit.cluedo.getMapRelatedAssetsFromServer()
    }

    suspend fun getSelectionAssetsFromServer(): AssetList? {
        return retrofit.cluedo.getSelectionAssetsFromServer()
    }

    suspend fun getTilesFromServer(): AssetList? {
        return retrofit.cluedo.getTilesFromServer()
    }

    suspend fun getMenuRelatedAssetsFromServer(): AssetList? {
        return retrofit.cluedo.getMenuRelatedAssetsFromServer()
    }

    suspend fun getTutorialAssetsFromServer(): AssetList? {
        return retrofit.cluedo.getTutorialAssetsFromServer()
    }

    suspend fun getMysteryRoomTokensFromServer(): AssetList? {
        return retrofit.cluedo.getMysteryRoomTokensFromServer()
    }

    suspend fun getMysteryToolTokensFromServer(): AssetList? {
        return retrofit.cluedo.getMysteryToolTokensFromServer()
    }

    suspend fun getMysterySuspectTokensFromServer(): AssetList? {
        return retrofit.cluedo.getMysterySuspectTokensFromServer()
    }

    suspend fun getPlayerTokensFromServer(): AssetList? {
        return retrofit.cluedo.getPlayerTokensFromServer()
    }

    suspend fun getAssetCount(): AssetCount? {
        return retrofit.cluedo.getAssetCount()
    }
}