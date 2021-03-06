package neptun.jxy1vz.hp_cluedo.domain.model.helper

import android.content.Context
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.domain.model.*
import neptun.jxy1vz.hp_cluedo.domain.model.card.MysteryCard
import neptun.jxy1vz.hp_cluedo.domain.model.card.PlayerCard
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel

class GameModels(private val context: Context) {

    var db: DatabaseAccess = DatabaseAccess(context)
    lateinit var gameSolution: List<MysteryCard>
    lateinit var playerList: List<BasePlayer>

    private val roomNameList: Array<String> = context.resources.getStringArray(R.array.rooms)
    lateinit var roomList: List<Room>
    lateinit var doorList: List<Door>
    lateinit var fieldSelection: String
    lateinit var starSelection: String

    private suspend fun loadSelections() {
        val selections = db.interactor.getAssetsByPrefix(AssetPrefixes.SELECTION.string())!!
            .map { assetDBmodel -> assetDBmodel.url }
        roomList = listOf(
            Room(0, roomNameList[7], 0, 6, 5, 0, selections[0]),
            Room(1, roomNameList[5], 0, 15, 6, 9, selections[1]),
            Room(2, roomNameList[2], 0, 24, 6, 18, selections[2]),
            Room(3, roomNameList[4], 8, 6, 11, 0, selections[3]),
            Room(
                4,
                context.resources.getString(R.string.room_dumbledore),
                10,
                14,
                15,
                10,
                selections[4]
            ),
            Room(5, roomNameList[8], 9, 24, 15, 18, selections[5]),
            Room(6, roomNameList[0], 13, 6, 16, 0, selections[6]),
            Room(7, roomNameList[3], 19, 6, 24, 0, selections[7]),
            Room(8, roomNameList[6], 18, 15, 24, 9, selections[8]),
            Room(9, roomNameList[1], 18, 24, 24, 18, selections[9])
        )

        doorList = listOf(
            Door(0, Position(1, 7), roomList[0]),
            Door(1, Position(6, 1), roomList[0]),
            Door(2, Position(5, 8), roomList[1]),
            Door(3, Position(7, 12), roomList[1]),
            Door(4, Position(5, 16), roomList[1]),
            Door(5, Position(1, 17), roomList[2]),
            Door(6, Position(7, 23), roomList[2]),
            Door(7, Position(8, 7), roomList[3]),
            Door(8, Position(12, 2), roomList[3]),
            Door(9, Position(11, 9), roomList[4]),
            Door(10, Position(14, 9), roomList[4]),
            Door(11, Position(13, 15), roomList[4]),
            Door(12, Position(8, 19), roomList[5]),
            Door(13, Position(16, 19), roomList[5]),
            Door(14, Position(12, 4), roomList[6]),
            Door(15, Position(16, 7), roomList[6]),
            Door(16, Position(18, 2), roomList[7]),
            Door(17, Position(22, 7), roomList[7]),
            Door(18, Position(17, 12), roomList[8]),
            Door(19, Position(19, 8), roomList[8]),
            Door(20, Position(19, 16), roomList[8]),
            Door(21, Position(17, 22), roomList[9]),
            Door(22, Position(21, 17), roomList[9])
        )
    }

    suspend fun eraseNotes() {
        db.eraseNotes()
    }

    suspend fun keepCurrentPlayers(): List<BasePlayer> {
        loadSelections()

        val allPlayers = loadPlayers()

        val mysteryCards = db.getMysteryCardsOfPlayers()

        val solutionList = ArrayList<MysteryCard>()
        mysteryCards!!.forEach { card ->
            if (card.second != -1)
                allPlayers[card.second].mysteryCards.add(card.first)
            else
                solutionList.add(card.first)
        }

        val playerIds = mysteryCards.map { card -> card.second }.distinct()
        val playersToDelete = ArrayList<BasePlayer>()
        allPlayers.forEach { player ->
            if (!playerIds.contains(player.id))
                playersToDelete.add(player)
        }
        allPlayers.removeAll(playersToDelete)

        playerList = allPlayers
        gameSolution = solutionList

        return playerList
    }

