package neptun.jxy1vz.cluedo.ui.fragment.choose_option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentChooseOptionBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class ChooseOptionFragment(private val canStep: Boolean, private val listener: DialogDismiss) : Fragment(), ViewModelListener {

    private lateinit var fragmentChooseOptionBinding: FragmentChooseOptionBinding

    companion object {
        var accusation = false
        var step = false
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
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}