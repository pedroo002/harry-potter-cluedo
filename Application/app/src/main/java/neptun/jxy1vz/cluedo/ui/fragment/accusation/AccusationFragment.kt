package neptun.jxy1vz.cluedo.ui.fragment.accusation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentAccusationBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel

class AccusationFragment(private val playerId: Int, private val listener: DialogDismiss) : Fragment(),
    AccusationViewModel.FinalizationListener {

    private lateinit var fragmentAccusationBinding: FragmentAccusationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAccusationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_accusation, container, false)
        fragmentAccusationBinding.accusationViewModel = AccusationViewModel(playerId, fragmentAccusationBinding, context!!, this)
        return fragmentAccusationBinding.root
    }

    override fun onFinalized(suspect: Suspect) {
        listener.onAccusationDismiss(suspect)
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}