    suspend fun loadPlayers(): ArrayList<BasePlayer> {
        val playerCards: ArrayList<PlayerCard> = ArrayList()
        context.resources.getStringArray(R.array.characters).forEach { name ->
            playerCards.add(db.getCardByName(name) as PlayerCard)
        }

        val listItems = ArrayList<BasePlayer>()
        withContext(Dispatchers.Main) {
            listItems.add(
                ThinkingPlayer(
                    0,
                    playerCards[0],
                    Position(17, 0),
                    R.id.ivBluePlayer,
                    Gender.WOMAN
                )
            )
            listItems.add(
                ThinkingPlayer(
                    1,
                    playerCards[1],
                    Position(17, 24),
                    R.id.ivPurplePlayer,
                    Gender.MAN
                )
            )
            listItems.add(
                ThinkingPlayer(
                    2,
                    playerCards[2],
                    Position(0, 7),
                    R.id.ivRedPlayer,
                    Gender.WOMAN
                )
            )
            listItems.add(
                ThinkingPlayer(
                    3,
                    playerCards[3],
                    Position(7, 24),
                    R.id.ivYellowPlayer,
                    Gender.MAN
                )
            )
            listItems.add(
                ThinkingPlayer(
                    4,
                    playerCards[4],
                    Position(24, 17),
                    R.id.ivWhitePlayer,
                    Gender.WOMAN
                )
            )
            listItems.add(
                ThinkingPlayer(
                    5,
                    playerCards[5],
                    Position(7, 0),
                    R.id.ivGreenPlayer,
                    Gender.MAN
                )
            )

            val gamePref = context.getSharedPreferences(
                context.resources.getString(R.string.game_params_pref),
                Context.MODE_PRIVATE
            )
            val gameMode =
                gamePref.getString(context.resources.getString(R.string.play_mode_key), "")
            val playerId = gamePref.getInt(context.resources.getString(R.string.player_id_key), 0)

            when (gameMode) {
                context.resources.getStringArray(R.array.playmodes)[0] -> {
                    (listItems.filter { p -> p.id != playerId } as MutableList<BasePlayer>).replaceAll { transformToBasePlayer(it as ThinkingPlayer) }
                }
                context.resources.getStringArray(R.array.playmodes)[1] -> {
                    listItems.replaceAll { transformToBasePlayer(it as ThinkingPlayer) }
                }
            }

            playerList = listItems
        }
        return listItems
    }

    private fun transformToBasePlayer(thinkingPlayer: ThinkingPlayer): BasePlayer {
        return BasePlayer(
            thinkingPlayer.id,
            thinkingPlayer.card,
            thinkingPlayer.pos,
            thinkingPlayer.tile,
            thinkingPlayer.gender,
            thinkingPlayer.hp,
            thinkingPlayer.mysteryCards,
            thinkingPlayer.helperCards
        )
    }

    val starList = listOf(
        Position(2, 8),
        Position(8, 10),
        Position(8, 23),
        Position(14, 17),
        Position(16, 11),
        Position(18, 6),
        Position(13, 9),
        Position(23, 16)
    )

