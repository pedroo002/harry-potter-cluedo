package neptun.jxy1vz.cluedo.ui.dialog.card_dialog.reveal_mystery_card

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogCardRevealBinding
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss

class CardRevealDialog(private val card: MysteryCard, private val playerName: String, private val listener: DialogDismiss) : DialogFragment() {

    private lateinit var dialogCardRevealBinding: DialogCardRevealBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogCardRevealBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_card_reveal, null, false)

        dialogCardRevealBinding.cardRevealDialogViewModel = CardRevealViewModel(dialogCardRevealBinding, context!!, card.imageRes, playerName)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogCardRevealBinding.root).setTitle(R.string.card_reveal).setNeutralButton(R.string.ok) {
            dialog, _ ->
            dialog.dismiss()
        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onCardRevealDismiss()
        super.onDismiss(dialog)
    }
}