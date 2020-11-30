package neptun.jxy1vz.hp_cluedo.ui.fragment.cards.mystery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentUnusedMysteryCardsBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.domain.model.card.MysteryCard
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class UnusedMysteryCardsFragment: Fragment(),
    ViewModelListener {

    private lateinit var listener: DialogDismiss
    private lateinit var cardList: List<MysteryCard>

    fun setArgs(l: DialogDismiss, list: List<MysteryCard>) {
        listener = l
        cardList = list
    }

    companion object {
        fun newInstance(listener: DialogDismiss, cardList: List<MysteryCard>): UnusedMysteryCardsFragment {
            val fragment = UnusedMysteryCardsFragment()
            fragment.setArgs(listener, cardList)
            return fragment
        }
    }

    private lateinit var fragmentUnusedMysteryCardsBinding: FragmentUnusedMysteryCardsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUnusedMysteryCardsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_unused_mystery_cards,
            container,
            false
        )
        fragmentUnusedMysteryCardsBinding.unusedCardsViewModel =
            UnusedMysteryCardsViewModel(
                fragmentUnusedMysteryCardsBinding,
                context!!,
                cardList,
                this
            )

        return fragmentUnusedMysteryCardsBinding.root
    }

    override fun onFinish() {
        listener.onCardRevealDismiss()
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}