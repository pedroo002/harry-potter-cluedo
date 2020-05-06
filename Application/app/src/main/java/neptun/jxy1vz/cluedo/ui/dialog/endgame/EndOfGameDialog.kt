package neptun.jxy1vz.cluedo.ui.dialog.endgame

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogEndOfGameBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss

class EndOfGameDialog(private val listener: DialogDismiss, private val playerName: String, private val titleId: Int, private val correct: Boolean) : DialogFragment() {

    private lateinit var dialogEndOfGameBinding: DialogEndOfGameBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogEndOfGameBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_end_of_game, null, false)
        dialogEndOfGameBinding.dialogEndOfGameViewModel = EndOfGameViewModel(playerName, correct, context!!)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogEndOfGameBinding.root).setTitle(titleId).setNeutralButton(R.string.back_to_menu) {
            dialog, _ ->
            dialog.dismiss()
        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onEndOfGameDismiss()
        super.onDismiss(dialog)
    }
}