package neptun.jxy1vz.cluedo.ui.fragment.cards.mystery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentUnusedMysteryCardsBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class UnusedMysteryCardsFragment(
    private val listener: DialogDismiss,
    private val cardList: List<MysteryCard>
) : Fragment(),
    ViewModelListener {

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
                cardList,
                this
            )

        return fragmentUnusedMysteryCardsBinding.root
    }

    override fun onFinish() {
        listener.onCardRevealDismiss()
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}