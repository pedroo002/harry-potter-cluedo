package neptun.jxy1vz.hp_cluedo.ui.fragment.rules.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RulesPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentList = ArrayList<Fragment>()

    fun addFragments(list: List<Fragment>) {
        fragmentList.addAll(list)
    }

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}