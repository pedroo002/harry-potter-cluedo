package neptun.jxy1vz.cluedo.ui.fragment.cards.dark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentDarkCardBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.card.DarkCard
import neptun.jxy1vz.cluedo.domain.model.card.LossType
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class DarkCardFragment(
    private val playerId: Int,
    private val card: DarkCard,
    private val playerList: List<Player>,
    private val affectedPlayerIds: List<Int>,
    private val listener: DialogDismiss
): Fragment(),
    ViewModelListener {

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