    val cols = listOf(
        R.id.borderLeft,
        R.id.guidelineCol1,
        R.id.guidelineCol2,
        R.id.guidelineCol3,
        R.id.guidelineCol4,
        R.id.guidelineCol5,
        R.id.guidelineCol6,
        R.id.guidelineCol7,
        R.id.guidelineCol8,
        R.id.guidelineCol9,
        R.id.guidelineCol10,
        R.id.guidelineCol11,
        R.id.guidelineCol12,
        R.id.guidelineCol13,
        R.id.guidelineCol14,
        R.id.guidelineCol15,
        R.id.guidelineCol16,
        R.id.guidelineCol17,
        R.id.guidelineCol18,
        R.id.guidelineCol19,
        R.id.guidelineCol20,
        R.id.guidelineCol21,
        R.id.guidelineCol22,
        R.id.guidelineCol23,
        R.id.guidelineCol24,
        R.id.borderRight
    )
    val rows = listOf(
        R.id.borderTop,
        R.id.guidelineRow1,
        R.id.guidelineRow2,
        R.id.guidelineRow3,
        R.id.guidelineRow4,
        R.id.guidelineRow5,
        R.id.guidelineRow6,
        R.id.guidelineRow7,
        R.id.guidelineRow8,
        R.id.guidelineRow9,
        R.id.guidelineRow10,
        R.id.guidelineRow11,
        R.id.guidelineRow12,
        R.id.guidelineRow13,
        R.id.guidelineRow14,
        R.id.guidelineRow15,
        R.id.guidelineRow16,
        R.id.guidelineRow17,
        R.id.guidelineRow18,
        R.id.guidelineRow19,
        R.id.guidelineRow20,
        R.id.guidelineRow21,
        R.id.guidelineRow22,
        R.id.guidelineRow23,
        R.id.guidelineRow24,
        R.id.borderBottom
    )

    val slytherinStates = listOf(
        State(0, 0, 0, DoorState.CLOSED, false, 9),
        State(0, 1, 2, DoorState.CLOSED, false),
        State(0, 3, 7, DoorState.OPENED, false, 7),
        State(1, 0, 0, DoorState.OPENED, false),
        State(1, 1, 2, DoorState.OPENED, false),
        State(1, 3, 7, DoorState.CLOSED, false),
        State(2, 0, 0, DoorState.CLOSED, false),
        State(2, 1, 2, DoorState.CLOSED, false),
        State(2, 3, 7, DoorState.OPENED, false),
        State(3, 0, 0, DoorState.OPENED, false, 9),
        State(3, 1, 2, DoorState.CLOSED, false, 5),
        State(3, 3, 7, DoorState.CLOSED, true, 9),
        State(4, 0, 0, DoorState.CLOSED, false, 9),
        State(4, 1, 2, DoorState.OPENED, false),
        State(4, 3, 7, DoorState.OPENED, false),
        State(5, 0, 0, DoorState.OPENED, false, 7),
        State(5, 1, 2, DoorState.CLOSED, false, 9),
        State(5, 3, 7, DoorState.CLOSED, false),
        State(6, 0, 0, DoorState.OPENED, false, 7),
        State(6, 1, 2, DoorState.OPENED, false),
        State(6, 3, 7, DoorState.OPENED, false),
        State(7, 0, 0, DoorState.CLOSED, false),
        State(7, 1, 2, DoorState.CLOSED, false),
        State(7, 3, 7, DoorState.OPENED, true),
        State(8, 0, 0, DoorState.OPENED, false, 9),
        State(8, 1, 2, DoorState.OPENED, false),
        State(8, 3, 7, DoorState.CLOSED, false, 5),
        State(9, 0, 0, DoorState.CLOSED, false, 9),
        State(9, 1, 2, DoorState.OPENED, false, 9),
        State(9, 3, 7, DoorState.OPENED, false),
        State(10, 0, 0, DoorState.OPENED, false),
        State(10, 1, 2, DoorState.CLOSED, false),
        State(10, 3, 7, DoorState.CLOSED, false, 9),
        State(11, 0, 0, DoorState.CLOSED, false),
        State(11, 1, 2, DoorState.OPENED, false, 7),
        State(11, 3, 7, DoorState.OPENED, false),
        State(12, 0, 0, DoorState.OPENED, false),
        State(12, 1, 2, DoorState.CLOSED, false),
        State(12, 3, 7, DoorState.OPENED, true),
        State(13, 0, 0, DoorState.CLOSED, false, 5),
        State(13, 1, 2, DoorState.OPENED, false),
        State(13, 3, 7, DoorState.CLOSED, false),
        State(14, 0, 0, DoorState.OPENED, false, 5),
        State(14, 1, 2, DoorState.CLOSED, false, 9),
        State(14, 3, 7, DoorState.OPENED, true, 9),
        State(15, 0, 0, DoorState.CLOSED, false, 9),
        State(15, 1, 2, DoorState.OPENED, false),
        State(15, 3, 7, DoorState.CLOSED, false)
    )

