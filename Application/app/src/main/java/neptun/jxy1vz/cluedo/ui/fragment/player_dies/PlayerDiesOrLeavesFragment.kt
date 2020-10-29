package neptun.jxy1vz.cluedo.ui.fragment.player_dies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.database.CluedoDatabase
import neptun.jxy1vz.cluedo.databinding.FragmentPlayerDiesBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.CardFragment
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.adapter.CardPagerAdapter

class PlayerDiesOrLeavesFragment(private val player: Player, private val dead: Boolean, private val listener: DialogDismiss) : Fragment(), ViewModelListener {

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
            context!!,
            player,
            dead,
            lifecycleScope,
            this
        )
        return fragmentPlayerDiesBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentList = ArrayList<CardFragment>()
        for (card in player.mysteryCards) {
            fragmentList.add(
                CardFragment(
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
        if (dead)
            listener.onPlayerDiesDismiss(player)
        else
            listener.onPlayerLeavesDismiss()
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}