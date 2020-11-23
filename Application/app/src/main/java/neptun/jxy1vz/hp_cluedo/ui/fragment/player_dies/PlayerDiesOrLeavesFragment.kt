package neptun.jxy1vz.hp_cluedo.ui.fragment.player_dies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentPlayerDiesBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.hp_cluedo.ui.fragment.card_pager.CardFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.card_pager.adapter.CardPagerAdapter

class PlayerDiesOrLeavesFragment : Fragment(), ViewModelListener {

    enum class ExitScenario {
        DEAD,
        LEAVE,
        WRONG_SOLUTION
    }

    private lateinit var player: Player
    private lateinit var scenario: ExitScenario
    private lateinit var listener: DialogDismiss
    private lateinit var title: String

    fun setArgs(p: Player, scenario: ExitScenario, l: DialogDismiss, t: String) {
        player = p
        this.scenario = scenario
        listener = l
        title = t
    }

    companion object {
        fun newInstance(player: Player, scenario: ExitScenario, listener: DialogDismiss, title: String) : PlayerDiesOrLeavesFragment {
            val fragment = PlayerDiesOrLeavesFragment()
            fragment.setArgs(player, scenario, listener, title)
            return fragment
        }
    }

    private lateinit var fragmentPlayerDiesBinding: FragmentPlayerDiesBinding
    private lateinit var adapter: CardPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPlayerDiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player_dies, container, false)
        fragmentPlayerDiesBinding.playerDiesOrViewModel = PlayerDiesOrLeavesViewModel(
            fragmentPlayerDiesBinding,
            player,
            title,
            lifecycleScope,
            this
        )
        return fragmentPlayerDiesBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentList = ArrayList<CardFragment>()
        player.mysteryCards.forEach { card ->
            fragmentList.add(
                CardFragment.newInstance(
                    card.imageRes
                )
            )
        }
        adapter = CardPagerAdapter(childFragmentManager, fragmentList)
    }

    override fun onResume() {
        super.onResume()
        fragmentPlayerDiesBinding.vpCards.adapter = adapter
    }

    override fun onFinish() {
        when (scenario) {
            ExitScenario.DEAD -> listener.onPlayerDiesDismiss(player)
            ExitScenario.LEAVE -> listener.onPlayerLeavesDismiss()
            ExitScenario.WRONG_SOLUTION -> listener.onPlayerLeavesDismiss(false)
        }
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}