    val ravenclawStates = listOf(
        State(0, 2, 6, DoorState.CLOSED, false, 7),
        State(0, 1, 4, DoorState.OPENED, false),
        State(0, 5, 12, DoorState.CLOSED, false),
        State(1, 2, 6, DoorState.OPENED, false, 7),
        State(1, 1, 4, DoorState.CLOSED, false, 0),
        State(1, 5, 12, DoorState.OPENED, true),
        State(2, 2, 6, DoorState.CLOSED, false, 8),
        State(2, 1, 4, DoorState.CLOSED, false),
        State(2, 5, 12, DoorState.OPENED, false),
        State(3, 2, 6, DoorState.OPENED, false, 8),
        State(3, 1, 4, DoorState.OPENED, false),
        State(3, 5, 12, DoorState.CLOSED, false, 3),
        State(4, 2, 6, DoorState.CLOSED, false, 7),
        State(4, 1, 4, DoorState.CLOSED, false, 7),
        State(4, 5, 12, DoorState.OPENED, false),
        State(5, 2, 6, DoorState.OPENED, false, 7),
        State(5, 1, 4, DoorState.OPENED, false),
        State(5, 5, 12, DoorState.CLOSED, true),
        State(6, 2, 6, DoorState.CLOSED, false, 0),
        State(6, 1, 4, DoorState.OPENED, false),
        State(6, 5, 12, DoorState.OPENED, false, 7),
        State(7, 2, 6, DoorState.CLOSED, false, 0),
        State(7, 1, 4, DoorState.CLOSED, false),
        State(7, 5, 12, DoorState.CLOSED, false),
        State(8, 2, 6, DoorState.OPENED, false),
        State(8, 1, 4, DoorState.OPENED, false, 3),
        State(8, 5, 12, DoorState.OPENED, false, 8),
        State(9, 2, 6, DoorState.OPENED, false, 7),
        State(9, 1, 4, DoorState.CLOSED, false),
        State(9, 5, 12, DoorState.CLOSED, false),
        State(10, 2, 6, DoorState.CLOSED, false, 7),
        State(10, 1, 4, DoorState.OPENED, false),
        State(10, 5, 12, DoorState.CLOSED, false, 7),
        State(11, 2, 6, DoorState.CLOSED, false),
        State(11, 1, 4, DoorState.CLOSED, false, 7),
        State(11, 5, 12, DoorState.OPENED, false),
        State(12, 2, 6, DoorState.OPENED, false),
        State(12, 1, 4, DoorState.OPENED, false),
        State(12, 5, 12, DoorState.OPENED, true, 0),
        State(13, 2, 6, DoorState.CLOSED, false, 3),
        State(13, 1, 4, DoorState.CLOSED, false, 8),
        State(13, 5, 12, DoorState.CLOSED, false),
        State(14, 2, 6, DoorState.OPENED, false, 3),
        State(14, 1, 4, DoorState.CLOSED, false),
        State(14, 5, 12, DoorState.CLOSED, false),
        State(15, 2, 6, DoorState.OPENED, false),
        State(15, 1, 4, DoorState.OPENED, false, 7),
        State(15, 5, 12, DoorState.OPENED, false, 7)
    )

