package neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.hp_loss

import androidx.databinding.BaseObservable

class HpLossViewModel(private val hp_loss: Int, private val hp: Int) : BaseObservable() {
    fun getLoss(): String {
        return hp_loss.toString()
    }

    fun getHp(): String {
        return hp.toString()
    }
}