package neptun.jxy1vz.cluedo.ui.fragment.cards.card_loss

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCardLossBinding
import neptun.jxy1vz.cluedo.domain.model.HelperCard
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.CardFragment
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.adapter.CardPagerAdapter

class CardLossFragment(private val title: String, private val cardList: List<HelperCard>, private val listener: ThrowCardListener) : Fragment(),
    ViewModelListener, ViewPager.OnPageChangeListener {

    interface ThrowCardListener {
        fun onThrow()
    }

    private lateinit var fragmentCardLossBinding: FragmentCardLossBinding
    private lateinit var fragmentList: MutableList<CardFragment>
    private lateinit var adapter: CardPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCardLossBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_card_loss, container, false)
        fragmentCardLossBinding.cardLossViewModel = CardLossViewModel(title, this)
        return fragmentCardLossBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentList = ArrayList()
        for (card in cardList) {
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
        fragmentCardLossBinding.vpCards.adapter = adapter
        fragmentCardLossBinding.cardLossViewModel!!.selectedCard = cardList[0]
        fragmentCardLossBinding.vpCards.addOnPageChangeListener(this)
    }

    override fun onFinish() {
        listener.onThrow()
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        fragmentCardLossBinding.cardLossViewModel!!.selectedCard = cardList[position]
    }
}