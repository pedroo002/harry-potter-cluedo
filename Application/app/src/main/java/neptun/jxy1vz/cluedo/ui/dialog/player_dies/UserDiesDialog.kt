package neptun.jxy1vz.cluedo.ui.dialog.player_dies

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss

class UserDiesDialog(private val listener: DialogDismiss) : DialogFragment() {

    companion object {
        const val TAG = "DIALOG_USER_DIES"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setTitle(getString(R.string.you_died)).setMessage(getString(R.string.you_lost_your_hps) + "\n" + getString(
                            R.string.game_over))
            .setNeutralButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onPlayerDiesDismiss(null)
        super.onDismiss(dialog)
    }
}