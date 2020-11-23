package neptun.jxy1vz.cluedo.domain.model.helper

import android.content.Context
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.database.CluedoDatabase
import neptun.jxy1vz.cluedo.database.model.CardDBmodel
import neptun.jxy1vz.cluedo.database.model.CardType.PLAYER
import neptun.jxy1vz.cluedo.database.model.DarkHelperPairDBmodel
import neptun.jxy1vz.cluedo.database.model.string
import neptun.jxy1vz.cluedo.domain.model.card.*
import neptun.jxy1vz.cluedo.domain.util.Interactor
import neptun.jxy1vz.cluedo.domain.util.toDatabaseModel
import neptun.jxy1vz.cluedo.domain.util.toDomainModel

class DatabaseAccess(private val context: Context) {

    private val interactor = Interactor(CluedoDatabase.getInstance(context))

    suspend fun resetCards() {
        val allCards = interactor.getCards()
        allCards!!.forEach { card ->
            val clearCard = CardDBmodel(card.id, card.name, card.imageRes, card.versoRes, card.cardType, null, card.lossType, card.hpLoss)
            interactor.updateCards(clearCard)
        }
    }

    suspend fun eraseNotes() {
        interactor.eraseNotes()
    }

    suspend fun updateCard(card: CardDBmodel) {
        interactor.updateCards(card)
    }

    suspend fun getUnusedMysteryCards(): List<MysteryCard> {
        return interactor.getCardBySuperType(context.getString(R.string.mystery_prefix))!!.map { cardDBmodel -> cardDBmodel.toDomainModel() as MysteryCard }
    }

