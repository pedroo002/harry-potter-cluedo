package neptun.jxy1vz.cluedo.ui.dialog.information

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.Suspect
import neptun.jxy1vz.cluedo.ui.map.DialogDismiss

class InformationDialog(private val suspect: Suspect?, private val title: String, private val message: String, private val listener: DialogDismiss) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setTitle(title).setMessage(message).setNeutralButton(
            R.string.ok) {
                dialog, _ ->
            dialog.dismiss()
        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        suspect?.let {
            listener.onInformationDismiss(Suspect(suspect.playerId, suspect.room, suspect.tool, suspect.suspect))
        }
        if (suspect == null)
            listener.onSimpleInformationDismiss()
        super.onDismiss(dialog)
    }
}