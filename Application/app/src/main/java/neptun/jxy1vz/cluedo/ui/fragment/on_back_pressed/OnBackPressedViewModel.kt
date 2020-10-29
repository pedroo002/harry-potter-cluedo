package neptun.jxy1vz.cluedo.ui.fragment.on_back_pressed

import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

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