package neptun.jxy1vz.hp_cluedo.ui.fragment.channel.num_picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentNumPickerBinding
import neptun.jxy1vz.hp_cluedo.ui.fragment.channel.num_picker.NumPickerViewModel.ViewModelListener

class NumPickerFragment : Fragment(),
    ViewModelListener {

    private lateinit var listener: NumPickerChangeListener

    fun setListener(l: NumPickerChangeListener) {
        listener = l
    }

    companion object {
        fun newInstance(listener: NumPickerChangeListener): NumPickerFragment {
            val fragment = NumPickerFragment()
            fragment.setListener(listener)
            return fragment
        }
    }

    interface NumPickerChangeListener {
        fun onValueChanged(num1: Int, num2: Int, num3: Int, num4: Int)
    }

    private lateinit var fragmentNumPickerBinding: FragmentNumPickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentNumPickerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_num_picker, container, false)

        fragmentNumPickerBinding.viewModel = NumPickerViewModel(fragmentNumPickerBinding, this)

        return fragmentNumPickerBinding.root
    }

    override fun onUpdate(num1: Int, num2: Int, num3: Int, num4: Int) {
        listener.onValueChanged(num1, num2, num3, num4)
    }

    fun disablePickers() {
        fragmentNumPickerBinding.viewModel!!.disablePickers()
    }
}