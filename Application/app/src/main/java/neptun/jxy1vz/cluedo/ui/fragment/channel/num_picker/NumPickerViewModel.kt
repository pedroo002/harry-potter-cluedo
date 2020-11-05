package neptun.jxy1vz.cluedo.ui.fragment.channel.num_picker

import android.graphics.Color
import android.widget.NumberPicker
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_num_picker.view.*
import neptun.jxy1vz.cluedo.databinding.FragmentNumPickerBinding
import neptun.jxy1vz.cluedo.domain.util.setNumPicker

class NumPickerViewModel(private val bind: FragmentNumPickerBinding, private val listener: ViewModelListener) : BaseObservable() {

    interface ViewModelListener {
        fun onUpdate(num1: Int, num2: Int, num3: Int, num4: Int)
    }

    init {
        initPicker(bind.numPickerRoot.numAuthKey1)
        initPicker(bind.numPickerRoot.numAuthKey2)
        initPicker(bind.numPickerRoot.numAuthKey3)
        initPicker(bind.numPickerRoot.numAuthKey4)
    }

    private fun initPicker(numPicker: NumberPicker) {
        setNumPicker(numPicker, 0, 9, Color.WHITE)
        numPicker.setOnValueChangedListener { _, _, _ ->
            listener.onUpdate(bind.numAuthKey1.value, bind.numAuthKey2.value, bind.numAuthKey3.value, bind.numAuthKey4.value)
        }
    }

    fun disablePickers() {
        bind.numPickerRoot.numAuthKey1.isEnabled = false
        bind.numPickerRoot.numAuthKey2.isEnabled = false
        bind.numPickerRoot.numAuthKey3.isEnabled = false
        bind.numPickerRoot.numAuthKey4.isEnabled = false
    }
}