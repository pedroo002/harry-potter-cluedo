package neptun.jxy1vz.hp_cluedo.ui.fragment.on_back_pressed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentOnBackPressedBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class OnBackPressedFragment : Fragment(), ViewModelListener {

    private lateinit var listener: DialogDismiss

    fun setListener(l: DialogDismiss) {
        listener = l
    }

    companion object {
        fun newInstance(listener: DialogDismiss) : OnBackPressedFragment {
            val fragment = OnBackPressedFragment()
            fragment.setListener(listener)
            return fragment
        }
    }

    private lateinit var fragmentOnBackPressedBinding: FragmentOnBackPressedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentOnBackPressedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_back_pressed, container, false)
        fragmentOnBackPressedBinding.viewModel = OnBackPressedViewModel(this)

        return fragmentOnBackPressedBinding.root
    }

    override fun onFinish() {
        listener.onBackPressedDismiss(fragmentOnBackPressedBinding.viewModel!!.quit)
        MapViewModel.enableScrolling()
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}