    val gryffindorStates = listOf(
        State(0, 9, 21, DoorState.CLOSED, false, 0),
        State(0, 8, 20, DoorState.CLOSED, false, 7),
        State(0, 5, 13, DoorState.CLOSED, false),
        State(1, 9, 21, DoorState.OPENED, false),
        State(1, 8, 20, DoorState.CLOSED, false),
        State(1, 5, 13, DoorState.OPENED, false, 1),
        State(2, 9, 21, DoorState.OPENED, false, 2),
        State(2, 8, 20, DoorState.OPENED, false, 3),
        State(2, 5, 13, DoorState.OPENED, false),
        State(3, 9, 21, DoorState.CLOSED, false, 2),
        State(3, 8, 20, DoorState.CLOSED, false),
        State(3, 5, 13, DoorState.CLOSED, true, 0),
        State(4, 9, 21, DoorState.OPENED, false, 0),
        State(4, 8, 20, DoorState.OPENED, false),
        State(4, 5, 13, DoorState.CLOSED, false),
        State(5, 9, 21, DoorState.OPENED, false, 0),
        State(5, 8, 20, DoorState.CLOSED, false, 0),
        State(5, 5, 13, DoorState.CLOSED, false, 7),
        State(6, 9, 21, DoorState.CLOSED, false, 1),
        State(6, 8, 20, DoorState.OPENED, false),
        State(6, 5, 13, DoorState.OPENED, false),
        State(7, 9, 21, DoorState.CLOSED, false, 1),
        State(7, 8, 20, DoorState.CLOSED, false),
        State(7, 5, 13, DoorState.CLOSED, false, 3),
        State(8, 9, 21, DoorState.CLOSED, false, 0),
        State(8, 8, 20, DoorState.OPENED, false, 2),
        State(8, 5, 13, DoorState.OPENED, false),
        State(9, 9, 21, DoorState.OPENED, false, 0),
        State(9, 8, 20, DoorState.CLOSED, false),
        State(9, 5, 13, DoorState.CLOSED, false),
        State(10, 9, 21, DoorState.CLOSED, false, 7),
        State(10, 8, 20, DoorState.OPENED, false, 0),
        State(10, 5, 13, DoorState.OPENED, false, 0),
        State(11, 9, 21, DoorState.OPENED, false, 7),
        State(11, 8, 20, DoorState.OPENED, false),
        State(11, 5, 13, DoorState.CLOSED, false),
        State(12, 9, 21, DoorState.CLOSED, false, 3),
        State(12, 8, 20, DoorState.CLOSED, false, 1),
        State(12, 5, 13, DoorState.OPENED, true),
        State(13, 9, 21, DoorState.OPENED, false, 3),
        State(13, 8, 20, DoorState.OPENED, false),
        State(13, 5, 13, DoorState.CLOSED, false, 2),
        State(14, 9, 21, DoorState.CLOSED, false),
        State(14, 8, 20, DoorState.OPENED, false, 0),
        State(14, 5, 13, DoorState.OPENED, false),
        State(15, 9, 21, DoorState.OPENED, false, 0),
        State(15, 8, 20, DoorState.CLOSED, false),
        State(15, 5, 13, DoorState.OPENED, false, 0)
    )

