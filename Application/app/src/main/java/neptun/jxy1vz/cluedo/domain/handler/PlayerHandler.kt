package neptun.jxy1vz.cluedo.domain.handler

import android.widget.ImageView
import neptun.jxy1vz.cluedo.domain.model.*
import neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.card_loss.CardLossDialog
import neptun.jxy1vz.cluedo.ui.fragment.cards.dark.DarkCardFragment
import neptun.jxy1vz.cluedo.ui.fragment.dice_roller.DiceRollerViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.otherPlayerStepsOnStar
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.player
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerImagePairs
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.unusedMysteryCards
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userCanStep
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userFinishedHisTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userHasToIncriminate
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userHasToStepOrIncriminate

class PlayerHandler(private val map: MapViewModel.Companion) :
    CardLossDialog.CardLossDialogListener {
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
        for (player in gameModels.playerList) {
            if (player.id == id)
                return player
        }
        return gameModels.playerList[0]
    }

    fun getPairById(id: Int): Pair<Player, ImageView> {
        for (pair in playerImagePairs) {
            if (pair.first.id == id)
                return pair
        }
        return playerImagePairs[0]
    }

    fun hasKnowledgeOfUnusedCards(playerId: Int): Boolean {
        if (unusedMysteryCards.isNullOrEmpty())
            return true
        val p = getPlayerById(playerId)
        for (unusedCard in unusedMysteryCards) {
            if (!p.hasConclusion(unusedCard.name))
                return false
        }
        return true
    }

    fun stepPlayer(playerId: Int, targetPosition: Position) {
        while (map.mapHandler.stepInRoom(targetPosition) != -1 && map.mapHandler.isFieldOccupied(
                targetPosition
            )
        )
            targetPosition.col++
        getPlayerById(playerId).pos = targetPosition
        map.cameraHandler.moveCameraToPlayer(playerId)

        val starStep = map.mapHandler.stepOnStar(targetPosition)

        val pair = getPairById(playerId)
        map.uiHandler.setLayoutConstraintStart(
            pair.second,
            gameModels.cols[getPlayerById(playerId).pos.col]
        )
        map.uiHandler.setLayoutConstraintTop(
            pair.second,
            gameModels.rows[getPlayerById(playerId).pos.row]
        )

        when {
            map.mapHandler.stepInRoom(getPlayerById(playerId).pos) != -1 -> {
                if (playerId == player.id) {
                    userHasToIncriminate = true
                    userHasToStepOrIncriminate = false
                    userCanStep = false
                }
                map.interactionHandler.incrimination(
                    playerId,
                    map.mapHandler.stepInRoom(getPlayerById(playerId).pos)
                )
            }
            map.mapHandler.stepInRoom(getPlayerById(playerId).pos) == -1 -> {
                if (playerId != player.id) {
                    if (starStep) {
                        map.cameraHandler.moveCameraToPlayer(playerId)
                        otherPlayerStepsOnStar = true
                        map.interactionHandler.getCard(
                            playerId,
                            DiceRollerViewModel.CardType.HELPER
                        )
                    } else {
                        map.gameSequenceHandler.moveToNextPlayer()
                    }
                } else {
                    userFinishedHisTurn = true
                    if (!starStep) {
                        map.gameSequenceHandler.moveToNextPlayer()
                    } else
                        map.interactionHandler.getCard(
                            playerId,
                            DiceRollerViewModel.CardType.HELPER
                        )
                }
            }
        }
    }

    fun harmToAffectedPlayers(card: DarkCard) {
        val playerIds = ArrayList<Int>()
        when (card.type) {
            DarkType.CORRIDOR -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == -1)
                        playerIds.add(player.id)
                }
            }
            DarkType.PLAYER_IN_TURN -> {
                playerIds.add(playerInTurn)
            }
            DarkType.ROOM_BAGOLYHAZ -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == 6)
                        playerIds.add(player.id)
                }
            }
            DarkType.ROOM_BAJITALTAN -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == 9)
                        playerIds.add(player.id)
                }
            }
            DarkType.ROOM_GYENGELKEDO -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == 2)
                        playerIds.add(player.id)
                }
            }
            DarkType.ROOM_JOSLASTAN -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == 7)
                        playerIds.add(player.id)
                }
            }
            DarkType.ROOM_KONYVTAR -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == 3)
                        playerIds.add(player.id)
                }
            }
            DarkType.ROOM_NAGYTEREM -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == 1)
                        playerIds.add(player.id)
                }
            }
            DarkType.ROOM_SERLEG -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == 8)
                        playerIds.add(player.id)
                }
            }
            DarkType.ROOM_SVK -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == 0)
                        playerIds.add(player.id)
                }
            }
            DarkType.ROOM_SZUKSEG_SZOBAJA -> {
                for (player in gameModels.playerList) {
                    if (map.mapHandler.stepInRoom(player.pos) == 5)
                        playerIds.add(player.id)
                }
            }
            DarkType.ALL_PLAYERS -> {
                for (player in gameModels.playerList) {
                    playerIds.add(player.id)
                }
            }
            DarkType.GENDER_MEN -> {
                for (player in gameModels.playerList) {
                    if (player.gender == Player.Gender.MAN)
                        playerIds.add(player.id)
                }
            }
            DarkType.GENDER_WOMEN -> {
                for (player in gameModels.playerList) {
                    if (player.gender == Player.Gender.WOMAN)
                        playerIds.add(player.id)
                }
            }
        }

        val fragment = DarkCardFragment(player.id, card, gameModels.playerList, playerIds, map.dialogHandler)
        map.insertFragment(fragment)
    }

    override fun throwCard(playerId: Int, card: HelperCard) {
        getPlayerById(playerId).helperCards!!.remove(card)
    }
}