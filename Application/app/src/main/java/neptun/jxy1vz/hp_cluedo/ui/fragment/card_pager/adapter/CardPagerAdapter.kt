package neptun.jxy1vz.hp_cluedo.ui.fragment.card_pager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import neptun.jxy1vz.hp_cluedo.ui.fragment.card_pager.CardFragment

class CardPagerAdapter(fm: FragmentManager, private val fragments: List<CardFragment>) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}