package neptun.jxy1vz.cluedo.domain.handler

import neptun.jxy1vz.cluedo.domain.model.card.DarkCard
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.activityListener
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.finishedCardCheck
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.isGameAbleToContinue
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.mPlayerId
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.pause
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.player
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.playerInTurnDied
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.unusedMysteryCards
import neptun.jxy1vz.cluedo.ui.fragment.accusation.AccusationFragment
import neptun.jxy1vz.cluedo.ui.fragment.cards.mystery.UnusedMysteryCardsFragment
import neptun.jxy1vz.cluedo.ui.fragment.endgame.EndOfGameFragment
import neptun.jxy1vz.cluedo.ui.fragment.note.NoteFragment
import neptun.jxy1vz.cluedo.ui.fragment.notes_or_dice.NotesOrDiceFragment

class DialogHandler(private val map: MapViewModel.Companion) : DialogDismiss {
    override fun onIncriminationDetailsDismiss(needToTakeNotes: Boolean) {
        map.enableScrolling()
        if (needToTakeNotes) {
            val fragment = NoteFragment(player, this)
            map.insertFragment(fragment)
        } else
            map.gameSequenceHandler.moveToNextPlayer()
    }

    override fun onCardRevealDismiss() {
        map.enableScrolling()
        map.uiHandler.emptySelectionList()
        val fragment = NoteFragment(player, this)
        map.insertFragment(fragment)
    }

    override fun onDarkCardDismiss(card: DarkCard?) {
        map.enableScrolling()

        finishedCardCheck = true
        isGameAbleToContinue = true
        if (playerInTurnDied)
            map.gameSequenceHandler.moveToNextPlayer()
        else
            map.gameSequenceHandler.continueGame()
        playerInTurnDied = false
    }

    override fun onAccusationDismiss(suspect: Suspect) {
        map.enableScrolling()
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
        map.enableScrolling()
        if (!isGameAbleToContinue)
            return

        if (!isGameRunning)
            map.cardHandler.handOutHelperCards()
        else if (pause)
            map.gameSequenceHandler.continueGame()
        else
            map.gameSequenceHandler.moveToNextPlayer()
    }

    override fun onOptionsDismiss(accusation: Boolean) {
        if (accusation) {
            val fragment = AccusationFragment(playerInTurn, this)
            map.insertFragment(fragment)
        } else {
            val fragment =
                UnusedMysteryCardsFragment(
                    this,
                    unusedMysteryCards
                )
            map.insertFragment(fragment)
        }
    }

    override fun onShowOptionsDismiss(option: NotesOrDiceFragment.Option) {
        when (option) {
            NotesOrDiceFragment.Option.NOTES -> {
                isGameAbleToContinue = false
                val noteFragment = NoteFragment(player, this)
                map.insertFragment(noteFragment)
            }
            NotesOrDiceFragment.Option.DICE -> {
                isGameAbleToContinue = true
                map.interactionHandler.rollWithDice(mPlayerId!!)
            }
        }
    }

    override fun onBackPressedDismiss(quit: Boolean) {
        if (quit) {
            activityListener.exitToMenu()
            map.onDestroy()
        }
    }

    override fun onPlayerLeavesDismiss() {
        val fragment = NoteFragment(player, this)
        map.insertFragment(fragment)
    }
}