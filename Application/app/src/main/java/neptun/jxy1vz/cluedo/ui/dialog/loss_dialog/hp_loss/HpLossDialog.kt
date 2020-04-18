package neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.hp_loss

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogHpLossBinding
import neptun.jxy1vz.cluedo.ui.map.DialogDismiss

class HpLossDialog(private val listener: DialogDismiss, private val hp_loss: Int, private val hp: Int) : DialogFragment() {

    private lateinit var dialogHPLossBinding: DialogHpLossBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogHPLossBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context!!),
            R.layout.dialog_hp_loss,
            null,
            false
        )
        dialogHPLossBinding.dialogHpLossViewModel = HpLossViewModel(hp_loss, hp)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setView(dialogHPLossBinding.root).setTitle(R.string.hp_loss)
            .setNeutralButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onLossDialogDismiss()
        super.onDismiss(dialog)
    }
}