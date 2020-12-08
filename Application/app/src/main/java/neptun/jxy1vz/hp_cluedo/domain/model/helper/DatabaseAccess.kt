package neptun.jxy1vz.hp_cluedo.domain.model.helper

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetDBmodel
import neptun.jxy1vz.hp_cluedo.data.database.model.CardDBmodel
import neptun.jxy1vz.hp_cluedo.data.database.model.CardType.PLAYER
import neptun.jxy1vz.hp_cluedo.data.database.model.DarkHelperPairDBmodel
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.domain.model.card.*
import neptun.jxy1vz.hp_cluedo.domain.util.Interactor
import neptun.jxy1vz.hp_cluedo.domain.util.toDatabaseModel
import neptun.jxy1vz.hp_cluedo.domain.util.toDomainModel
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.ui.activity.login.LoginActivityListener

class DatabaseAccess(private val context: Context) {

    val interactor =
        Interactor(CluedoDatabase.getInstance(context), RetrofitInstance.getInstance(context))

    suspend fun resetCards() {
        val allCards = interactor.getCards()
        allCards!!.forEach { card ->
            val clearCard = CardDBmodel(
                card.id,
                card.name,
                card.imageRes,
                card.versoRes,
                card.cardType,
                null,
                card.lossType,
                card.hpLoss
            )
            interactor.updateCards(clearCard)
        }
    }

    suspend fun eraseNotes() {
        interactor.eraseNotes()
    }

    suspend fun getUnusedMysteryCards(): List<MysteryCard> {
        return interactor.getCardBySuperType(context.getString(R.string.mystery_prefix))!!
            .map { cardDBmodel -> cardDBmodel.toDomainModel(context) as MysteryCard }
    }

    suspend fun getAllMysteryCards(): List<MysteryCard> {
        return interactor.getAllMysteryCards()!!
            .map { cardDBmodel -> cardDBmodel.toDomainModel(context) as MysteryCard }
    }

    suspend fun getCardBySuperType(playerId: Int, prefix: String): Card? {
        val cards = interactor.getCardBySuperType(prefix)
        if (cards != null) {
            val card = cards.random()
            interactor.updateCards(
                CardDBmodel(
                    card.id,
                    card.name,
                    card.imageRes,
                    card.versoRes,
                    card.cardType,
                    playerId,
                    card.lossType,
                    card.hpLoss
                )
            )
            return when (prefix) {
                context.getString(R.string.dark_prefix) -> card.toDomainModel(context) as DarkCard
                context.getString(R.string.helper_prefix) -> card.toDomainModel(context) as HelperCard
                context.getString(R.string.mystery_prefix) -> card.toDomainModel(context) as MysteryCard
                else -> card.toDomainModel(context) as PlayerCard
            }
        }
        return null
    }

    suspend fun getCardByName(name: String): Card? {
        return interactor.getCardByName(name)?.toDomainModel(context)
    }

    suspend fun getMysteryCardsForPlayers(playerIds: List<Int>): List<Pair<MysteryCard, Int>> {
        resetCards()

        val mcList = ArrayList<Pair<MysteryCard, Int>>()

        val suspectSolution =
            interactor.getCardsByType(MysteryType.SUSPECT.toDatabaseModel().toString())!!.random()
        val toolSolution =
            interactor.getCardsByType(MysteryType.TOOL.toDatabaseModel().toString())!!.random()
        val roomSolution =
            interactor.getCardsByType(MysteryType.VENUE.toDatabaseModel().toString())!!.random()
        mcList.add(Pair(suspectSolution.toDomainModel(context) as MysteryCard, -1))
        mcList.add(Pair(toolSolution.toDomainModel(context) as MysteryCard, -1))
        mcList.add(Pair(roomSolution.toDomainModel(context) as MysteryCard, -1))
        interactor.updateCards(
            CardDBmodel(
                suspectSolution.id,
                suspectSolution.name,
                suspectSolution.imageRes,
                suspectSolution.versoRes,
                suspectSolution.cardType,
                -1,
                suspectSolution.lossType,
                suspectSolution.hpLoss
            )
        )
        interactor.updateCards(
            CardDBmodel(
                toolSolution.id,
                toolSolution.name,
                toolSolution.imageRes,
                toolSolution.versoRes,
                toolSolution.cardType,
                -1,
                toolSolution.lossType,
                toolSolution.hpLoss
            )
        )
        interactor.updateCards(
            CardDBmodel(
                roomSolution.id,
                roomSolution.name,
                roomSolution.imageRes,
                roomSolution.versoRes,
                roomSolution.cardType,
                -1,
                roomSolution.lossType,
                roomSolution.hpLoss
            )
        )

        val cardsPerPlayers = when (playerIds.size - 1) {
            3 -> 6
            4 -> 4
            else -> 3
        }
        for (i in 1..cardsPerPlayers) {
            for (id in playerIds) {
                if (id != -1) {
                    val card =
                        interactor.getCardBySuperType(context.resources.getString(R.string.mystery_prefix))!!
                            .random()
                    interactor.updateCards(
                        CardDBmodel(
                            card.id,
                            card.name,
                            card.imageRes,
                            card.versoRes,
                            card.cardType,
                            id,
                            card.lossType,
                            card.hpLoss
                        )
                    )
                    mcList.add(Pair(card.toDomainModel(context) as MysteryCard, id))
                }
            }
        }

        return mcList
    }

