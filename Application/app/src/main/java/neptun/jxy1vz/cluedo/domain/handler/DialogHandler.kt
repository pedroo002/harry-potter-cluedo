package neptun.jxy1vz.cluedo.domain.handler

import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.model.DarkCard
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.ui.dialog.endgame.EndOfGameDialog
import neptun.jxy1vz.cluedo.ui.dialog.note.NoteDialog
import neptun.jxy1vz.cluedo.ui.fragment.accusation.AccusationFragment
import neptun.jxy1vz.cluedo.ui.fragment.cards.mystery.unused.UnusedMysteryCardsFragment
import neptun.jxy1vz.cluedo.ui.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.activityListener
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.fm
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.isGameAbleToContinue
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.pause
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.player
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurnDied
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.unusedMysteryCards

class DialogHandler(private val map: MapViewModel.Companion) : DialogDismiss {
    override fun onIncriminationDetailsDismiss(needToTakeNotes: Boolean) {
        mapRoot.setVerticalPanEnabled(true)
        mapRoot.setHorizontalPanEnabled(true)
        mapRoot.setScrollEnabled(true)
        if (needToTakeNotes)
            NoteDialog(player, this).show(fm, NoteDialog.TAG)
        else
            map.gameSequenceHandler.moveToNextPlayer()
    }

    override fun onCardRevealDismiss() {
        mapRoot.setVerticalPanEnabled(true)
        mapRoot.setHorizontalPanEnabled(true)
        mapRoot.setScrollEnabled(true)
        NoteDialog(player, this).show(fm, NoteDialog.TAG)
    }

    override fun onDarkCardDismiss(card: DarkCard?) {
        mapRoot.setVerticalPanEnabled(true)
        mapRoot.setHorizontalPanEnabled(true)
        mapRoot.setScrollEnabled(true)
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
        var correct = true
        for (card in gameModels.gameSolution) {
            if (card.name != suspect.room && card.name != suspect.tool && card.name != suspect.suspect)
                correct = false
        }
        val titleId = if (correct) R.string.correct_accusation else R.string.incorrect_accusation
        EndOfGameDialog(this, map.playerHandler.getPlayerById(suspect.playerId).card.name, titleId, correct).show(
            fm,
            EndOfGameDialog.TAG
        )
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
            NoteDialog(map.player, this).show(fm, NoteDialog.TAG)
        }
    }

    override fun onNoteDismiss() {
        if (!isGameAbleToContinue)
            return
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
            }
            else {
                val fragment = UnusedMysteryCardsFragment(this, unusedMysteryCards)
                map.insertFragment(fragment)
            }
            return
        }
    }
}