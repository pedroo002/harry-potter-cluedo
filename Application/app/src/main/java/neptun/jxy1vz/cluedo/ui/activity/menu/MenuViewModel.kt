package neptun.jxy1vz.cluedo.ui.activity.menu

import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import neptun.jxy1vz.cluedo.ui.dialog.game_mode.GameModeDialog

class MenuViewModel(private val fragmentManager: FragmentManager, private val listener: MenuListener) : BaseObservable() {

    interface MenuListener {
        fun exitGame()
    }

    fun openGameModeDialog() {
        GameModeDialog().show(fragmentManager, "DIALOG_GAME_MODE")
    }

    fun exit() {
        listener.exitGame()
    }
}