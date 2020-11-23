package neptun.jxy1vz.cluedo.domain.handler

import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.domain.model.card.DarkCard
import neptun.jxy1vz.cluedo.domain.model.card.DarkType
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.Position
import neptun.jxy1vz.cluedo.network.model.message.move.MovingData
import neptun.jxy1vz.cluedo.network.model.message.move.PosData
import neptun.jxy1vz.cluedo.ui.fragment.cards.dark.DarkCardFragment
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.isGameModeMulti
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.mPlayerId
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.playerImagePairs
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.unusedMysteryCards
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class PlayerHandler(private val map: MapViewModel.Companion) {
    fun useGateway(playerId: Int, from: Int, to: Int) {
        if (map.mapHandler.stepInRoom(getPlayerById(playerId).pos) == from) {
            stepPlayer(
                playerId,
                Position(gameModels.roomList[to].top, gameModels.roomList[to].left)
            )
            map.uiHandler.emptySelectionList()
        }
    }

    fun getPlayerById(id: Int): Player {
        gameModels.playerList.forEach { player ->
            if (player.id == id)
                return player
        }
        return gameModels.playerList[0]
    }

    fun getPairById(id: Int): Pair<Player, ImageView> {
        playerImagePairs.forEach { pair ->
            if (pair.first.id == id)
                return pair
        }
        return playerImagePairs[0]
    }

    fun hasKnowledgeOfUnusedCards(playerId: Int): Boolean {
        if (unusedMysteryCards.isNullOrEmpty())
            return true
        val p = getPlayerById(playerId)
        unusedMysteryCards.forEach { unusedCard ->
            if (!p.hasConclusion(unusedCard.name))
                return false
        }
        return true
    }

    fun stepPlayer(playerId: Int, targetPosition: Position) {
        while (map.mapHandler.stepInRoom(targetPosition) != -1 && map.mapHandler.isFieldOccupied(targetPosition))
            targetPosition.col++
        GlobalScope.launch(Dispatchers.Main) {
            if (isGameModeMulti())
                withContext(Dispatchers.IO) {
                    val movingData = MovingData(playerId, PosData(targetPosition.row, targetPosition.col))
                    val json = MapViewModel.retrofit.moshi.adapter(MovingData::class.java).toJson(movingData)
                    val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    MapViewModel.retrofit.cluedo.sendMovingData(MapViewModel.channelName, body)
                }
            map.uiHandler.animatePlayerWalking(playerId, getPlayerById(playerId).pos, targetPosition)
        }
    }

    fun harmToAffectedPlayers(card: DarkCard) {
        val playerIds = ArrayList<Int>()
        when (card.type) {
            DarkType.CORRIDOR -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == -1 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.PLAYER_IN_TURN -> {
                playerIds.add(playerInTurn)
            }
            DarkType.ROOM_BAGOLYHAZ -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == 6 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.ROOM_BAJITALTAN -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == 9 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.ROOM_GYENGELKEDO -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == 2 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.ROOM_JOSLASTAN -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == 7 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.ROOM_KONYVTAR -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == 3 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.ROOM_NAGYTEREM -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == 1 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.ROOM_SERLEG -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == 8 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.ROOM_SVK -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == 0 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.ROOM_SZUKSEG_SZOBAJA -> {
                gameModels.playerList.filter { player ->  map.mapHandler.stepInRoom(player.pos) == 5 }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.ALL_PLAYERS -> {
                gameModels.playerList.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.GENDER_MEN -> {
                gameModels.playerList.filter { player ->  player.gender == Player.Gender.MAN }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
            DarkType.GENDER_WOMEN -> {
                gameModels.playerList.filter { player ->  player.gender == Player.Gender.WOMAN }.forEach { player ->
                    playerIds.add(player.id)
                }
            }
        }

        val fragment = DarkCardFragment.newInstance(mPlayerId!!, card, gameModels.playerList, playerIds, map.dialogHandler)
        map.insertFragment(fragment)
    }
}