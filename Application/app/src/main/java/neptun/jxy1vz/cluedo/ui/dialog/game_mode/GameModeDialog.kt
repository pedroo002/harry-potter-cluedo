package neptun.jxy1vz.cluedo.ui.dialog.game_mode

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogGameModeBinding

class GameModeDialog : DialogFragment() {

    private lateinit var dialogGameModeBinding: DialogGameModeBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogGameModeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_game_mode, null, false)
        dialogGameModeBinding.dialogGameModeViewModel = GameModeViewModel()

        dialogGameModeBinding.spinnerPlayMode.adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.playmodes))

        val playerCount = listOf("3", "4", "5").toTypedArray()
        dialogGameModeBinding.spinnerPlayerCount.adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, playerCount)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogGameModeBinding.root).setTitle(R.string.game_mode).setNeutralButton(R.string.ok) {
            dialog, _ ->
            dialog.dismiss()
        }.create()
    }
}