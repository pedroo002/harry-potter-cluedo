package neptun.jxy1vz.cluedo.ui.fragment.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentNoteBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class NoteFragment(private val player: Player, private val listener: DialogDismiss) : Fragment(),
    ViewModelListener {

    private lateinit var fragmentNoteBinding: FragmentNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentNoteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false)
        fragmentNoteBinding.noteViewModel = NoteViewModel(context!!, player, fragmentNoteBinding, this)
        return fragmentNoteBinding.root
    }

    override fun onFinish() {
        listener.onNoteDismiss()
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}