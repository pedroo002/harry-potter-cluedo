package neptun.jxy1vz.cluedo.ui.fragment.notes_or_dice

import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class NotesOrDiceViewModel(private val viewModelListener: ViewModelListener, private val optionListener: OptionListener) : BaseObservable() {

    interface OptionListener {
        fun onReceiveOption(option: NotesOrDiceFragment.Option)
    }

    fun openNotes() {
        optionListener.onReceiveOption(NotesOrDiceFragment.Option.NOTES)
    }

    fun rollWithDice() {
        optionListener.onReceiveOption(NotesOrDiceFragment.Option.DICE)
    }

    fun cancel() {
        viewModelListener.onFinish()
    }
}