package neptun.jxy1vz.cluedo.domain.handler

import neptun.jxy1vz.cluedo.domain.model.DarkCard
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.Suspect

interface DialogDismiss {
    fun onSuspectInformationDismiss(suspect: Suspect)
    fun onSimpleInformationDismiss()
    fun onCardRevealDismiss()
    fun onCardShowDismiss(suspect: Suspect, card: MysteryCard)
    fun onHelperCardDismiss()
    fun onDarkCardDismiss(card: DarkCard?)
    fun onAccusationDismiss(suspect: Suspect)
    fun onEndOfGameDismiss()
    fun onLossDialogDismiss(playerId: Int? = null)
    fun onPlayerDiesDismiss(player: Player?)
    fun onNoteDismiss()
    fun onOptionsDismiss(accusation: Boolean? = null)
}