    val hufflepuffStates = listOf(
        State(0, 7, 17, DoorState.CLOSED, false, 2),
        State(0, 8, 19, DoorState.OPENED, false),
        State(0, 6, 15, DoorState.CLOSED, false),
        State(1, 7, 17, DoorState.OPENED, false, 2),
        State(1, 8, 19, DoorState.CLOSED, false, 2),
        State(1, 6, 15, DoorState.OPENED, false, 9),
        State(2, 7, 17, DoorState.CLOSED, false),
        State(2, 8, 19, DoorState.OPENED, false),
        State(2, 6, 15, DoorState.CLOSED, true),
        State(3, 7, 17, DoorState.OPENED, false, 2),
        State(3, 8, 19, DoorState.CLOSED, false, 5),
        State(3, 6, 15, DoorState.OPENED, false, 1),
        State(4, 7, 17, DoorState.CLOSED, false, 2),
        State(4, 8, 19, DoorState.OPENED, false),
        State(4, 6, 15, DoorState.OPENED, false),
        State(5, 7, 17, DoorState.OPENED, false),
        State(5, 8, 19, DoorState.CLOSED, false, 0),
        State(5, 6, 15, DoorState.CLOSED, false, 2),
        State(6, 7, 17, DoorState.CLOSED, false, 2),
        State(6, 8, 19, DoorState.OPENED, false),
        State(6, 6, 15, DoorState.CLOSED, false),
        State(7, 7, 17, DoorState.OPENED, false, 2),
        State(7, 8, 19, DoorState.OPENED, false, 9),
        State(7, 6, 15, DoorState.OPENED, false),
        State(8, 7, 17, DoorState.CLOSED, false, 5),
        State(8, 8, 19, DoorState.CLOSED, false),
        State(8, 6, 15, DoorState.OPENED, false, 2),
        State(9, 7, 17, DoorState.OPENED, false, 5),
        State(9, 8, 19, DoorState.CLOSED, false, 1),
        State(9, 6, 15, DoorState.OPENED, true),
        State(10, 7, 17, DoorState.OPENED, false, 0),
        State(10, 8, 19, DoorState.OPENED, false),
        State(10, 6, 15, DoorState.CLOSED, false),
        State(11, 7, 17, DoorState.CLOSED, false, 0),
        State(11, 8, 19, DoorState.OPENED, false, 2),
        State(11, 6, 15, DoorState.OPENED, false, 2),
        State(12, 7, 17, DoorState.CLOSED, false, 9),
        State(12, 8, 19, DoorState.OPENED, false),
        State(12, 6, 15, DoorState.CLOSED, false),
        State(13, 7, 17, DoorState.OPENED, false, 9),
        State(13, 8, 19, DoorState.CLOSED, false),
        State(13, 6, 15, DoorState.OPENED, true, 5),
        State(14, 7, 17, DoorState.OPENED, false, 1),
        State(14, 8, 19, DoorState.OPENED, false, 2),
        State(14, 6, 15, DoorState.CLOSED, false),
        State(15, 7, 17, DoorState.OPENED, false, 1),
        State(15, 8, 19, DoorState.CLOSED, false),
        State(15, 6, 15, DoorState.OPENED, false, 0)
    )

    val passageWayListSlytherin = listOf(
        R.id.ivSVKToBajitaltan,
        R.id.ivSVKToJoslastan,
        R.id.ivSVKToSzuksegSzobaja,
        R.id.ivNagyteremToBajitaltan,
        R.id.ivNagyteremToJoslastanBal,
        R.id.ivNagyteremToSzuksegSzobaja,
        R.id.ivKonyvtarToBajitaltan,
        R.id.ivKonyvtarToJoslastan,
        R.id.ivKonyvtarToSzuksegSzobaja
    )
    val passageWayVisibilitiesSlytherin = listOf(
        listOf(true, false, false, false, false, false, false, true, false),
        listOf(false, false, false, false, false, false, false, false, false),
        listOf(false, false, false, false, false, false, false, false, false),
        listOf(true, false, false, false, false, true, true, false, false),
        listOf(true, false, false, false, false, false, false, false, false),
        listOf(false, true, false, true, false, false, false, false, false),
        listOf(false, true, false, false, false, false, false, false, false),
        listOf(false, false, false, false, false, false, false, false, false),
        listOf(true, false, false, false, false, false, false, false, true),
        listOf(true, false, false, true, false, false, false, false, false),
        listOf(false, false, false, false, false, false, true, false, false),
        listOf(false, false, false, false, true, false, false, false, false),
        listOf(false, false, false, false, false, false, false, false, false),
        listOf(false, false, true, false, false, false, false, false, false),
        listOf(false, false, true, true, false, false, true, false, false),
        listOf(true, false, false, false, false, false, false, false, false)
    )

