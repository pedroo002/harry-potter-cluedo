package neptun.jxy1vz.cluedo.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss

class ChooseOptionDialog(private val listener: DialogDismiss, private val canStep: Boolean) :
    DialogFragment() {

    private var accusation: Boolean? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setTitle(R.string.choose_option).setMessage(R.string.purpose_in_room)
            .setPositiveButton(R.string.accusation) { dialog, _ ->
                accusation = true
                dialog.dismiss()
            }.setNegativeButton(R.string.check_cards) { dialog, _ ->
            accusation = false
            dialog.dismiss()
        }
        if (canStep)
            alertDialog.setNeutralButton(R.string.step) { dialog, _ ->
                dialog.dismiss()
            }
        return alertDialog.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onOptionsDismiss(accusation)
        super.onDismiss(dialog)
    }
}