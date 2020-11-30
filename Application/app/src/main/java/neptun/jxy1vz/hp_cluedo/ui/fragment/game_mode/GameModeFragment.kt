package neptun.jxy1vz.hp_cluedo.ui.fragment.game_mode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.FragmentGameModeBinding
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
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
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                val single = getAssetByTag("resources/menu/other/singleplayer.png")!!.url
                val multi = getAssetByTag("resources/menu/other/multiplayer.png")!!.url
                val count3 = getAssetByTag("resources/menu/other/count3.png")!!.url
                val count4 = getAssetByTag("resources/menu/other/count4.png")!!.url
                val count5 = getAssetByTag("resources/menu/other/count5.png")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(single, context!!, fragmentGameModeBinding.ivSinglePlayer)
                    loadUrlImageIntoImageView(multi, context!!, fragmentGameModeBinding.ivMultiPlayer)
                    loadUrlImageIntoImageView(count3, context!!, fragmentGameModeBinding.ivPlayerCount3)
                    loadUrlImageIntoImageView(count4, context!!, fragmentGameModeBinding.ivPlayerCount4)
                    loadUrlImageIntoImageView(count5, context!!, fragmentGameModeBinding.ivPlayerCount5)
                    fragmentGameModeBinding.gameModeViewModel = GameModeViewModel(fragmentGameModeBinding, context!!, this@GameModeFragment)
                }
            }
        }
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