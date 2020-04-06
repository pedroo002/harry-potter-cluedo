package neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.hp_loss

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogHpLossBinding

class HpLossDialog(private val hp_loss: Int) : DialogFragment() {

    private lateinit var dialogHPLossBinding: DialogHpLossBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogHPLossBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context!!),
            R.layout.dialog_hp_loss,
            null,
            false
        )
        dialogHPLossBinding.dialogHpLossViewModel = HpLossViewModel(hp_loss)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setView(dialogHPLossBinding.root).setTitle(R.string.hp_loss)
            .setNeutralButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }
}