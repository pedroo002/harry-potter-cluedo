package neptun.jxy1vz.cluedo.ui.fragment.cards.mystery.unused

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.CardFragment
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.adapter.CardPagerAdapter

class UnusedMysteryCardsFragment(private val listener: DialogDismiss, private val cardList: List<MysteryCard>) : Fragment() {

    private lateinit var adapter: CardPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_unused_mystery_cards, container, false)

        val fragmentList = ArrayList<CardFragment>()
        for (card in cardList) {
            fragmentList.add(
                CardFragment(
                    card.imageRes
                )
            )
        }
        adapter = CardPagerAdapter(childFragmentManager, fragmentList)
        view.findViewById<ViewPager>(R.id.vpCards).adapter = adapter

        view.findViewById<Button>(R.id.btnOk).setOnClickListener{
            listener.onCardRevealDismiss()
            activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
        }

        return view
    }
}