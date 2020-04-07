package neptun.jxy1vz.cluedo.ui.dialog.game_mode

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import neptun.jxy1vz.cluedo.ui.dialog.character_selector.CharacterSelectorDialog

class GameModeViewModel(private val context: Context, private val fm: FragmentManager) : BaseObservable() {
    private fun openCharacterSelector() {
        CharacterSelectorDialog().show(fm, "DIALOG_CHARACTER_SELECTOR")
    }

    fun setGameMode(mode: String, playerCount: Int) {
        val pref = context.getSharedPreferences("Game params", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("game_mode", mode)
        editor.putInt("player_count", playerCount)
        editor.apply()

        openCharacterSelector()
    }
}