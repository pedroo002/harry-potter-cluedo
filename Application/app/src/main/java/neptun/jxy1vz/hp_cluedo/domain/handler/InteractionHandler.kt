package neptun.jxy1vz.hp_cluedo.domain.handler

import android.widget.ImageView
import kotlinx.coroutines.*
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.domain.model.Suspect
import neptun.jxy1vz.hp_cluedo.domain.model.ThinkingPlayer
import neptun.jxy1vz.hp_cluedo.domain.model.card.DarkCard
import neptun.jxy1vz.hp_cluedo.domain.model.card.HelperCard
import neptun.jxy1vz.hp_cluedo.domain.model.hasFelixFelicis
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.diceList
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.isGameModeMulti
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mContext
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mPlayerId
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.pause
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.player
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.unusedMysteryCards
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.userCanStep
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.userHasToStepOrIncriminate
import neptun.jxy1vz.hp_cluedo.ui.fragment.accusation.AccusationFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.choose_option.ChooseOptionFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.dice_roller.DiceRollerFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.dice_roller.DiceRollerViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.incrimination.IncriminationFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.incrimination.incrimination_details.IncriminationDetailsFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.notes_or_dice.NotesOrDiceFragment

class InteractionHandler(private val map: MapViewModel.Companion) : IncriminationFragment.MapInterface,
    DiceRollerFragment.DiceResultInterface {
    fun showOptions(playerId: Int) {
        if (!isGameRunning)
            return
        if (playerId == mPlayerId && playerId == playerInTurn) {
            val roomId = map.mapHandler.stepInRoom(player.pos)
            if (!userHasToStepOrIncriminate && userCanStep) {
                val optionsFragment = NotesOrDiceFragment.newInstance(map.dialogHandler)
                map.insertFragment(optionsFragment)
            } else if (roomId != -1) {
                incrimination(playerId, roomId)
            }
        }
    }

    fun rollWithDice(playerId: Int) {
        if (mPlayerId != playerId) {
            diceList.indices.forEach {i ->
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
            val fragment = DiceRollerFragment.newInstance(this, playerId, player.hasFelixFelicis())
            map.insertFragment(fragment)
        }
    }

    override fun onDiceRoll(playerId: Int, sum: Int, house: StateMachineHandler.HogwartsHouse?) {
        map.enableScrolling()
        house?.let {
            if (playerId == mPlayerId)
                map.stateMachineHandler.setState(playerId, it)
        }
        if (!pause)
            map.mapHandler.calculateMovingOptions(playerId, sum)
        if (playerId == mPlayerId)
            userHasToStepOrIncriminate = true
    }

    override fun getCard(playerId: Int, type: DiceRollerViewModel.CardType?) {
        if (type == null)
            return
        GlobalScope.launch(Dispatchers.IO) {
            val randomCard = when (type) {
                DiceRollerViewModel.CardType.HELPER -> {
                    gameModels.db.getCardBySuperType(
                        playerId,
                        mContext!!.getString(R.string.helper_prefix)
                    ) as? HelperCard
                }
                else -> gameModels.db.getCardBySuperType(playerId, mContext!!.getString(R.string.dark_prefix)) as? DarkCard
            }
                ?: return@launch

            if (isGameModeMulti() && playerId == mPlayerId)
                MapViewModel.retrofit.cluedo.sendCardEvent(MapViewModel.channelName, playerId, randomCard.name)
            if (!isGameModeMulti() || playerId == mPlayerId)
                withContext(Dispatchers.Main) {
                    map.cardHandler.showCard(playerId, randomCard, type)
                }
        }
    }

    fun incrimination(playerId: Int, roomId: Int) {
        if (playerId == mPlayerId) {
            if (roomId != 4) {
                val fragment = IncriminationFragment.newInstance(gameModels, playerId, roomId, this)
                map.insertFragment(fragment)
            }
            else {
                if (unusedMysteryCards.isNotEmpty()) {
                    val fragment = ChooseOptionFragment.newInstance(userHasToStepOrIncriminate, map.dialogHandler)
                    map.insertFragment(fragment)
                }
                else {
                    val fragment = AccusationFragment.newInstance(playerId, map.dialogHandler)
                    map.insertFragment(fragment)
                }
            }
        } else {
            val room = gameModels.roomList[roomId].name

            val tools = mContext!!.resources.getStringArray(R.array.tools)
            val suspects = mContext!!.resources.getStringArray(R.array.suspects)
            val suspect = (map.playerHandler.getPlayerById(playerId) as ThinkingPlayer).generateSuspect(room, tools, suspects)

            for (t in tools) {
                if ((map.playerHandler.getPlayerById(playerId) as ThinkingPlayer).hasSuspicion(t)) {
                    suspect.tool = t
                    break
                }
            }

            for (s in suspects) {
                if ((map.playerHandler.getPlayerById(playerId) as ThinkingPlayer).hasSuspicion(s)) {
                    suspect.suspect = s
                    break
                }
            }

            if (room != mContext!!.resources.getString(R.string.room_dumbledore))
                getIncrimination(suspect)
            else {
                var hasConclusionsOfThem = true
                if (unusedMysteryCards.any { card ->
                        !(map.playerHandler.getPlayerById(playerId) as ThinkingPlayer).hasConclusion(card.name)
                    })
                    hasConclusionsOfThem = false
                if (hasConclusionsOfThem)
                    map.dialogHandler.onAccusationDismiss((map.playerHandler.getPlayerById(playerId) as ThinkingPlayer).solution!!)
                else {
                    unusedMysteryCards.forEach {card ->
                        (map.playerHandler.getPlayerById(playerId) as ThinkingPlayer).getConclusion(card.name, -2)
                    }
                    GlobalScope.launch(Dispatchers.IO) {
                        (map.playerHandler.getPlayerById(playerId) as ThinkingPlayer).updateConclusions(gameModels.db.getAllMysteryCards())
                    }
                    map.gameSequenceHandler.moveToNextPlayer()
                }
            }
        }
    }

    override fun getIncrimination(suspect: Suspect) {
        map.uiHandler.emptySelectionList()
        GlobalScope.launch(Dispatchers.Main) {
            delay(1000)
            val detailsFragment = IncriminationDetailsFragment.newInstance(suspect, map.dialogHandler)
            map.insertFragment(detailsFragment)
        }
    }

    fun letOtherPlayersKnow(
        suspect: Suspect,
        playerWhoShowed: Int? = null,
        revealedMysteryCardName: String? = null
    ) {
        if (map.isGameModeMulti())
            return
        if (playerWhoShowed != null && revealedMysteryCardName != null) {
            gameModels.playerList.filter { p -> p.id != map.mPlayerId }.forEach { p ->
                if (p.id != playerWhoShowed && p.id != mPlayerId) {
                    if (p.id == suspect.playerId)
                        (p as ThinkingPlayer).getConclusion(revealedMysteryCardName, playerWhoShowed)
                    else
                        (p as ThinkingPlayer).getSuspicion(suspect, playerWhoShowed)
                }
            }
        } else {
            gameModels.playerList.filter { p -> p.id != map.mPlayerId }.forEach { p ->
                if (suspect.playerId != p.id)
                    (p as ThinkingPlayer).getSuspicion(suspect)
                else if (p.id == suspect.playerId) {
                    listOf(suspect.room, suspect.tool, suspect.suspect).forEach { suspectParam ->
                        if (!(p as ThinkingPlayer).ownCard(suspectParam)) {
                            GlobalScope.launch(Dispatchers.IO) {
                                if (map.playerHandler.hasKnowledgeOfUnusedCards(p.id) && !unusedMysteryCards.contains(
                                        gameModels.db.getCardByName(suspectParam)
                                    )
                                ) {
                                    p.getConclusion(suspectParam, -1)
                                    p.fillSolution(
                                        map.cardHandler.typeOf(suspectParam),
                                        suspectParam
                                    )
                                } else
                                    p.getConclusion(suspectParam, -1)
                            }
                        }
                    }
                }
            }
        }
    }
}