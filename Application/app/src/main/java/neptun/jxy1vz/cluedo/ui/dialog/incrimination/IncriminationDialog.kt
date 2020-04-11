package neptun.jxy1vz.cluedo.ui.dialog.incrimination

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogIncriminationBinding

class IncriminationDialog(private val playerId: Int, private val roomId: Int): DialogFragment() {

    private lateinit var dialogIncriminationBinding: DialogIncriminationBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogIncriminationBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_incrimination, null, false)

        dialogIncriminationBinding.dialogViewModel = IncriminationViewModel(dialogIncriminationBinding, context!!, roomId)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogIncriminationBinding.root).setTitle(R.string.incriminate).create()
    }
}