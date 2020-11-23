package neptun.jxy1vz.hp_cluedo.ui.fragment.cards.dark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentDarkCardBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.domain.model.card.DarkCard
import neptun.jxy1vz.hp_cluedo.domain.model.card.LossType
import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class DarkCardFragment: Fragment(),
    ViewModelListener {

    private var playerId: Int = 0
    private lateinit var card: DarkCard
    private lateinit var playerList: List<Player>
    private lateinit var affectedPlayerIds: List<Int>
    private lateinit var listener: DialogDismiss

    fun setArgs(id: Int, card: DarkCard, list: List<Player>, ids: List<Int>, l: DialogDismiss) {
        playerId = id
        this.card = card
        playerList = list
        affectedPlayerIds = ids
        listener = l
    }

    companion object {
        fun newInstance(playerId: Int, card: DarkCard, playerList: List<Player>, affectedPlayerIds: List<Int>, listener: DialogDismiss): DarkCardFragment {
            val fragment = DarkCardFragment()
            fragment.setArgs(playerId, card, playerList, affectedPlayerIds, listener)
            return fragment
        }
    }

    private lateinit var fragmentDarkCardBinding: FragmentDarkCardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDarkCardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dark_card, container, false)
        fragmentDarkCardBinding.darkCardViewModel = DarkCardViewModel(
            fragmentDarkCardBinding,
            context!!,
            playerList,
            affectedPlayerIds,
            card,
            this,
            activity!!.supportFragmentManager
        )
        return fragmentDarkCardBinding.root
    }

    override fun onFinish() {
        val passedCard: DarkCard? = if (affectedPlayerIds.contains(playerId) && card.lossType != LossType.HP) card else null
        listener.onDarkCardDismiss(passedCard)
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}