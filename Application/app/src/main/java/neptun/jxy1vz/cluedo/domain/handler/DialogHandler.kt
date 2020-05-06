package neptun.jxy1vz.cluedo.domain.handler

import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.ui.dialog.accusation.AccusationDialog
import neptun.jxy1vz.cluedo.ui.dialog.card_dialog.unused_mystery_cards.UnusedMysteryCardsDialog
import neptun.jxy1vz.cluedo.ui.dialog.endgame.EndOfGameDialog
import neptun.jxy1vz.cluedo.ui.dialog.information.InformationDialog
import neptun.jxy1vz.cluedo.ui.dialog.note.NoteDialog
import neptun.jxy1vz.cluedo.ui.dialog.show_card.ShowCardDialog
import neptun.jxy1vz.cluedo.ui.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.activityListener
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.fm
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mContext
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.pause
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.player
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurnAffected
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.unusedMysteryCards
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.userFinishedHisTurn
import kotlin.random.Random

class DialogHandler(private val map: MapViewModel.Companion) : DialogDismiss {
    override fun onSuspectInformationDismiss(suspect: Suspect) {
        var someoneShowedSomething = false
        var playerIdx = gameModels.playerList.indexOf(map.playerHandler.getPlayerById(suspect.playerId))
        for (i in 0 until gameModels.playerList.size - 1) {
            playerIdx--
            if (playerIdx < 0)
                playerIdx = gameModels.playerList.lastIndex
            if (playerIdx == gameModels.playerList.indexOf(player)) {
                val cards =
                    map.cardHandler.revealMysteryCards(playerIdx, suspect.room, suspect.tool, suspect.suspect)
                if (cards != null) {
                    ShowCardDialog(
                        suspect,
                        map.playerHandler.getPlayerById(suspect.playerId).card.name,
                        cards,
                        this
                    ).show(fm, ShowCardDialog.TAG)
                    someoneShowedSomething = true
                }
            } else {
                val cards =
                    map.cardHandler.revealMysteryCards(playerIdx, suspect.room, suspect.tool, suspect.suspect)
                if (cards != null) {
                    val revealedCard = cards[Random.nextInt(0, cards.size)]

                    val title = mContext!!.getString(R.string.card_reveal_happened)
                    val message =
                        gameModels.playerList[playerIdx].card.name + mContext!!.getString(R.string.showed_something) + map.playerHandler.getPlayerById(
                            suspect.playerId
                        ).card.name + "\n" + mContext!!.resources.getString(R.string.incrimination_params) + "\n\t" + mContext!!.resources.getString(R.string.current_room) + suspect.room + "\n\t" +
                                mContext!!.resources.getString(R.string.current_tool) + "${suspect.tool}\n\t" +
                                mContext!!.resources.getString(R.string.suspect_person) + suspect.suspect
                    InformationDialog(null, title, message, this).show(
                        fm,
                        InformationDialog.TAG
                    )
                    someoneShowedSomething = true
                    map.interactionHandler.letOtherPlayersKnow(
                        suspect,
                        gameModels.playerList[playerIdx].id,
                        revealedCard.name
                    )
                }
            }
            if (someoneShowedSomething)
                break
        }
        if (!someoneShowedSomething) {
            map.interactionHandler.nothingHasBeenShowed(suspect)
            map.interactionHandler.letOtherPlayersKnow(suspect)
        }
    }

    override fun onSimpleInformationDismiss() {
        NoteDialog(player, this).show(fm, NoteDialog.TAG)
    }

    override fun onCardRevealDismiss() {
        NoteDialog(player, this).show(fm, NoteDialog.TAG)
    }

    override fun onCardShowDismiss(suspect: Suspect, card: MysteryCard) {
        map.interactionHandler.letOtherPlayersKnow(suspect, player.id, card.name)
        map.gameSequenceHandler.moveToNextPlayer()
    }

    override fun onHelperCardDismiss() {
        if (userFinishedHisTurn)
            map.gameSequenceHandler.moveToNextPlayer()
    }

    override fun onAccusationDismiss(suspect: Suspect?) {
        if (suspect == null) {
            Snackbar.make(mapRoot.mapLayout, mContext!!.getString(R.string.make_your_accusation), Snackbar.LENGTH_LONG).show()
            AccusationDialog(playerInTurn, this).show(fm, AccusationDialog.TAG)
            return
        }
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
    }

    override fun onLossDialogDismiss(playerId: Int?) {
        when {
            playerId == playerInTurn -> {
                playerInTurnAffected = false
                map.gameSequenceHandler.continueGame()
                return
            }
            playerInTurnAffected -> return
            playerId != null && !playerInTurnAffected -> {
                map.gameSequenceHandler.continueGame()
                return
            }
            playerId == null && playerInTurn != player.id && !playerInTurnAffected -> {
                map.gameSequenceHandler.continueGame()
                return
            }
            else -> map.gameSequenceHandler.continueGame()
        }
    }

    override fun onPlayerDiesDismiss(player: Player?) {
        if (player == null)
            activityListener.exitToMenu()
        else {
            NoteDialog(map.player, this).show(fm, NoteDialog.TAG)
        }
    }

    override fun onNoteDismiss() {
        if (!isGameRunning)
            map.cardHandler.handOutHelperCards()
        else if (pause)
            map.gameSequenceHandler.continueGame()
        else
            map.gameSequenceHandler.moveToNextPlayer()
    }

    override fun onOptionsDismiss(accusation: Boolean?) {
        accusation?.let {
            if (accusation)
                AccusationDialog(playerInTurn, this).show(fm, AccusationDialog.TAG)
            else
                UnusedMysteryCardsDialog(this, unusedMysteryCards).show(fm, UnusedMysteryCardsDialog.TAG)
            return
        }
    }
}