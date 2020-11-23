package neptun.jxy1vz.hp_cluedo.ui.fragment.on_back_pressed

import androidx.databinding.BaseObservable
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class OnBackPressedViewModel(private val listener: ViewModelListener) : BaseObservable() {

    var quit = false

    fun cancel() {
        listener.onFinish()
    }

    fun ok() {
        quit = true
        listener.onFinish()
    }
}