    suspend fun getAllMysteryCards(): List<MysteryCard> {
        return interactor.getAllMysteryCards()!!.map { cardDBmodel -> cardDBmodel.toDomainModel() as MysteryCard }
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
                context.getString(R.string.dark_prefix) -> card.toDomainModel() as DarkCard
                context.getString(R.string.helper_prefix) -> card.toDomainModel() as HelperCard
                context.getString(R.string.mystery_prefix) -> card.toDomainModel() as MysteryCard
                else -> card.toDomainModel() as PlayerCard
            }
        }
        return null
    }

    suspend fun getCardByName(name: String): Card? {
        return interactor.getCardByName(name)?.toDomainModel()
    }

    suspend fun getMysteryCardsForPlayers(playerIds: List<Int>): List<Pair<MysteryCard, Int>> {
        resetCards()

        val mcList = ArrayList<Pair<MysteryCard, Int>>()

        val suspectSolution = interactor.getCardsByType(MysteryType.SUSPECT.toDatabaseModel().toString())!!.random()
        val toolSolution = interactor.getCardsByType(MysteryType.TOOL.toDatabaseModel().toString())!!.random()
        val roomSolution = interactor.getCardsByType(MysteryType.VENUE.toDatabaseModel().toString())!!.random()
        mcList.add(Pair(suspectSolution.toDomainModel() as MysteryCard, -1))
        mcList.add(Pair(toolSolution.toDomainModel() as MysteryCard, -1))
        mcList.add(Pair(roomSolution.toDomainModel() as MysteryCard, -1))
        interactor.updateCards(CardDBmodel(suspectSolution.id, suspectSolution.name, suspectSolution.imageRes, suspectSolution.versoRes, suspectSolution.cardType, -1, suspectSolution.lossType, suspectSolution.hpLoss))
        interactor.updateCards(CardDBmodel(toolSolution.id, toolSolution.name, toolSolution.imageRes, toolSolution.versoRes, toolSolution.cardType, -1, toolSolution.lossType, toolSolution.hpLoss))
        interactor.updateCards(CardDBmodel(roomSolution.id, roomSolution.name, roomSolution.imageRes, roomSolution.versoRes, roomSolution.cardType, -1, roomSolution.lossType, roomSolution.hpLoss))

        val cardsPerPlayers = when (playerIds.size - 1) {
            3 -> 6
            4 -> 4
            else -> 3
        }
        for (i in 1..cardsPerPlayers) {
            for (id in playerIds) {
                if (id != -1) {
                    val card = interactor.getCardBySuperType(context.resources.getString(R.string.mystery_prefix))!!.random()
                    interactor.updateCards(CardDBmodel(card.id, card.name, card.imageRes, card.versoRes, card.cardType, id, card.lossType, card.hpLoss))
                    mcList.add(Pair(card.toDomainModel() as MysteryCard, id))
                }
            }
        }

        return mcList
    }

    suspend fun getMysteryCardsOfPlayers(): List<Pair<MysteryCard, Int>>? {
        val cards = interactor.getUsedMysteryCards()

        val pairList = ArrayList<Pair<MysteryCard, Int>>()
        cards!!.forEach { card ->
            pairList.add(Pair(card.toDomainModel() as MysteryCard, card.ownerId!!))
        }
        return pairList
    }

    suspend fun getHelperCardsAgainstDarkCard(card: DarkCard): List<HelperCard>? {
        val helperIds = interactor.getHelperCardsToDarkCard(card.id.toLong())
        helperIds?.let {
            val cardList: MutableList<HelperCard> = ArrayList()
            helperIds.forEach { id ->
                val c = interactor.getCardById(id)
                cardList.add(c!!.toDomainModel() as HelperCard)
            }
            return cardList
        }
        return null
    }

    suspend fun uploadDatabase() {
        playerCards.forEach { card ->
            val dbCard = CardDBmodel(0, card.name, card.imageRes, card.verso, PLAYER.string(), null, null, null)
            interactor.insertIntoCards(dbCard)
        }

        helperCards.forEach { card ->
            val dbCard = CardDBmodel(0, card.name, card.imageRes, card.verso, card.type.toDatabaseModel().string(), null, null, null)
            for (i in 0 until card.count)
                interactor.insertIntoCards(dbCard)
        }

        mysteryCards.forEach { card ->
            val dbCard = CardDBmodel(0, card.name, card.imageRes, card.verso, card.type.toDatabaseModel().string(), null, null, null)
            interactor.insertIntoCards(dbCard)
        }

        darkCards.forEach { card ->
            val dbCard = CardDBmodel(0, card.name, card.imageRes, card.verso, card.type.toDatabaseModel().string(), null, card.lossType.toDatabaseModel().string(), card.hpLoss)
            interactor.insertIntoCards(dbCard)

            card.helperIds?.let {
                card.helperIds!!.forEach { id ->
                    interactor.insertIntoDarkHelperPairs(DarkHelperPairDBmodel(0, interactor.getCardIdByName(card.name)!!, interactor.getCardIdByName(helperCards[id].name)!!))
                }
            }
        }
    }

    private val playerNameList: Array<String> = context.resources.getStringArray(R.array.characters)

    private val playerCards = listOf(
        PlayerCard(
            0,
            playerNameList[0],
            R.drawable.szereplo_ginny,
            R.drawable.szereplo_hatlap
        ),
        PlayerCard(
            1,
            playerNameList[1],
            R.drawable.szereplo_harry,
            R.drawable.szereplo_hatlap
        ),
        PlayerCard(
            2,
            playerNameList[2],
            R.drawable.szereplo_hermione,
            R.drawable.szereplo_hatlap
        ),
        PlayerCard(
            3,
            playerNameList[3],
            R.drawable.szereplo_ron,
            R.drawable.szereplo_hatlap
        ),
        PlayerCard(
            4,
            playerNameList[4],
            R.drawable.szereplo_luna,
            R.drawable.szereplo_hatlap
        ),
        PlayerCard(
            5,
            playerNameList[5],
            R.drawable.szereplo_neville,
            R.drawable.szereplo_hatlap
        )
    )

    private val helperCardNames: Array<String> = context.resources.getStringArray(R.array.helper_cards)

    private val helperCards = listOf(
        HelperCard(
            0,
            helperCardNames[0],
            R.drawable.mento_bezoar,
            R.drawable.mento_hatlap,
            HelperType.TOOL
        ),
        HelperCard(
            1,
            helperCardNames[1],
            R.drawable.mento_sepru,
            R.drawable.mento_hatlap,
            HelperType.TOOL
        ),
        HelperCard(
            2,
            helperCardNames[2],
            R.drawable.mento_alsagdetektor,
            R.drawable.mento_hatlap,
            HelperType.TOOL
        ),
        HelperCard(
            3,
            helperCardNames[3],
            R.drawable.mento_onolto,
            R.drawable.mento_hatlap,
            HelperType.TOOL
        ),
        HelperCard(
            4,
            helperCardNames[4],
            R.drawable.mento_varangydudva,
            R.drawable.mento_hatlap,
            HelperType.TOOL
        ),
        HelperCard(
            5,
            helperCardNames[5],
            R.drawable.mento_lathatatlanna_tevo_kopeny,
            R.drawable.mento_hatlap,
            HelperType.TOOL
        ),
        HelperCard(
            6,
            helperCardNames[6],
            R.drawable.mento_mandragoras_gyogyszirup,
            R.drawable.mento_hatlap,
            HelperType.TOOL
        ),
        HelperCard(
            7,
            helperCardNames[7],
            R.drawable.mento_tekergok_terkepe,
            R.drawable.mento_hatlap,
            HelperType.TOOL
        ),
        HelperCard(
            8,
            helperCardNames[8],
            R.drawable.mento_zsupszkulcs,
            R.drawable.mento_hatlap,
            HelperType.TOOL
        ),
        HelperCard(
            9,
            helperCardNames[9],
            R.drawable.mento_felix_felicis,
            R.drawable.mento_hatlap,
            HelperType.TOOL,
            3
        ),
        HelperCard(
            10,
            helperCardNames[10],
            R.drawable.mento_albus_dumbledore,
            R.drawable.mento_hatlap,
            HelperType.ALLY
        ),
        HelperCard(
            11,
            helperCardNames[11],
            R.drawable.mento_dobby,
            R.drawable.mento_hatlap,
            HelperType.ALLY
        ),
        HelperCard(
            12,
            helperCardNames[12],
            R.drawable.mento_fawkes,
            R.drawable.mento_hatlap,
            HelperType.ALLY
        ),
        HelperCard(
            13,
            helperCardNames[13],
            R.drawable.mento_madam_pomfrey,
            R.drawable.mento_hatlap,
            HelperType.ALLY
        ),
        HelperCard(
            14,
            helperCardNames[14],
            R.drawable.mento_mcgalagony_professzor,
            R.drawable.mento_hatlap,
            HelperType.ALLY
        ),
        HelperCard(
            15,
            helperCardNames[15],
            R.drawable.mento_piton_professzor,
            R.drawable.mento_hatlap,
            HelperType.ALLY
        ),
        HelperCard(
            16,
            helperCardNames[16],
            R.drawable.mento_rubeus_hagrid,
            R.drawable.mento_hatlap,
            HelperType.ALLY
        ),
        HelperCard(
            17,
            helperCardNames[17],
            R.drawable.mento_weasley_ikrek,
            R.drawable.mento_hatlap,
            HelperType.ALLY
        ),
        HelperCard(
            18,
            helperCardNames[18],
            R.drawable.mento_capitulatus,
            R.drawable.mento_hatlap,
            HelperType.SPELL
        ),
        HelperCard(
            19,
            helperCardNames[19],
            R.drawable.mento_immobilus,
            R.drawable.mento_hatlap,
            HelperType.SPELL
        ),
        HelperCard(
            20,
            helperCardNames[20],
            R.drawable.mento_lumos,
            R.drawable.mento_hatlap,
            HelperType.SPELL
        ),
        HelperCard(
            21,
            helperCardNames[21],
            R.drawable.mento_petrificus_totalus,
            R.drawable.mento_hatlap,
            HelperType.SPELL
        ),
        HelperCard(
            22,
            helperCardNames[22],
            R.drawable.mento_protego,
            R.drawable.mento_hatlap,
            HelperType.SPELL
        ),
        HelperCard(
            23,
            helperCardNames[23],
            R.drawable.mento_commikulissimus,
            R.drawable.mento_hatlap,
            HelperType.SPELL
        ),
        HelperCard(
            24,
            helperCardNames[24],
            R.drawable.mento_stupor,
            R.drawable.mento_hatlap,
            HelperType.SPELL
        ),
        HelperCard(
            25,
            helperCardNames[25],
            R.drawable.mento_vingardium_leviosa,
            R.drawable.mento_hatlap,
            HelperType.SPELL
        ),
        HelperCard(
            26,
            helperCardNames[26],
            R.drawable.mento_alohomora,
            R.drawable.mento_hatlap,
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
            R.drawable.rejtely_altatoital,
            R.drawable.rejtely_hatlap,
            MysteryType.TOOL
        ),
        MysteryCard(
            1,
            toolNames[1],
            R.drawable.rejtely_mandragora,
            R.drawable.rejtely_hatlap,
            MysteryType.TOOL
        ),
        MysteryCard(
            2,
            toolNames[2],
            R.drawable.rejtely_obstructo,
            R.drawable.rejtely_hatlap,
            MysteryType.TOOL
        ),
        MysteryCard(
            3,
            toolNames[3],
            R.drawable.rejtely_petrificus_totalus,
            R.drawable.rejtely_hatlap,
            MysteryType.TOOL
        ),
        MysteryCard(
            4,
            toolNames[4],
            R.drawable.rejtely_volt_nincs,
            R.drawable.rejtely_hatlap,
            MysteryType.TOOL
        ),
        MysteryCard(
            5,
            toolNames[5],
            R.drawable.rejtely_zsupszkulcs,
            R.drawable.rejtely_hatlap,
            MysteryType.TOOL
        ),
        MysteryCard(
            6,
            suspectNames[0],
            R.drawable.rejtely_bellatrix_lestrange,
            R.drawable.rejtely_hatlap,
            MysteryType.SUSPECT
        ),
        MysteryCard(
            7,
            suspectNames[1],
            R.drawable.rejtely_crak_es_monstro,
            R.drawable.rejtely_hatlap,
            MysteryType.SUSPECT
        ),
        MysteryCard(
            8,
            suspectNames[2],
            R.drawable.rejtely_draco_malfoy,
            R.drawable.rejtely_hatlap,
            MysteryType.SUSPECT
        ),
        MysteryCard(
            9,
            suspectNames[3],
            R.drawable.rejtely_lucius_malfoy,
            R.drawable.rejtely_hatlap,
            MysteryType.SUSPECT
        ),
        MysteryCard(
            10,
            suspectNames[4],
            R.drawable.rejtely_dolores_umbridge,
            R.drawable.rejtely_hatlap,
            MysteryType.SUSPECT
        ),
        MysteryCard(
            11,
            suspectNames[5],
            R.drawable.rejtely_peter_pettigrew,
            R.drawable.rejtely_hatlap,
            MysteryType.SUSPECT
        ),
        MysteryCard(
            12,
            roomNames[0],
            R.drawable.rejtely_bagolyhaz,
            R.drawable.rejtely_hatlap,
            MysteryType.VENUE
        ),
        MysteryCard(
            13,
            roomNames[1],
            R.drawable.rejtely_bajitaltan,
            R.drawable.rejtely_hatlap,
            MysteryType.VENUE
        ),
        MysteryCard(
            14,
            roomNames[2],
            R.drawable.rejtely_gyengelkedo,
            R.drawable.rejtely_hatlap,
            MysteryType.VENUE
        ),
        MysteryCard(
            15,
            roomNames[3],
            R.drawable.rejtely_joslastan,
            R.drawable.rejtely_hatlap,
            MysteryType.VENUE
        ),
        MysteryCard(
            16,
            roomNames[4],
            R.drawable.rejtely_konyvtar,
            R.drawable.rejtely_hatlap,
            MysteryType.VENUE
        ),
        MysteryCard(
            17,
            roomNames[5],
            R.drawable.rejtely_nagyterem,
            R.drawable.rejtely_hatlap,
            MysteryType.VENUE
        ),
        MysteryCard(
            18,
            roomNames[6],
            R.drawable.rejtely_serleg,
            R.drawable.rejtely_hatlap,
            MysteryType.VENUE
        ),
        MysteryCard(
            19,
            roomNames[7],
            R.drawable.rejtely_svk,
            R.drawable.rejtely_hatlap,
            MysteryType.VENUE
        ),
        MysteryCard(
            20,
            roomNames[8],
            R.drawable.rejtely_szukseg_szobaja,
            R.drawable.rejtely_hatlap,
            MysteryType.VENUE
        )
    )

    private val darkCards = listOf(
        DarkCard(
            0,
            context.getString(R.string.dark_card1),
            R.drawable.sotet_pakli_corridor_1,
            R.drawable.sotet_pakli_hatlap,
            DarkType.CORRIDOR,
            LossType.HP,
            15,
            listOf(13, 16, 2)
        ),
        DarkCard(
            1,
            context.getString(R.string.dark_card2),
            R.drawable.sotet_pakli_corridor_2,
            R.drawable.sotet_pakli_hatlap,
            DarkType.CORRIDOR,
            LossType.HP,
            10,
            listOf(0, 13)
        ),
        DarkCard(
            2,
            context.getString(R.string.dark_card3),
            R.drawable.sotet_pakli_corridor_3,
            R.drawable.sotet_pakli_hatlap,
            DarkType.CORRIDOR,
            LossType.HP,
            20,
            listOf(6, 13)
        ),
        DarkCard(
            3,
            context.getString(R.string.dark_card4),
            R.drawable.sotet_pakli_corridor_4,
            R.drawable.sotet_pakli_hatlap,
            DarkType.CORRIDOR,
            LossType.HP,
            10,
            listOf(4, 10)
        ),
        DarkCard(
            4,
            context.getString(R.string.dark_card5),
            R.drawable.sotet_pakli_corridor_5,
            R.drawable.sotet_pakli_hatlap,
            DarkType.CORRIDOR,
            LossType.HP,
            20,
            listOf(1, 8, 23)
        ),
        DarkCard(
            5,
            context.getString(R.string.dark_card6),
            R.drawable.sotet_pakli_corridor_6,
            R.drawable.sotet_pakli_hatlap,
            DarkType.CORRIDOR,
            LossType.HP,
            5,
            listOf(10)
        ),
        DarkCard(
            6,
            context.getString(R.string.dark_card7),
            R.drawable.sotet_pakli_corridor_7,
            R.drawable.sotet_pakli_hatlap,
            DarkType.CORRIDOR,
            LossType.HP,
            15,
            listOf(18, 21, 22, 24)
        ),
        DarkCard(
            7,
            context.getString(R.string.dark_card8),
            R.drawable.sotet_pakli_picker_1,
            R.drawable.sotet_pakli_hatlap,
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            10,
            listOf(13, 15)
        ),
        DarkCard(
            8,
            context.getString(R.string.dark_card9),
            R.drawable.sotet_pakli_picker_2,
            R.drawable.sotet_pakli_hatlap,
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            15,
            listOf(20, 8)
        ),
        DarkCard(
            9,
            context.getString(R.string.dark_card10),
            R.drawable.sotet_pakli_picker_3,
            R.drawable.sotet_pakli_hatlap,
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            15,
            listOf(20, 8)
        ),
        DarkCard(
            10,
            context.getString(R.string.dark_card11),
            R.drawable.sotet_pakli_picker_4,
            R.drawable.sotet_pakli_hatlap,
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            15,
            listOf(13, 17)
        ),
        DarkCard(
            11,
            context.getString(R.string.dark_card12),
            R.drawable.sotet_pakli_picker_5,
            R.drawable.sotet_pakli_hatlap,
            DarkType.PLAYER_IN_TURN,
            LossType.HP,
            20,
            listOf(10, 13, 12)
        ),
        DarkCard(
            12,
            context.getString(R.string.dark_card13),
            R.drawable.sotet_pakli_bagolyhaz,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ROOM_BAGOLYHAZ,
            LossType.HP,
            15,
            listOf(16, 17, 11)
        ),
        DarkCard(
            13,
            context.getString(R.string.dark_card14),
            R.drawable.sotet_pakli_bajitaltan,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ROOM_BAJITALTAN,
            LossType.HP,
            25,
            listOf(1, 15)
        ),
        DarkCard(
            14,
            context.getString(R.string.dark_card15),
            R.drawable.sotet_pakli_gyengelkedo,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ROOM_GYENGELKEDO,
            LossType.HP,
            15,
            listOf(0, 13)
        ),
        DarkCard(
            15,
            context.getString(R.string.dark_card16),
            R.drawable.sotet_pakli_joslastan,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ROOM_JOSLASTAN,
            LossType.HP,
            5,
            listOf(14)
        ),
        DarkCard(
            16,
            context.getString(R.string.dark_card17),
            R.drawable.sotet_pakli_konyvtar,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ROOM_KONYVTAR,
            LossType.HP,
            10,
            listOf(3, 5, 8)
        ),
        DarkCard(
            17,
            context.getString(R.string.dark_card18),
            R.drawable.sotet_pakli_nagyterem,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ROOM_NAGYTEREM,
            LossType.HP,
            15,
            listOf(12, 18, 19, 22)
        ),
        DarkCard(
            18,
            context.getString(R.string.dark_card19),
            R.drawable.sotet_pakli_serleg,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ROOM_SERLEG,
            LossType.HP,
            5
        ),
        DarkCard(
            19,
            context.getString(R.string.dark_card20),
            R.drawable.sotet_pakli_svk,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ROOM_SVK,
            LossType.HP,
            20,
            listOf(19, 14, 8)
        ),
        DarkCard(
            20,
            context.getString(R.string.dark_card21),
            R.drawable.sotet_pakli_szukseg_szobaja,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ROOM_SZUKSEG_SZOBAJA,
            LossType.HP,
            20,
            listOf(3, 5, 1, 8)
        ),
        DarkCard(
            21,
            context.getString(R.string.dark_card22),
            R.drawable.sotet_pakli_all_players_1,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.ALLY,
            0
        ),
        DarkCard(
            22,
            context.getString(R.string.dark_card23),
            R.drawable.sotet_pakli_all_players_2,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.HP,
            15,
            listOf(21, 22, 24, 5)
        ),
        DarkCard(
            23,
            context.getString(R.string.dark_card24),
            R.drawable.sotet_pakli_all_players_3,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.HP,
            10,
            listOf(16, 5, 12)
        ),
        DarkCard(
            24,
            context.getString(R.string.dark_card25),
            R.drawable.sotet_pakli_all_players_4,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.HP,
            5,
            listOf(14, 7, 18, 24)
        ),
        DarkCard(
            25,
            context.getString(R.string.dark_card26),
            R.drawable.sotet_pakli_all_players_5,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.SPELL,
            0
        ),
        DarkCard(
            26,
            context.getString(R.string.dark_card27),
            R.drawable.sotet_pakli_all_players_6,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.HP,
            15,
            listOf(16, 12, 13)
        ),
        DarkCard(
            27,
            context.getString(R.string.dark_card28),
            R.drawable.sotet_pakli_all_players_7,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.HP,
            10,
            listOf(7, 5, 15)
        ),
        DarkCard(
            28,
            context.getString(R.string.dark_card29),
            R.drawable.sotet_pakli_all_players_8,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.HP,
            15,
            listOf(25, 7, 1, 21)
        ),
        DarkCard(
            29,
            context.getString(R.string.dark_card30),
            R.drawable.sotet_pakli_all_players_9,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.HP,
            5,
            listOf(7, 5, 10)
        ),
        DarkCard(
            30,
            context.getString(R.string.dark_card31),
            R.drawable.sotet_pakli_all_players_10,
            R.drawable.sotet_pakli_hatlap,
            DarkType.ALL_PLAYERS,
            LossType.TOOL,
            0
        ),
        DarkCard(
            31,
            context.getString(R.string.dark_card32),
            R.drawable.sotet_pakli_ferfiaknak,
            R.drawable.sotet_pakli_hatlap,
            DarkType.GENDER_MEN,
            LossType.HP,
            20,
            listOf(14, 10)
        ),
        DarkCard(
            32,
            context.getString(R.string.dark_card33),
            R.drawable.sotet_pakli_noknek,
            R.drawable.sotet_pakli_hatlap,
            DarkType.GENDER_WOMEN,
            LossType.HP,
            20,
            listOf(15, 1)
        )
    )
}