package neptun.jxy1vz.cluedo.ui.dialog.note

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogNoteBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Player

class NoteDialog(private val player: Player, private val listener: DialogDismiss) : DialogFragment() {

    companion object {
        const val TAG = "DIALOG_NOTE"
    }

    private lateinit var dialogNoteBinding: DialogNoteBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogNoteBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_note, null, false)
        dialogNoteBinding.dialogNoteViewModel = NoteViewModel(context!!, player, dialogNoteBinding)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_DialogWhenLarge).setView(dialogNoteBinding.root).setTitle(R.string.take_note).setNeutralButton(R.string.ok) {
            dialog, _ ->
            dialog.dismiss()
        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        dialogNoteBinding.dialogNoteViewModel!!.saveNotes()
        listener.onNoteDismiss()
        super.onDismiss(dialog)
    }
}