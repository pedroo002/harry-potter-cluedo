package neptun.jxy1vz.cluedo.domain.handler

import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.model.DarkCard
import neptun.jxy1vz.cluedo.domain.model.HelperCard
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.ui.dialog.ChooseOptionDialog
import neptun.jxy1vz.cluedo.ui.dialog.accusation.AccusationDialog
import neptun.jxy1vz.cluedo.ui.dialog.card_dialog.reveal_mystery_card.CardRevealDialog
import neptun.jxy1vz.cluedo.ui.dialog.dice.DiceRollerDialog
import neptun.jxy1vz.cluedo.ui.dialog.dice.DiceRollerViewModel
import neptun.jxy1vz.cluedo.ui.dialog.incrimination.IncriminationDialog
import neptun.jxy1vz.cluedo.ui.dialog.information.InformationDialog
import neptun.jxy1vz.cluedo.ui.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.diceList
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.fm
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mContext
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.pause
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.player
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.unusedMysteryCards
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userCanStep
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userFinishedHisTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userHasToIncriminate
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userHasToStepOrIncriminate
import kotlin.random.Random

class InteractionHandler(private val map: MapViewModel.Companion) : IncriminationDialog.MapInterface, DiceRollerDialog.DiceResultInterface {
    fun showOptions(playerId: Int) {
        if (isGameRunning) {
            if (playerId == player.id && playerId == playerInTurn!!) {
                val roomId = map.mapHandler.stepInRoom(player.pos)
                val snackbar = Snackbar.make(mapRoot.mapLayout, "Lépj!", Snackbar.LENGTH_LONG)
                if (!userHasToStepOrIncriminate && userCanStep) {
                    snackbar.setAction("Kockadobás") { rollWithDice(playerId) }
                    snackbar.show()
                } else if (roomId != -1) {
                    val title = if (roomId == 4) "Lehetőségek" else "Gyanúsítás"
                    snackbar.setAction(title) { incrimination(playerId, roomId) }
                    snackbar.show()
                }
                else
                    Snackbar.make(mapRoot.mapLayout, "Muszáj lépned!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun rollWithDice(playerId: Int) {
        if (player.id != playerId) {
            for (i in diceList.indices) {
                val row = when (map.playerHandler.getPlayerById(playerId).pos.row) {
                    MapViewModel.ROWS -> MapViewModel.ROWS - 1
                    else -> map.playerHandler.getPlayerById(
                        playerId
                    ).pos.row + 1
                }
                val col =
                    when {
                        map.playerHandler.getPlayerById(playerId).pos.col == 0 -> 0
                        map.playerHandler.getPlayerById(playerId).pos.col >= MapViewModel.COLS - 2 -> MapViewModel.COLS - 2
                        else -> map.playerHandler.getPlayerById(
                            playerId
                        ).pos.col - 1
                    }
                map.uiHandler.setLayoutConstraintTop(diceList[i], gameModels.rows[row])
                map.uiHandler.setLayoutConstraintStart(diceList[i], gameModels.cols[col + i])
                diceList[i].visibility = ImageView.VISIBLE

                diceList[i].startAnimation(map.anim)
            }
        } else
            DiceRollerDialog(this, playerId, player.hasFelixFelicis()).show(fm, "DIALOG_DICE")
    }

    override fun onDiceRoll(playerId: Int, sum: Int, house: StateMachineHandler.HogwartsHouse?) {
        house?.let {
            if (playerId == player.id)
                map.stateMachineHandler.setState(playerId, it)
        }
        if (!pause)
            map.mapHandler.calculateMovingOptions(playerId, sum)
        if (playerId == player.id)
            userHasToStepOrIncriminate = true
    }

    override fun getCard(playerId: Int, type: DiceRollerViewModel.CardType?) {
        if (type == null)
            return
        GlobalScope.launch(Dispatchers.IO) {
            val randomCard = when (type) {
                DiceRollerViewModel.CardType.HELPER -> gameModels.db.getCardBySuperType(
                    playerId,
                    "HELPER_%"
                ) as? HelperCard
                else -> gameModels.db.getCardBySuperType(playerId, "DARK_%") as? DarkCard
            }
                ?: return@launch

            withContext(Dispatchers.Main) {
                if (playerId != player.id) {
                    map.cardHandler.showCard(playerId, randomCard, type)
                } else
                    map.cardHandler.evaluateCard(playerId, randomCard, type)
            }
        }
    }

    fun incrimination(playerId: Int, roomId: Int) {
        if (playerId == player.id) {
            if (roomId != 4)
                IncriminationDialog(gameModels, playerId, roomId, this).show(
                    fm,
                    "DIALOG_INCRIMINATION"
                )
            else {
                if (unusedMysteryCards.isNotEmpty())
                    ChooseOptionDialog(map.dialogHandler, userCanStep && !userHasToStepOrIncriminate).show(
                        fm,
                        "DIALOG_OPTIONS"
                    )
                else
                    AccusationDialog(playerId, map.dialogHandler).show(fm, "DIALOG_ACCUSATION")
            }
        } else {
            val room = gameModels.roomList[roomId].name

            val tools = mContext!!.resources.getStringArray(R.array.tools)
            val suspects = mContext!!.resources.getStringArray(R.array.suspects)
            val suspect = map.playerHandler.getPlayerById(playerId).getRandomSuspect(room, tools, suspects)

            for (t in tools) {
                if (map.playerHandler.getPlayerById(playerId).hasSuspicion(t)) {
                    suspect.tool = t
                    break
                }
            }

            for (s in suspects) {
                if (map.playerHandler.getPlayerById(playerId).hasSuspicion(s)) {
                    suspect.suspect = s
                    break
                }
            }

            if (room != "Dumbledore irodája")
                getIncrimination(suspect)
            else {
                var hasConclusionsOfThem = true
                for (card in unusedMysteryCards) {
                    if (!map.playerHandler.getPlayerById(playerId).hasConclusion(card.name))
                        hasConclusionsOfThem = false
                }
                if (hasConclusionsOfThem)
                    map.dialogHandler.onAccusationDismiss(map.playerHandler.getPlayerById(playerId).solution)
                else {
                    for (card in unusedMysteryCards) {
                        map.playerHandler.getPlayerById(playerId).getConclusion(card.name, -2)
                    }
                    GlobalScope.launch(Dispatchers.IO) {
                        map.playerHandler.getPlayerById(playerId).updateConclusions(gameModels.db.getAllMysteryCards())
                    }
                    map.gameSequenceHandler.moveToNextPlayer()
                }
            }
        }
    }

    override fun getIncrimination(suspect: Suspect) {
        map.uiHandler.emptySelectionList()

        if (suspect.playerId != player.id) {
            val title = "${map.playerHandler.getPlayerById(suspect.playerId).card.name} gyanúsít"
            val message =
                "Ebben a helyiségben: ${suspect.room}\nEzzel az eszközzel: ${suspect.tool}\nGyanúsított: ${suspect.suspect}"
            InformationDialog(suspect, title, message, map.dialogHandler).show(fm, "DIALOG_INFORMATION")
        } else {
            var someoneShowedSomething = false
            var playerIdx = gameModels.playerList.indexOf(map.playerHandler.getPlayerById(suspect.playerId))
            for (i in 0 until gameModels.playerList.size - 1) {
                playerIdx--
                if (playerIdx < 0)
                    playerIdx = gameModels.playerList.lastIndex
                val cards =
                    map.cardHandler.revealMysteryCards(playerIdx, suspect.room, suspect.tool, suspect.suspect)
                if (cards != null) {
                    val revealedCard = cards[Random.nextInt(0, cards.size)]
                    CardRevealDialog(
                        revealedCard,
                        gameModels.playerList[playerIdx].card.name,
                        map.dialogHandler
                    ).show(fm, "DIALOG_CARD_REVEAL")
                    someoneShowedSomething = true
                    letOtherPlayersKnow(
                        suspect,
                        gameModels.playerList[playerIdx].id,
                        revealedCard.name
                    )
                }
                if (someoneShowedSomething)
                    break
            }
            if (!someoneShowedSomething) {
                nothingHasBeenShowed(suspect)
                letOtherPlayersKnow(suspect)
            }

            userFinishedHisTurn = true
        }
    }

    override fun onIncriminationSkip() {
        if (userHasToIncriminate) {
            Snackbar.make(mapRoot.mapLayout, "Muszáj gyanúsítanod!", Snackbar.LENGTH_LONG).show()
            incrimination(player.id, map.mapHandler.stepInRoom(player.pos))
        }
        else
            Snackbar.make(mapRoot.mapLayout, "Lépj!", Snackbar.LENGTH_SHORT).show()
    }

    fun letOtherPlayersKnow(
        suspect: Suspect,
        playerWhoShowed: Int? = null,
        revealedMysteryCardName: String? = null
    ) {
        if (playerWhoShowed != null && revealedMysteryCardName != null) {
            for (p in gameModels.playerList) {
                if (p.id != playerWhoShowed && p.id != player.id) {
                    if (p.id == suspect.playerId)
                        p.getConclusion(revealedMysteryCardName, playerWhoShowed)
                    else
                        p.getSuspicion(suspect, playerWhoShowed)
                }
            }
        } else {
            for (p in gameModels.playerList) {
                if (p.id != player.id && suspect.playerId != p.id)
                    p.getSuspicion(suspect)
                else if (p.id == suspect.playerId) {
                    for (suspectParam in listOf(suspect.room, suspect.tool, suspect.suspect))
                        if (!p.ownCard(suspectParam)) {
                            GlobalScope.launch(Dispatchers.IO) {
                                if (map.playerHandler.hasKnowledgeOfUnusedCards(p.id) && !unusedMysteryCards.contains(
                                        gameModels.db.getCardByName(suspectParam)
                                    )
                                ) {
                                    p.getConclusion(suspectParam, -1)
                                    p.fillSolution(map.cardHandler.typeOf(suspectParam), suspectParam)
                                } else
                                    p.getConclusion(suspectParam, -1)
                            }
                        }
                }
            }
        }
    }

    fun nothingHasBeenShowed(suspect: Suspect) {
        val title = "Senki sem tudott mutatni..."
        val message =
            "Gyanúsítás paraméterei:\n\tHelyiség: ${suspect.room}\n\tEszköz: ${suspect.tool}\n\tGyanúsított: ${suspect.suspect}"
        InformationDialog(null, title, message, map.dialogHandler).show(fm, "DIALOG_SIMPLE_INFORMATION")
    }
}