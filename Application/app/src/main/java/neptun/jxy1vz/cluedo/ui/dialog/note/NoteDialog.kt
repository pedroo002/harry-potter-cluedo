package neptun.jxy1vz.cluedo.ui.dialog.note

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogNoteBinding

class NoteDialog : DialogFragment() {

    private lateinit var dialogNoteBinding: DialogNoteBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogNoteBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_note, null, false)
        dialogNoteBinding.dialogNoteViewModel = NoteViewModel()

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogNoteBinding.root).setTitle(R.string.take_note).setNeutralButton(R.string.ok) {
            dialog, _ ->
            dialog.dismiss()
        }.create()
    }
}