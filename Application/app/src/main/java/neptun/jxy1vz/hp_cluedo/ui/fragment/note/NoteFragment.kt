package neptun.jxy1vz.hp_cluedo.ui.fragment.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.FragmentNoteBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class NoteFragment : Fragment(),
    ViewModelListener {

    private lateinit var player: Player
    private lateinit var listener: DialogDismiss

    fun setArgs(p: Player, l: DialogDismiss) {
        player = p
        listener = l
    }

    companion object {
        fun newInstance(player: Player, listener: DialogDismiss) : NoteFragment {
            val fragment = NoteFragment()
            fragment.setArgs(player, listener)
            return fragment
        }
    }

    private lateinit var fragmentNoteBinding: FragmentNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentNoteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false)
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                val notepad = getAssetByTag("resources/map/note/notepad.png")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(notepad, context!!, fragmentNoteBinding.ivNotepad)
                    fragmentNoteBinding.noteViewModel = NoteViewModel(context!!, player, fragmentNoteBinding, this@NoteFragment)
                }
            }
        }
        return fragmentNoteBinding.root
    }

    override fun onFinish() {
        listener.onNoteDismiss()
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}