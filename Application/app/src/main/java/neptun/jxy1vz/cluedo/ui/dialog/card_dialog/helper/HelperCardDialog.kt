package neptun.jxy1vz.cluedo.ui.dialog.card_dialog.helper

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogHelperCardBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss

class HelperCardDialog(private val cardResource: Int, private val listener: DialogDismiss) : DialogFragment() {

    private lateinit var dialogHelperCardBinding: DialogHelperCardBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogHelperCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_helper_card,
            null,
            false
        )
        dialogHelperCardBinding.helperCardDialogViewModel = HelperCardViewModel(dialogHelperCardBinding, context!!, cardResource)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setView(dialogHelperCardBinding.root)
            .setTitle(resources.getString(R.string.got_helper_card)).setNeutralButton(
            resources.getString(R.string.ok)
        ) { dialog, _ ->
            dialog.dismiss()
        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onHelperCardDismiss()
        super.onDismiss(dialog)
    }
}