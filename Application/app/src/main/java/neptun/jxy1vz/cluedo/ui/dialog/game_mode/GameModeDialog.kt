package neptun.jxy1vz.cluedo.ui.dialog.game_mode

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogGameModeBinding

class GameModeDialog : DialogFragment() {

    private lateinit var dialogGameModeBinding: DialogGameModeBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogGameModeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_game_mode, null, false)
        dialogGameModeBinding.dialogGameModeViewModel = GameModeViewModel(context!!, fragmentManager!!, dialogGameModeBinding)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogGameModeBinding.root).setTitle(R.string.game_mode).setNeutralButton(R.string.ok) {
            dialog, _ ->
            dialogGameModeBinding.dialogGameModeViewModel!!.setGameMode()
            dialog.dismiss()
        }.create()
    }
}