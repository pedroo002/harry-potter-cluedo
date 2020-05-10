package neptun.jxy1vz.cluedo.domain.handler

import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.model.*
import neptun.jxy1vz.cluedo.domain.model.helper.getHelperObjects
import neptun.jxy1vz.cluedo.ui.dialog.RescuedFromDarkCardDialog
import neptun.jxy1vz.cluedo.ui.dialog.card_dialog.dark_mark.DarkCardDialog
import neptun.jxy1vz.cluedo.ui.fragment.dice_roller.DiceRollerViewModel
import neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.card_loss.CardLossDialog
import neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.hp_loss.HpLossDialog
import neptun.jxy1vz.cluedo.ui.dialog.player_dies.PlayerDiesDialog
import neptun.jxy1vz.cluedo.ui.dialog.player_dies.UserDiesDialog
import neptun.jxy1vz.cluedo.ui.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.fm
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mContext
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.otherPlayerStepsOnStar
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.player
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerImagePairs
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurnAffected
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.unusedMysteryCards
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userCanStep
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userFinishedHisTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userHasToIncriminate
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userHasToStepOrIncriminate
import kotlin.random.Random

class PlayerHandler(private val map: MapViewModel.Companion) : DarkCardDialog.DarkCardDialogListener, CardLossDialog.CardLossDialogListener {
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
        while (map.mapHandler.stepInRoom(targetPosition) != -1 && map.mapHandler.isFieldOccupied(targetPosition))
            targetPosition.col++
        getPlayerById(playerId).pos = targetPosition
        map.cameraHandler.moveCameraToPlayer(playerId)

        val starStep = map.mapHandler.stepOnStar(targetPosition)

        val pair = getPairById(playerId)
        map.uiHandler.setLayoutConstraintStart(pair.second, gameModels.cols[getPlayerById(playerId).pos.col])
        map.uiHandler.setLayoutConstraintTop(pair.second, gameModels.rows[getPlayerById(playerId).pos.row])

        when {
            map.mapHandler.stepInRoom(getPlayerById(playerId).pos) != -1 -> {
                if (playerId == player.id) {
                    userHasToIncriminate = true
                    userHasToStepOrIncriminate = false
                    userCanStep = false
                }
                map.interactionHandler.incrimination(playerId, map.mapHandler.stepInRoom(getPlayerById(playerId).pos))
            }
            map.mapHandler.stepInRoom(getPlayerById(playerId).pos) == -1 -> {
                if (playerId != player.id) {
                    if (starStep) {
                        map.cameraHandler.moveCameraToPlayer(playerId)
                        otherPlayerStepsOnStar = true
                        map.interactionHandler.getCard(playerId, DiceRollerViewModel.CardType.HELPER)
                    } else {
                        map.gameSequenceHandler.moveToNextPlayer()
                    }
                } else {
                    userFinishedHisTurn = true
                    if (!starStep) {
                        map.gameSequenceHandler.moveToNextPlayer()
                    } else
                        map.interactionHandler.getCard(playerId, DiceRollerViewModel.CardType.HELPER)
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
        if (playerIds.contains(playerInTurn) && playerInTurn != player.id) {
            playerInTurnAffected = true

            val tools: ArrayList<String> = ArrayList()
            val spells: ArrayList<String> = ArrayList()
            val allys: ArrayList<String> = ArrayList()

            getHelperObjects(getPlayerById(playerInTurn), card, tools, spells, allys)

            if (tools.size == 1 && spells.size == 1 && allys.size == 1)
                getLoss(playerInTurn, card)
            else
                (getLoss(playerInTurn, null))
        }

        for (id in playerIds) {
            if (id != player.id && id != playerInTurn) {
                val tools: ArrayList<String> = ArrayList()
                val spells: ArrayList<String> = ArrayList()
                val allys: ArrayList<String> = ArrayList()

                getHelperObjects(getPlayerById(id), card, tools, spells, allys)

                if (tools.size == 1 && spells.size == 1 && allys.size == 1)
                    getLoss(id, card)
                else
                    (getLoss(id, null))
            }
        }

        if (!playerIds.contains(player.id)) {
            if (playerInTurn == player.id)
                map.cardHandler.showCard(player.id, card, DiceRollerViewModel.CardType.DARK)
        }
        else
            DarkCardDialog(player, card, this).show(fm, DarkCardDialog.TAG)

        if (playerIds.isEmpty())
            map.gameSequenceHandler.continueGame()
    }

    override fun getLoss(playerId: Int, card: DarkCard?) {
        if (card == null) {
            if (playerId == player.id)
                RescuedFromDarkCardDialog(map.dialogHandler).show(fm, RescuedFromDarkCardDialog.TAG)
            else
                RescuedFromDarkCardDialog(map.dialogHandler, getPlayerById(playerId)).show(fm, RescuedFromDarkCardDialog.TAG)
        } else {
            when (card.lossType) {
                LossType.HP -> {
                    getPlayerById(playerId).hp -= card.hpLoss
                    if (playerId == player.id) {
                        if (player.hp > 0)
                            HpLossDialog(map.dialogHandler, card.hpLoss, player.hp).show(fm, HpLossDialog.TAG)
                        else {
                            UserDiesDialog(map.dialogHandler).show(fm, UserDiesDialog.TAG)
                        }
                    }
                    else {
                        if (getPlayerById(playerId).hp > 0)
                            HpLossDialog(
                                map.dialogHandler,
                                card.hpLoss,
                                getPlayerById(playerId).hp,
                                getPlayerById(playerId)
                            ).show(fm, HpLossDialog.TAG)
                        else {
                            PlayerDiesDialog(getPlayerById(playerId), map.dialogHandler).show(
                                fm,
                                PlayerDiesDialog.TAG
                            )
                            val newPlayerList = ArrayList<Player>()
                            for (p in gameModels.playerList) {
                                if (p.id != playerId)
                                    newPlayerList.add(p)
                            }
                            gameModels.playerList = newPlayerList
                            val pair = getPairById(playerId)
                            mapRoot.mapLayout.removeView(pair.second)
                            val newPlayerImagePairs = ArrayList<Pair<Player, ImageView>>()
                            for (p in playerImagePairs) {
                                if (p.first.id != playerId)
                                    newPlayerImagePairs.add(p)
                            }
                            playerImagePairs = newPlayerImagePairs
                        }
                    }
                }
                else -> {
                    if (getPlayerById(playerId).helperCards != null) {
                        val properHelperCards: ArrayList<HelperCard> = ArrayList()
                        for (helperCard in getPlayerById(playerId).helperCards!!) {
                            if ((helperCard.type as HelperType).compareTo(card.lossType))
                                properHelperCards.add(helperCard)
                        }

                        if (properHelperCards.isNotEmpty()) {
                            if (playerId == player.id)
                                CardLossDialog(
                                    playerId,
                                    properHelperCards,
                                    card.lossType,
                                    this
                                ).show(
                                    fm,
                                    CardLossDialog.TAG
                                )
                            else {
                                val cardToThrow =
                                    properHelperCards[Random.nextInt(0, properHelperCards.size)]
                                throwCard(
                                    playerId,
                                    cardToThrow
                                )
                                Snackbar.make(
                                    mapRoot,
                                    getPlayerById(playerId).card.name + mContext!!.getString(R.string.someone_threw_card) + cardToThrow.name,
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            if (playerId == player.id)
                                map.gameSequenceHandler.continueGame()
                        }
                    }
                }
            }
        }
    }

    override fun throwCard(playerId: Int, card: HelperCard) {
        getPlayerById(playerId).helperCards!!.remove(card)
        map.gameSequenceHandler.continueGame()
    }
}