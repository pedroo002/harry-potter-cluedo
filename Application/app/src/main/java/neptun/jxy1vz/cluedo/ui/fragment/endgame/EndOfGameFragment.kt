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

class EndOfGameFragment(private val suspect: Suspect, private val listener: DialogDismiss) : Fragment(), ViewModelListener {

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