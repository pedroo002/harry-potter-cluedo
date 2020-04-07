package neptun.jxy1vz.cluedo.ui.dialog.game_mode

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogGameModeBinding

class GameModeDialog : DialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var dialogGameModeBinding: DialogGameModeBinding

    private lateinit var gameMode: String
    private var playerCount: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogGameModeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_game_mode, null, false)
        dialogGameModeBinding.dialogGameModeViewModel = GameModeViewModel(context!!, fragmentManager!!)

        dialogGameModeBinding.spinnerPlayMode.adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.playmodes))

        val playerCounts = listOf("3", "4", "5").toTypedArray()
        dialogGameModeBinding.spinnerPlayerCount.adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, playerCounts)

        dialogGameModeBinding.spinnerPlayMode.onItemSelectedListener = this
        dialogGameModeBinding.spinnerPlayerCount.onItemSelectedListener = this

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogGameModeBinding.root).setTitle(R.string.game_mode).setNeutralButton(R.string.ok) {
            dialog, _ ->
            dialogGameModeBinding.dialogGameModeViewModel!!.setGameMode(gameMode, playerCount)
            dialog.dismiss()
        }.create()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            dialogGameModeBinding.spinnerPlayMode -> {
                gameMode = dialogGameModeBinding.spinnerPlayMode.selectedItem.toString()
            }
            dialogGameModeBinding.spinnerPlayerCount -> {
                playerCount = dialogGameModeBinding.spinnerPlayerCount.selectedItem.toString().toInt()
            }
        }
    }
}