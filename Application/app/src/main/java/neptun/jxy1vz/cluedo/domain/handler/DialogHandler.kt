package neptun.jxy1vz.cluedo.domain.handler

import neptun.jxy1vz.cluedo.domain.model.DarkCard
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.activityListener
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.finishedCardCheck
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.isGameAbleToContinue
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.pause
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.player
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.playerInTurnDied
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.unusedMysteryCards
import neptun.jxy1vz.cluedo.ui.fragment.accusation.AccusationFragment
import neptun.jxy1vz.cluedo.ui.fragment.cards.mystery.unused.UnusedMysteryCardsFragment
import neptun.jxy1vz.cluedo.ui.fragment.endgame.EndOfGameFragment
import neptun.jxy1vz.cluedo.ui.fragment.note.NoteFragment

class DialogHandler(private val map: MapViewModel.Companion) : DialogDismiss {
    override fun onIncriminationDetailsDismiss(needToTakeNotes: Boolean) {
        mapRoot.setVerticalPanEnabled(true)
        mapRoot.setHorizontalPanEnabled(true)
        mapRoot.setScrollEnabled(true)
        if (needToTakeNotes) {
            val fragment = NoteFragment(player, this)
            map.insertFragment(fragment)
        } else
            map.gameSequenceHandler.moveToNextPlayer()
    }

    override fun onCardRevealDismiss() {
        mapRoot.setVerticalPanEnabled(true)
        mapRoot.setHorizontalPanEnabled(true)
        mapRoot.setScrollEnabled(true)
        val fragment = NoteFragment(player, this)
        map.insertFragment(fragment)
    }

    override fun onDarkCardDismiss(card: DarkCard?) {
        mapRoot.setVerticalPanEnabled(true)
        mapRoot.setHorizontalPanEnabled(true)
        mapRoot.setScrollEnabled(true)

        finishedCardCheck = true
        isGameAbleToContinue = true
        if (playerInTurnDied)
            map.gameSequenceHandler.moveToNextPlayer()
        else
            map.gameSequenceHandler.continueGame()
        playerInTurnDied = false
    }

    override fun onAccusationDismiss(suspect: Suspect) {
        mapRoot.setVerticalPanEnabled(true)
        mapRoot.setHorizontalPanEnabled(true)
        mapRoot.setScrollEnabled(true)
        val fragment = EndOfGameFragment(suspect, this)
        map.insertFragment(fragment)
        isGameRunning = false
    }

    override fun onEndOfGameDismiss() {
        activityListener.exitToMenu()
        map.onDestroy()
    }

    override fun onPlayerDiesDismiss(player: Player?) {
        if (player == null)
            activityListener.exitToMenu()
        else {
            val fragment = NoteFragment(MapViewModel.player, this)
            map.insertFragment(fragment)
        }
    }

    override fun onNoteDismiss() {
        if (!isGameAbleToContinue)
            return
        mapRoot.setVerticalPanEnabled(true)
        mapRoot.setHorizontalPanEnabled(true)
        mapRoot.setScrollEnabled(true)

        if (!isGameRunning)
            map.cardHandler.handOutHelperCards()
        else if (pause)
            map.gameSequenceHandler.continueGame()
        else
            map.gameSequenceHandler.moveToNextPlayer()
    }

    override fun onOptionsDismiss(accusation: Boolean?) {
        accusation?.let {
            if (accusation) {
                val fragment = AccusationFragment(playerInTurn, this)
                map.insertFragment(fragment)
            } else {
                val fragment = UnusedMysteryCardsFragment(this, unusedMysteryCards)
                map.insertFragment(fragment)
            }
            return
        }
    }
}