package neptun.jxy1vz.cluedo.ui.dialog.player_dies

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.ui.map.DialogDismiss

class UserDiesDialog(private val listener: DialogDismiss) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setTitle("Meghaltál!").setMessage("Elvesztetted házpontjaidat.\nA játéknak vége.")
            .setNeutralButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onPlayerDiesDismiss(null)
        super.onDismiss(dialog)
    }
}