    suspend fun getMysteryCardsOfPlayers(): List<Pair<MysteryCard, Int>>? {
        val cards = interactor.getUsedMysteryCards()

        val pairList = ArrayList<Pair<MysteryCard, Int>>()
        cards!!.forEach { card ->
            pairList.add(Pair(card.toDomainModel(context) as MysteryCard, card.ownerId!!))
        }
        return pairList
    }

    suspend fun getHelperCardsAgainstDarkCard(card: DarkCard): List<HelperCard>? {
        val helperIds = interactor.getHelperCardsToDarkCard(card.id.toLong())
        helperIds?.let {
            val cardList: MutableList<HelperCard> = ArrayList()
            helperIds.forEach { id ->
                val c = interactor.getCardById(id)
                cardList.add(c!!.toDomainModel(context) as HelperCard)
            }
            return cardList
        }
        return null
    }

    suspend fun uploadDatabase(listener: LoginActivityListener) {
        val assetCount = interactor.getAssetCount()!!.count
        withContext(Dispatchers.Main) {
            listener.setMaxProgress(assetCount)
        }

        val playerCardUrls =
            interactor.getPlayerCardsFromServer()!!.fileNames.map { path -> path.path }
        val helperCardUrls =
            interactor.getHelperCardsFromServer()!!.fileNames.map { path -> path.path }
        val mysteryCardUrls =
            interactor.getMysteryCardsFromServer()!!.fileNames.map { path -> path.path }
        val darkCardUrls = interactor.getDarkCardsFromServer()!!.fileNames.map { path -> path.path }

        playerCardUrls.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }

