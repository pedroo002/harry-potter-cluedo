package neptun.jxy1vz.hp_cluedo.ui.activity.menu

import android.view.View
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.ActivityMenuBinding
import neptun.jxy1vz.hp_cluedo.ui.fragment.game_mode.GameModeFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.rules.RulesFragment

class MenuViewModel(private val bind: ActivityMenuBinding, private val fragmentManager: FragmentManager, private val listener: MenuListener) : BaseObservable(),
    MenuListener {

    interface MenuListener {
        fun exitGame()
    }

    fun openGameModeDialog() {
        val gameModeFragment = GameModeFragment.newInstance(this)
        fragmentManager.beginTransaction().add(R.id.menuFrame, gameModeFragment).addToBackStack(gameModeFragment.toString()).commit()
        bind.menuFrame.bringToFront()

        bind.btnStart.visibility = View.GONE
        bind.btnRules.visibility = View.GONE
        bind.btnExit.visibility = View.GONE
    }

    fun openRules() {
        val rulesFragment = RulesFragment()
        fragmentManager.beginTransaction().add(R.id.menuFrame, rulesFragment).addToBackStack(rulesFragment.toString()).commit()
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