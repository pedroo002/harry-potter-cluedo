package neptun.jxy1vz.cluedo.ui.fragment.endgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentEndOfGameBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class EndOfGameFragment : Fragment(), ViewModelListener {

    private lateinit var suspect: Suspect
    private lateinit var listener: DialogDismiss

    fun setArgs(sus: Suspect, l: DialogDismiss) {
        suspect = sus
        listener = l
    }

    companion object {
        var goodSolution = false

        fun newInstance(suspect: Suspect, listener: DialogDismiss): EndOfGameFragment {
            goodSolution = false
            val fragment = EndOfGameFragment()
            fragment.setArgs(suspect, listener)
            return fragment
        }
    }

    private lateinit var fragmentEndOfGameBinding: FragmentEndOfGameBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentEndOfGameBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_end_of_game, container, false)
        fragmentEndOfGameBinding.endOfGameViewModel = EndOfGameViewModel(fragmentEndOfGameBinding, context!!, suspect, this)
        return fragmentEndOfGameBinding.root
    }

    override fun onFinish() {
        listener.onEndOfGameDismiss()
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}