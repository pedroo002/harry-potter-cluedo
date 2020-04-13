package neptun.jxy1vz.cluedo.ui.dialog.accusation

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogAccusationBinding
import neptun.jxy1vz.cluedo.model.Suspect
import neptun.jxy1vz.cluedo.ui.map.DialogDismiss

class AccusationDialog(private val playerId: Int, private val listener: DialogDismiss) : DialogFragment(), AccusationViewModel.FinalizationListener {

    private lateinit var dialogAccusationBinding: DialogAccusationBinding
    private lateinit var suspect: Suspect

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogAccusationBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_accusation, null, false)
        dialogAccusationBinding.dialogViewModel = AccusationViewModel(playerId, dialogAccusationBinding, context!!, this)

        return androidx.appcompat.app.AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_DialogWhenLarge).setView(dialogAccusationBinding.root).setTitle(R.string.accusation).create()
    }

    override fun onFinalized(suspect: Suspect) {
        this.suspect = suspect
        dialog!!.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onAccusationDismiss(suspect)
        super.onDismiss(dialog)
    }
}