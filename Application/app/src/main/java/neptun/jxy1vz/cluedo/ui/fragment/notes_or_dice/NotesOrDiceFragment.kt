package neptun.jxy1vz.cluedo.ui.fragment.notes_or_dice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentNotesOrDiceBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class NotesOrDiceFragment(private val listener: DialogDismiss) : Fragment(), ViewModelListener, NotesOrDiceViewModel.OptionListener {

    enum class Option {
        NOTES,
        DICE
    }

    private lateinit var fragmentNotesOrDiceBinding: FragmentNotesOrDiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentNotesOrDiceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes_or_dice, container, false)
        fragmentNotesOrDiceBinding.notesOrDiceViewModel = NotesOrDiceViewModel(this, this)
        return fragmentNotesOrDiceBinding.root
    }

    override fun onFinish() {
        MapViewModel.fm.beginTransaction().remove(this).commit()
        MapViewModel.enableScrolling()
    }

    override fun onReceiveOption(option: Option) {
        listener.onShowOptionsDismiss(option)
    }
}