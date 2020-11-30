package neptun.jxy1vz.hp_cluedo.ui.fragment.notes_or_dice

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
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.FragmentNotesOrDiceBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class NotesOrDiceFragment : Fragment(), ViewModelListener, NotesOrDiceViewModel.OptionListener {

    private lateinit var listener: DialogDismiss

    fun setListener(l: DialogDismiss) {
        listener = l
    }

    companion object {
        fun newInstance(listener: DialogDismiss) : NotesOrDiceFragment {
            val fragment = NotesOrDiceFragment()
            fragment.setListener(listener)
            return fragment
        }
    }

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
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                val notes = getAssetByTag("resources/map/other/notes.png")!!.url
                val dice = getAssetByTag("resources/map/other/dice.png")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(notes, context!!, fragmentNotesOrDiceBinding.ivNotes)
                    loadUrlImageIntoImageView(dice, context!!, fragmentNotesOrDiceBinding.ivDice)
                    fragmentNotesOrDiceBinding.notesOrDiceViewModel = NotesOrDiceViewModel(this@NotesOrDiceFragment, this@NotesOrDiceFragment)
                }
            }
        }
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