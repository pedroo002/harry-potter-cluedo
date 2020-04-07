package neptun.jxy1vz.cluedo.ui.menu

import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import neptun.jxy1vz.cluedo.ui.dialog.game_mode.GameModeDialog

class MenuViewModel(private val fragmentManager: FragmentManager) : BaseObservable() {
    fun openGameModeDialog() {
        GameModeDialog().show(fragmentManager, "DIALOG_GAME_MODE")
    }
}