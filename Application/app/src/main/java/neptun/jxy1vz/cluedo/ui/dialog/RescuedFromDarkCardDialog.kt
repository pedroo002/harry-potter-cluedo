package neptun.jxy1vz.cluedo.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss

class RescuedFromDarkCardDialog(private val listener: DialogDismiss, private val player: Player? = null) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = if (player != null)
            "${player.card.name} megmenekült"
        else
            context!!.resources.getString(R.string.rescued)
        val message = if (player != null)
            "Mentőkártyája megvédte őt a sötét erőkkel szemben."
        else
            context!!.resources.getString(R.string.your_card_helped_you)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onLossDialogDismiss(player?.id)
        super.onDismiss(dialog)
    }
}