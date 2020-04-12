package neptun.jxy1vz.cluedo.ui.dialog.information

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R

class InformationDialog(private val playerId: Int, private val name: String,  private val room: String, private val tool: String, private val suspect: String, private val listener: InformationDialogListener) : DialogFragment() {

    interface InformationDialogListener {
        fun onDismiss(playerId: Int, room: String, tool: String, suspect: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setTitle("$name gyanúsít").setMessage("Ebben a helyiségben: $room\nEzzel az eszközzel: $tool\nGyanúsított: $suspect").setNeutralButton(
            R.string.ok) {
                dialog, _ ->
            dialog.dismiss()
        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onDismiss(playerId, room, tool, suspect)
        super.onDismiss(dialog)
    }
}