    val passageWayListRavenclaw = listOf(
        R.id.ivGyengelkedoToJoslastan,
        R.id.ivGyengelkedoToKonyvtar,
        R.id.ivGyengelkedoToSVK,
        R.id.ivGyengelkedoToSerleg,
        R.id.ivNagyteremToJoslastanJobb,
        R.id.ivNagyteremToKonyvtar,
        R.id.ivNagyteremToSerleg,
        R.id.ivNagyteremToSVK,
        R.id.ivSzuksegSzobajaToJoslastanJobb,
        R.id.ivSzuksegSzobajaToKonyvtarJobb,
        R.id.ivSzuksegSzobajaToSerleg,
        R.id.ivSzuksegSzobajaToSVKJobb
    )
    val passageWayVisibilitiesRavenclaw = listOf(
        listOf(true, false, false, false, false, false, false, false, false, false, false, false),
        listOf(true, false, false, false, false, false, false, true, false, false, false, false),
        listOf(false, false, false, true, false, false, false, false, false, false, false, false),
        listOf(false, false, false, true, false, false, false, false, false, true, false, false),
        listOf(true, false, false, false, true, false, false, false, false, false, false, false),
        listOf(true, false, false, false, false, false, false, false, false, false, false, false),
        listOf(false, false, true, false, false, false, false, false, true, false, false, false),
        listOf(false, false, true, false, false, false, false, false, false, false, false, false),
        listOf(false, false, false, false, false, true, false, false, false, false, true, false),
        listOf(true, false, false, false, false, false, false, false, false, false, false, false),
        listOf(true, false, false, false, false, false, false, false, true, false, false, false),
        listOf(false, false, false, false, false, false, false, false, true, false, false, false),
        listOf(false, false, false, false, false, false, false, false, false, false, false, true),
        listOf(false, true, false, false, false, false, true, false, false, false, false, false),
        listOf(false, true, false, false, false, false, false, false, false, false, false, false),
        listOf(false, false, false, false, true, false, false, false, true, false, false, false)
    )

    val passageWayListGryffindor = listOf(
        R.id.ivBajitaltanToGyengelkedo,
        R.id.ivBajitaltanToJoslastan,
        R.id.ivBajitaltanToKonyvtar,
        R.id.ivBajitaltanToNagyterem,
        R.id.ivBajitaltanToSVK,
        R.id.ivSerlegToGyengelkedoJobb,
        R.id.ivSerlegToJoslastan,
        R.id.ivSerlegToKonyvtar,
        R.id.ivSerlegToNagyteremJobb,
        R.id.ivSerlegToSVKJobb,
        R.id.ivSzuksegSzobajaToGyengelkedo,
        R.id.ivSzuksegSzobajaToJoslastanBal,
        R.id.ivSzuksegSzobajaToKonyvtarBal,
        R.id.ivSzuksegSzobajaToNagyterem,
        R.id.ivSzuksegSzobajaToSVKBal
    )
    val passageWayVisibilitiesGryffindor = listOf(
        listOf(
            false,
            false,
            false,
            false,
            true,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false
        ),
        listOf(
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true
        ),
        listOf(
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            true,
            false,
            true,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            true,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            true
        ),
        listOf(
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true
        )
    )

    val passageWayListHufflepuff = listOf(
        R.id.ivJoslastanToBajitaltan,
        R.id.ivJoslastanToGyengelkedo,
        R.id.ivJoslastanToNagyterem,
        R.id.ivJoslastanToSVK,
        R.id.ivJoslastanToSzuksegSzobaja,
        R.id.ivSerlegToBajitaltan,
        R.id.ivSerlegToGyengelkedoBal,
        R.id.ivSerlegToNagyteremBal,
        R.id.ivSerlegToSVKBal,
        R.id.ivSerlegToSzuksegSzobaja,
        R.id.ivBagolyhazToBajitaltan,
        R.id.ivBagolyhazToGyengelkedo,
        R.id.ivBagolyhazToNagyterem,
        R.id.ivBagolyhazToSVK,
        R.id.ivBagolyhazToSzuksegSzobaja
    )
    val passageWayVisibilitiesHufflepuff = listOf(
        listOf(
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            true,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            true,
            false,
            false
        ),
        listOf(
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            true,
            false,
            false,
            false
        ),
        listOf(
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            true,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            false,
            true,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false
        ),
        listOf(
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true
        ),
        listOf(
            false,
            false,
            true,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ),
        listOf(
            false,
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            false
        )
    )
}