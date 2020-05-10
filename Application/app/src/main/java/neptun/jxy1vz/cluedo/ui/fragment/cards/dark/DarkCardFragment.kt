package neptun.jxy1vz.cluedo.ui.fragment.cards.dark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentDarkCardBinding
import neptun.jxy1vz.cluedo.domain.model.DarkCard
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class DarkCardFragment(private val player: Player, private val darkCard: DarkCard, private val listener: DarkCardListener): Fragment(),
    ViewModelListener {

    interface DarkCardListener {
        fun getLoss(playerId: Int, card: DarkCard?)
    }

    private lateinit var fragmentDarkCardBinding: FragmentDarkCardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDarkCardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dark_card, container, false)
        fragmentDarkCardBinding.darkCardViewModel = DarkCardViewModel(fragmentDarkCardBinding, context!!, player, darkCard, this)
        return fragmentDarkCardBinding.root
    }

    override fun onFinish() {
        listener.getLoss(player.id, fragmentDarkCardBinding.darkCardViewModel!!.getLoss())
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}