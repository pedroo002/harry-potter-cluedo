package neptun.jxy1vz.cluedo.ui.activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import neptun.jxy1vz.cluedo.ui.fragment.BottomMapFragment
import neptun.jxy1vz.cluedo.ui.fragment.MiddleMapFragment
import neptun.jxy1vz.cluedo.ui.fragment.TopMapFragment

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> TopMapFragment.getInstance()
            1 -> MiddleMapFragment.getInstance()
            else -> BottomMapFragment.getInstance()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}