        helperCardUrls.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }

        mysteryCardUrls.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }

        darkCardUrls.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }

        playerCards.forEach { card ->
            val dbCard = CardDBmodel(
                0,
                card.name,
                playerCardUrls[card.id * 2],
                playerCardUrls.last(),
                PLAYER.string(),
                null,
                null,
                null
            )
            interactor.insertIntoCards(dbCard)
            delay(500)
        }

        helperCards.forEach { card ->
            val dbCard = CardDBmodel(
                0,
                card.name,
                helperCardUrls[helperCards.indexOf(card)],
                helperCardUrls.last(),
                card.type.toDatabaseModel().string(),
                null,
                null,
                null
            )
            for (i in 0 until card.count)
                interactor.insertIntoCards(dbCard)
            delay(500)
        }

        mysteryCards.forEach { card ->
            val dbCard = CardDBmodel(
                0,
                card.name,
                mysteryCardUrls[mysteryCards.indexOf(card)],
                mysteryCardUrls.last(),
                card.type.toDatabaseModel().string(),
                null,
                null,
                null
            )
            interactor.insertIntoCards(dbCard)
            delay(500)
        }

        darkCards.forEach { card ->
            val dbCard = CardDBmodel(
                0,
                card.name,
                darkCardUrls[darkCards.indexOf(card)],
                darkCardUrls.last(),
                card.type.toDatabaseModel().string(),
                null,
                card.lossType.toDatabaseModel().string(),
                card.hpLoss
            )
            interactor.insertIntoCards(dbCard)
            delay(500)
            card.helperIds?.let {
                card.helperIds!!.forEach { id ->
                    interactor.insertIntoDarkHelperPairs(
                        DarkHelperPairDBmodel(
                            0,
                            interactor.getCardIdByName(card.name)!!,
                            interactor.getCardIdByName(helperCards[id].name)!!
                        )
                    )
                }
            }
        }

        interactor.getDarkMarkAssetsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getDarkCardFragmentAssetsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getDiceAssetsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getDoorAssetsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getFootprintsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getGatewaysFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getNoteAssetsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getMapRelatedAssetsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getSelectionAssetsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getTilesFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getMenuRelatedAssetsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getTutorialAssetsFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getMysteryRoomTokensFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getMysteryToolTokensFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getMysterySuspectTokensFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
        interactor.getPlayerTokensFromServer()!!.fileNames.map { path -> path.path }.forEach {
            interactor.insertIntoAssets(AssetDBmodel(0, "${RetrofitInstance.URL}${it}", it))
            withContext(Dispatchers.Main) {
                listener.increaseProgress()
            }
        }
    }

    private val playerNameList: Array<String> = context.resources.getStringArray(R.array.characters)

    private val playerCards = listOf(
        PlayerCard(
            0,
            playerNameList[0]
        ),
        PlayerCard(
            1,
            playerNameList[1]
        ),
        PlayerCard(
            2,
            playerNameList[2]
        ),
        PlayerCard(
            3,
            playerNameList[3]
        ),
        PlayerCard(
            4,
            playerNameList[4]
        ),
        PlayerCard(
            5,
            playerNameList[5]
        )
    )

    private val helperCardNames: Array<String> =
        context.resources.getStringArray(R.array.helper_cards)

    private val helperCards = listOf(
        HelperCard(
            0,
            helperCardNames[0],
            HelperType.TOOL
        ),
        HelperCard(
            1,
            helperCardNames[1],
            HelperType.TOOL
        ),
        HelperCard(
            2,
            helperCardNames[2],
            HelperType.TOOL
        ),
        HelperCard(
            3,
            helperCardNames[3],
            HelperType.TOOL
        ),
        HelperCard(
            4,
            helperCardNames[4],
            HelperType.TOOL
        ),
        HelperCard(
            5,
            helperCardNames[5],
            HelperType.TOOL
        ),
        HelperCard(
            6,
            helperCardNames[6],
            HelperType.TOOL
        ),
        HelperCard(
            7,
            helperCardNames[7],
            HelperType.TOOL
        ),
        HelperCard(
            8,
            helperCardNames[8],
            HelperType.TOOL
        ),
        HelperCard(
            9,
            helperCardNames[9],
            HelperType.TOOL,
            3
        ),
        HelperCard(
            10,
            helperCardNames[10],
            HelperType.ALLY
        ),
        HelperCard(
            11,
            helperCardNames[11],
            HelperType.ALLY
        ),
        HelperCard(
            12,
            helperCardNames[12],
            HelperType.ALLY
        ),
        HelperCard(
            13,
            helperCardNames[13],
            HelperType.ALLY
        ),
        HelperCard(
            14,
            helperCardNames[14],
            HelperType.ALLY
        ),
        HelperCard(
            15,
            helperCardNames[15],
            HelperType.ALLY
        ),
        HelperCard(
            16,
            helperCardNames[16],
            HelperType.ALLY
        ),
        HelperCard(
            17,
            helperCardNames[17],
            HelperType.ALLY
        ),
        HelperCard(
            18,
            helperCardNames[18],
            HelperType.SPELL
        ),
        HelperCard(
            19,
            helperCardNames[19],
            HelperType.SPELL
        ),
        HelperCard(
            20,
            helperCardNames[20],
            HelperType.SPELL
        ),
        HelperCard(
            21,
            helperCardNames[21],
            HelperType.SPELL
        ),
        HelperCard(
            22,
            helperCardNames[22],
            HelperType.SPELL
        ),
        HelperCard(
            23,
            helperCardNames[23],
            HelperType.SPELL
        ),
        HelperCard(
            24,
            helperCardNames[24],
            HelperType.SPELL
        ),
        HelperCard(
            25,
            helperCardNames[25],
            HelperType.SPELL
        ),
        HelperCard(
            26,
            helperCardNames[26],
            HelperType.SPELL,
            5
        )
    )

    private val toolNames = context.resources.getStringArray(R.array.tools)
    private val suspectNames = context.resources.getStringArray(R.array.suspects)
    private val roomNames = context.resources.getStringArray(R.array.rooms)

    private val mysteryCards = listOf(
        MysteryCard(
            0,
            toolNames[0],
            MysteryType.TOOL
        ),
        MysteryCard(
            1,
            toolNames[1],
            MysteryType.TOOL
        ),
        MysteryCard(
            2,
            toolNames[2],
            MysteryType.TOOL
        ),
        MysteryCard(
            3,
            toolNames[3],
            MysteryType.TOOL
        ),
        MysteryCard(
            4,
            toolNames[4],
            MysteryType.TOOL
        ),
        MysteryCard(
            5,
            toolNames[5],
            MysteryType.TOOL
        ),
        MysteryCard(
            6,
            suspectNames[0],
            MysteryType.SUSPECT
        ),
        MysteryCard(
            7,
            suspectNames[1],
            MysteryType.SUSPECT
        ),
        MysteryCard(
            8,
            suspectNames[2],
            MysteryType.SUSPECT
        ),
        MysteryCard(
            9,
            suspectNames[3],
            MysteryType.SUSPECT
        ),
        MysteryCard(
            10,
            suspectNames[4],
            MysteryType.SUSPECT
        ),
        MysteryCard(
            11,
            suspectNames[5],
            MysteryType.SUSPECT
        ),
        MysteryCard(
            12,
            roomNames[0],
            MysteryType.VENUE
        ),
        MysteryCard(
            13,
            roomNames[1],
            MysteryType.VENUE
        ),
        MysteryCard(
            14,
            roomNames[2],
            MysteryType.VENUE
        ),
        MysteryCard(
            15,
            roomNames[3],
            MysteryType.VENUE
        ),
        MysteryCard(
            16,
            roomNames[4],
            MysteryType.VENUE
        ),
        MysteryCard(
            17,
            roomNames[5],
            MysteryType.VENUE
        ),
        MysteryCard(
            18,
            roomNames[6],
            MysteryType.VENUE
        ),
        MysteryCard(
            19,
            roomNames[7],
            MysteryType.VENUE
        ),
        MysteryCard(
            20,
            roomNames[8],
            MysteryType.VENUE
        )
    )

    private val darkCardNames = context.resources.getStringArray(R.array.dark_cards)

    private val darkCards = listOf(
        DarkCard(
            0,
            darkCardNames[0],
            DarkType.CORRIDOR,
            LossType.HP,
            15,
            listOf(13, 16, 2)
        ),
        DarkCard(
            1,
            darkCardNames[1],
            DarkType.CORRIDOR,
            LossType.HP,
            10,
            listOf(0, 13)
        ),
        DarkCard(
            2,
            darkCardNames[2],
            DarkType.CORRIDOR,
            LossType.HP,
            20,
            listOf(6, 13)
        ),
        DarkCard(
            3,
            darkCardNames[3],
            DarkType.CORRIDOR,
            LossType.HP,
            10,
            listOf(4, 10)
        ),
        DarkCard(
            4,
            darkCardNames[4],
            DarkType.CORRIDOR,
            LossType.HP,
            20,
            listOf(1, 8, 23)
        ),
        DarkCard(
            5,
            darkCardNames[5],
            DarkType.CORRIDOR,
            LossType.HP,
            5,
            listOf(10)
        ),
        DarkCard(
            6,
            darkCardNames[6],
            DarkType.CORRIDOR,
            LossType.HP,
            15,
            listOf(18, 21, 22, 24)
        ),
        DarkCard(
            7,
            darkCardNames[7],
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            10,
            listOf(13, 15)
        ),
        DarkCard(
            8,
            darkCardNames[8],
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            15,
            listOf(20, 8)
        ),
        DarkCard(
            9,
            darkCardNames[9],
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            15,
            listOf(20, 8)
        ),
        DarkCard(
            10,
            darkCardNames[10],
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            15,
            listOf(13, 17)
        ),
        DarkCard(
            11,
            darkCardNames[11],
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            20,
            listOf(10, 13, 12)
        ),
        DarkCard(
            12,
            darkCardNames[12],
            DarkType.ROOM_BAGOLYHAZ,
            LossType.HP,
            15,
            listOf(16, 17, 11)
        ),
        DarkCard(
            13,
            darkCardNames[13],
            DarkType.ROOM_BAJITALTAN,
            LossType.HP,
            25,
            listOf(1, 15)
        ),
        DarkCard(
            14,
            darkCardNames[14],
            DarkType.ROOM_GYENGELKEDO,
            LossType.HP,
            15,
            listOf(0, 13)
        ),
        DarkCard(
            15,
            darkCardNames[15],
            DarkType.ROOM_JOSLASTAN,
            LossType.HP,
            5,
            listOf(14)
        ),
        DarkCard(
            16,
            darkCardNames[16],
            DarkType.ROOM_KONYVTAR,
            LossType.HP,
            10,
            listOf(3, 5, 8)
        ),
        DarkCard(
            17,
            darkCardNames[17],
            DarkType.ROOM_NAGYTEREM,
            LossType.HP,
            15,
            listOf(12, 18, 19, 22)
        ),
        DarkCard(
            18,
            darkCardNames[18],
            DarkType.ROOM_SERLEG,
            LossType.HP,
            5
        ),
        DarkCard(
            19,
            darkCardNames[19],
            DarkType.ROOM_SVK,
            LossType.HP,
            20,
            listOf(19, 14, 8)
        ),
        DarkCard(
            20,
            darkCardNames[20],
            DarkType.ROOM_SZUKSEG_SZOBAJA,
            LossType.HP,
            20,
            listOf(3, 5, 1, 8)
        ),
        DarkCard(
            21,
            darkCardNames[21],
            DarkType.ALL_PLAYERS,
            LossType.ALLY,
            0
        ),
        DarkCard(
            22,
            darkCardNames[22],
            DarkType.ALL_PLAYERS,
            LossType.HP,
            15,
            listOf(21, 22, 24, 5)
        ),
        DarkCard(
            23,
            darkCardNames[23],
            DarkType.ALL_PLAYERS,
            LossType.HP,
            10,
            listOf(16, 5, 12)
        ),
        DarkCard(
            24,
            darkCardNames[24],
            DarkType.ALL_PLAYERS,
            LossType.HP,
            5,
            listOf(14, 7, 18, 24)
        ),
        DarkCard(
            25,
            darkCardNames[25],
            DarkType.ALL_PLAYERS,
            LossType.SPELL,
            0
        ),
        DarkCard(
            26,
            darkCardNames[26],
            DarkType.ALL_PLAYERS,
            LossType.HP,
            15,
            listOf(16, 12, 13)
        ),
        DarkCard(
            27,
            darkCardNames[27],
            DarkType.ALL_PLAYERS,
            LossType.HP,
            10,
            listOf(7, 5, 15)
        ),
        DarkCard(
            28,
            darkCardNames[28],
            DarkType.ALL_PLAYERS,
            LossType.HP,
            15,
            listOf(25, 7, 1, 21)
        ),
        DarkCard(
            29,
            darkCardNames[29],
            DarkType.ALL_PLAYERS,
            LossType.HP,
            5,
            listOf(7, 5, 10)
        ),
        DarkCard(
            30,
            darkCardNames[30],
            DarkType.ALL_PLAYERS,
            LossType.TOOL,
            0
        ),
        DarkCard(
            31,
            darkCardNames[31],
            DarkType.GENDER_MEN,
            LossType.HP,
            20,
            listOf(14, 10)
        ),
        DarkCard(
            32,
            darkCardNames[32],
            DarkType.GENDER_WOMEN,
            LossType.HP,
            20,
            listOf(15, 1)
        )
    )
}