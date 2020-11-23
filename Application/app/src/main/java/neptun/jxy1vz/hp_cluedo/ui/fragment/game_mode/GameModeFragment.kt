package neptun.jxy1vz.hp_cluedo.ui.fragment.game_mode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentGameModeBinding
import neptun.jxy1vz.hp_cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.hp_cluedo.ui.fragment.channel.root.ChannelRootFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.character_selector.single.CharacterSelectorFragment

class GameModeFragment : Fragment(), ViewModelListener,
    MenuListener {

    private lateinit var listener: MenuListener

    fun setListener(l: MenuListener) {
        listener = l
    }

    private lateinit var fragmentGameModeBinding: FragmentGameModeBinding

    companion object {
        var isCanceled = false

        fun newInstance(listener: MenuListener): GameModeFragment {
            val fragment = GameModeFragment()
            fragment.setListener(listener)
            return fragment
        }
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
                    CharacterSelectorFragment.newInstance(this)

                }
                else -> {
                    ChannelRootFragment.newInstance(this)
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