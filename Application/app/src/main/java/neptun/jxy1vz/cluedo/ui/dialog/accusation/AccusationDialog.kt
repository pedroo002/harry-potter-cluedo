package neptun.jxy1vz.cluedo.ui.dialog.accusation

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogAccusationBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Suspect

class AccusationDialog(private val playerId: Int, private val listener: DialogDismiss) : DialogFragment(), AccusationViewModel.FinalizationListener {

    companion object {
        const val TAG = "DIALOG_ACCUSATION"
    }

    private lateinit var dialogAccusationBinding: DialogAccusationBinding
    private var suspect: Suspect? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogAccusationBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_accusation, null, false)
        dialogAccusationBinding.dialogViewModel = AccusationViewModel(playerId, dialogAccusationBinding, context!!, this)

        return androidx.appcompat.app.AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogAccusationBinding.root).setTitle(R.string.accusation).create()
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