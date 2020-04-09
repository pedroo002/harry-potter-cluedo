package neptun.jxy1vz.cluedo.ui.dialog.card_dialog.dark_mark

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogDarkCardBinding
import neptun.jxy1vz.cluedo.model.DarkCard
import neptun.jxy1vz.cluedo.model.Player

class DarkCardDialog(
    private val player: Player,
    private val darkCard: DarkCard,
    private val listener: DarkCardDialogListener
) : DialogFragment() {

    interface DarkCardDialogListener {
        fun getLoss(playerId: Int, card: DarkCard?)
    }

    private lateinit var dialogDarkCardBinding: DialogDarkCardBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogDarkCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_dark_card,
            null,
            false
        )
        dialogDarkCardBinding.darkCardDialogViewModel = DarkCardViewModel(dialogDarkCardBinding, context!!, player, darkCard)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_DialogWhenLarge)
            .setView(dialogDarkCardBinding.root)
            .setTitle(resources.getString(R.string.sotet_jegy)).setNeutralButton(
                resources.getString(R.string.ok)
            ) { dialog, _ ->
                listener.getLoss(player.id, dialogDarkCardBinding.darkCardDialogViewModel!!.getLoss())
                dialog.dismiss()
            }.create()
    }
}