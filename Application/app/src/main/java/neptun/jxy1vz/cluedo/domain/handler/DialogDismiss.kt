package neptun.jxy1vz.cluedo.domain.handler

import neptun.jxy1vz.cluedo.domain.model.DarkCard
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.Suspect

interface DialogDismiss {
    fun onIncriminationDetailsDismiss(needToTakeNotes: Boolean)
    fun onCardRevealDismiss()
    fun onDarkCardDismiss(card: DarkCard?)
    fun onAccusationDismiss(suspect: Suspect)
    fun onEndOfGameDismiss()
    fun onPlayerDiesDismiss(player: Player?)
    fun onNoteDismiss()
    fun onOptionsDismiss(accusation: Boolean? = null)
}