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
import neptun.jxy1vz.cluedo.ui.dialog.information.InformationDialog
import neptun.jxy1vz.cluedo.ui.fragment.accusation.AccusationFragment
import neptun.jxy1vz.cluedo.ui.fragment.cards.mystery.reveal.RevealMysteryCardFragment
import neptun.jxy1vz.cluedo.ui.fragment.dice_roller.DiceRollerFragment
import neptun.jxy1vz.cluedo.ui.fragment.dice_roller.DiceRollerViewModel
import neptun.jxy1vz.cluedo.ui.fragment.incrimination.IncriminationFragment
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

class InteractionHandler(private val map: MapViewModel.Companion) : IncriminationFragment.MapInterface,
    DiceRollerFragment.DiceResultInterface {
    fun showOptions(playerId: Int) {
        if (isGameRunning) {
            if (playerId == player.id && playerId == playerInTurn) {
                val roomId = map.mapHandler.stepInRoom(player.pos)
                val snackbar = Snackbar.make(mapRoot.mapLayout, mContext!!.getString(R.string.make_a_movement), Snackbar.LENGTH_LONG)
                if (!userHasToStepOrIncriminate && userCanStep) {
                    snackbar.setAction(mContext!!.getString(R.string.dice_rolling)) { rollWithDice(playerId) }
                    snackbar.show()
                } else if (roomId != -1) {
                    val title = if (roomId == 4) mContext!!.getString(R.string.your_options) else mContext!!.resources.getString(R.string.incrimination)
                    snackbar.setAction(title) { incrimination(playerId, roomId) }
                    snackbar.show()
                }
                else
                    Snackbar.make(mapRoot.mapLayout, mContext!!.getString(R.string.you_have_to_step), Snackbar.LENGTH_LONG).show()
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
        } else {
            val fragment = DiceRollerFragment(this, playerId, player.hasFelixFelicis())
            map.insertFragment(fragment)
        }
    }

    override fun onDiceRoll(playerId: Int, sum: Int, house: StateMachineHandler.HogwartsHouse?) {
        mapRoot.setScrollEnabled(true)
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
                    mContext!!.getString(R.string.helper_prefix)
                ) as? HelperCard
                else -> gameModels.db.getCardBySuperType(playerId, mContext!!.getString(R.string.dark_prefix)) as? DarkCard
            }
                ?: return@launch

            withContext(Dispatchers.Main) {
                if (playerId != player.id || type == DiceRollerViewModel.CardType.DARK)
                    map.cardHandler.showCard(playerId, randomCard, type)
                else
                    map.cardHandler.evaluateCard(playerId, randomCard, type)
            }
        }
    }

    fun incrimination(playerId: Int, roomId: Int) {
        if (playerId == player.id) {
            if (roomId != 4) {
                val fragment = IncriminationFragment(gameModels, playerId, roomId, this)
                map.insertFragment(fragment)
            }
            else {
                if (unusedMysteryCards.isNotEmpty())
                    ChooseOptionDialog(map.dialogHandler, userCanStep && !userHasToStepOrIncriminate).show(
                        fm,
                        ChooseOptionDialog.TAG
                    )
                else {
                    val fragment = AccusationFragment(playerId, map.dialogHandler)
                    map.insertFragment(fragment)
                }
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

            if (room != mContext!!.resources.getString(R.string.room_dumbledore))
                getIncrimination(suspect)
            else {
                var hasConclusionsOfThem = true
                for (card in unusedMysteryCards) {
                    if (!map.playerHandler.getPlayerById(playerId).hasConclusion(card.name))
                        hasConclusionsOfThem = false
                }
                if (hasConclusionsOfThem)
                    map.dialogHandler.onAccusationDismiss(map.playerHandler.getPlayerById(playerId).solution!!)
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
            val title = map.playerHandler.getPlayerById(suspect.playerId).card.name + mContext!!.getString(
                            R.string.some_one_incriminates)
            val message =
                mContext!!.getString(R.string.in_this_room) + suspect.room + "\n" + mContext!!.getString(
                                    R.string.with_this_tool) + suspect.tool + "\n" + mContext!!.getString(
                                                        R.string.suspect_person) + suspect.suspect
            InformationDialog(suspect, title, message, map.dialogHandler).show(fm, InformationDialog.TAG)
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
                    val fragment = RevealMysteryCardFragment(revealedCard, gameModels.playerList[playerIdx].card.name, map.dialogHandler)
                    map.insertFragment(fragment)
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
            Snackbar.make(mapRoot.mapLayout, mContext!!.getString(R.string.you_have_to_incriminate), Snackbar.LENGTH_LONG).show()
            incrimination(player.id, map.mapHandler.stepInRoom(player.pos))
        }
        else
            Snackbar.make(mapRoot.mapLayout, mContext!!.resources.getString(R.string.make_a_movement), Snackbar.LENGTH_SHORT).show()
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
        val title = mContext!!.getString(R.string.no_one_could_show)
        val message =
            mContext!!.getString(R.string.incrimination_params) + "\n\t" + mContext!!.getString(R.string.current_room) + "${suspect.room}\n\t" + mContext!!.getString(
                            R.string.current_tool) + "${suspect.tool}\n\t" + mContext!!.resources.getString(R.string.suspect_person) + suspect.suspect
        InformationDialog(null, title, message, map.dialogHandler).show(fm, InformationDialog.TAG)
    }
}