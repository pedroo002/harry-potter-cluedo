package neptun.jxy1vz.hp_cluedo.ui.fragment.choose_option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentChooseOptionBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class ChooseOptionFragment : Fragment(), ViewModelListener {

    private lateinit var fragmentChooseOptionBinding: FragmentChooseOptionBinding

    private var canStep: Boolean = false
    private lateinit var listener: DialogDismiss

    fun setArgs(steppable: Boolean, l: DialogDismiss) {
        canStep = steppable
        listener = l
    }

    companion object {
        var accusation = false
        var step = false

        fun newInstance(canStep: Boolean, listener: DialogDismiss): ChooseOptionFragment {
            val fragment = ChooseOptionFragment()
            fragment.setArgs(canStep, listener)
            return fragment
        }
    }

    init {
        accusation = false
        step = false

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChooseOptionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_option, container, false)
        fragmentChooseOptionBinding.chooseOptionViewModel = ChooseOptionViewModel(fragmentChooseOptionBinding, context!!, canStep, this)
        return fragmentChooseOptionBinding.root
    }

    override fun onFinish() {
        if (!step)
            listener.onOptionsDismiss(accusation)
        else
            MapViewModel.enableScrolling()
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}