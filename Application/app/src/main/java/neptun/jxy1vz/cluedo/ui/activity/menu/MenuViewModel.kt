package neptun.jxy1vz.cluedo.ui.activity.menu

import android.view.View
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMenuBinding
import neptun.jxy1vz.cluedo.ui.fragment.game_mode.GameModeFragment

class MenuViewModel(private val bind: ActivityMenuBinding, private val fragmentManager: FragmentManager, private val listener: MenuListener) : BaseObservable(),
    MenuListener {

    interface MenuListener {
        fun exitGame()
    }

    fun openGameModeDialog() {
        val gameModeFragment = GameModeFragment(this)
        fragmentManager.beginTransaction().replace(R.id.menuFrame, gameModeFragment).commit()
        bind.menuFrame.bringToFront()

        bind.btnStart.visibility = View.GONE
        bind.btnRules.visibility = View.GONE
        bind.btnExit.visibility = View.GONE
    }

    fun exit() {
        listener.exitGame()
    }

    override fun onFragmentClose() {
        bind.btnStart.visibility = View.VISIBLE
        bind.btnRules.visibility = View.VISIBLE
        bind.btnExit.visibility = View.VISIBLE
    }
}