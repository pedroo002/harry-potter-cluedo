package neptun.jxy1vz.cluedo.ui.fragment.game_mode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentGameModeBinding
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.dialog.character_selector.CharacterSelectorDialog
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class GameModeFragment(private val listener: MenuListener) : Fragment(), ViewModelListener {

    private lateinit var fragmentGameModeBinding: FragmentGameModeBinding

    companion object {
        var isCanceled = false
    }

    init {
        isCanceled = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentGameModeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_mode, container, false)
        fragmentGameModeBinding.gameModeViewModel = GameModeViewModel(fragmentGameModeBinding, context!!, this)
        return fragmentGameModeBinding.root
    }

    override fun onFinish() {
        if (!isCanceled)
            CharacterSelectorDialog().show(activity!!.supportFragmentManager, "DIALOG_CHARACTER_SELECTOR")
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
        listener.onFragmentClose()
    }
}