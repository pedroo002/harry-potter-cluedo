package neptun.jxy1vz.cluedo.ui.fragment.cards.mystery.reveal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentRevealMysteryCardBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class RevealMysteryCardFragment(private val card: MysteryCard, private val playerName: String, private val listener: DialogDismiss) : Fragment(),
    ViewModelListener {

    lateinit var fragmentRevealMysteryCardBinding: FragmentRevealMysteryCardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRevealMysteryCardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reveal_mystery_card, container, false)
        fragmentRevealMysteryCardBinding.cardRevealViewModel = RevealMysteryCardViewModel(fragmentRevealMysteryCardBinding, context!!, card.imageRes, playerName, this)
        return fragmentRevealMysteryCardBinding.root
    }

    override fun onFinish() {
        listener.onCardRevealDismiss()
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}