package neptun.jxy1vz.cluedo.ui.dialog.game_mode

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogGameModeBinding
import neptun.jxy1vz.cluedo.ui.dialog.character_selector.CharacterSelectorDialog

class GameModeViewModel(private val context: Context, private val fm: FragmentManager, private val bind: DialogGameModeBinding) : BaseObservable(),
    AdapterView.OnItemSelectedListener {

    private lateinit var gameMode: String
    private var playerCount: Int = 0

    init {
        bind.spinnerPlayMode.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, context.resources.getStringArray(
            R.array.playmodes))

        val playerCounts = listOf("3", "4", "5").toTypedArray()
        bind.spinnerPlayerCount.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, playerCounts)

        bind.spinnerPlayMode.onItemSelectedListener = this
        bind.spinnerPlayerCount.onItemSelectedListener = this
    }

    private fun openCharacterSelector() {
        CharacterSelectorDialog().show(fm, "DIALOG_CHARACTER_SELECTOR")
    }

    fun setGameMode() {
        val pref = context.getSharedPreferences(context.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(context.resources.getString(R.string.game_mode), gameMode)
        editor.putInt(context.resources.getString(R.string.player_count_key), playerCount)
        editor.apply()

        openCharacterSelector()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            bind.spinnerPlayMode -> {
                gameMode = bind.spinnerPlayMode.selectedItem.toString()
            }
            bind.spinnerPlayerCount -> {
                playerCount = bind.spinnerPlayerCount.selectedItem.toString().toInt()
            }
        }
    }
}