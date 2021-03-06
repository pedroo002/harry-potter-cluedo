package neptun.jxy1vz.hp_cluedo.domain.handler

import neptun.jxy1vz.hp_cluedo.domain.model.BasePlayer
import neptun.jxy1vz.hp_cluedo.domain.model.card.DarkCard
import neptun.jxy1vz.hp_cluedo.domain.model.ThinkingPlayer
import neptun.jxy1vz.hp_cluedo.domain.model.Suspect
import neptun.jxy1vz.hp_cluedo.ui.fragment.notes_or_dice.NotesOrDiceFragment

interface DialogDismiss {
    fun onIncriminationDetailsDismiss()
    fun onCardRevealDismiss()
    fun onDarkCardDismiss(card: DarkCard?)
    fun onAccusationDismiss(suspect: Suspect)
    fun onEndOfGameDismiss()
    fun onPlayerDiesDismiss(player: BasePlayer?)
    fun onNoteDismiss()
    fun onOptionsDismiss(accusation: Boolean)
    fun onShowOptionsDismiss(option: NotesOrDiceFragment.Option)
    fun onBackPressedDismiss(quit: Boolean)
    fun onPlayerLeavesDismiss(setWaitingQueue: Boolean = true)
}