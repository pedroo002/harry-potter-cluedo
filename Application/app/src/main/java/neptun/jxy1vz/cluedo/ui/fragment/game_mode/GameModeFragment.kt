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
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.channel.root.ChannelRootFragment
import neptun.jxy1vz.cluedo.ui.fragment.character_selector.single.CharacterSelectorFragment

class GameModeFragment(private val listener: MenuListener) : Fragment(), ViewModelListener,
    MenuListener {

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
        if (!isCanceled) {
            val fragment: Fragment = when (fragmentGameModeBinding.gameMode) {
                resources.getStringArray(R.array.playmodes)[0] -> {
                    CharacterSelectorFragment(this)

                }
                else -> {
                    ChannelRootFragment(this)
                }
            }
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.menuFrame, fragment).addToBackStack("FRAGMENT-${fragmentGameModeBinding.gameMode}").commit()
        }
        else
            onFragmentClose()
    }

    override fun onFragmentClose() {
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
        listener.onFragmentClose()
    }
}