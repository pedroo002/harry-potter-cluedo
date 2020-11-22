package neptun.jxy1vz.cluedo.ui.fragment.rules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentRulesBinding
import neptun.jxy1vz.cluedo.ui.fragment.rules.adapter.RulesPagerAdapter
import neptun.jxy1vz.cluedo.ui.fragment.rules.page.*

class RulesFragment : Fragment() {

    private lateinit var fragmentRulesBinding: FragmentRulesBinding
    private lateinit var dotList: ArrayList<ImageView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRulesBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_rules, container, false)
        fragmentRulesBinding.rulesViewModel = RulesViewModel()

        dotList = ArrayList()
        dotList.add(fragmentRulesBinding.ivDot1)
        dotList.add(fragmentRulesBinding.ivDot2)
        dotList.add(fragmentRulesBinding.ivDot3)
        dotList.add(fragmentRulesBinding.ivDot4)
        dotList.add(fragmentRulesBinding.ivDot5)
        dotList.add(fragmentRulesBinding.ivDot6)
        dotList.add(fragmentRulesBinding.ivDot7)
        dotList.add(fragmentRulesBinding.ivDot8)

        return fragmentRulesBinding.root
    }

    override fun onResume() {
        super.onResume()

        val fragmentList = listOf(
            GameTurnStartRulesFragment(),
            NoteRulesFragment(),
            DiceRulesFragment(),
            StepRulesFragment(),
            DarkCardRulesFragment(),
            IncriminationRulesFragment(),
            DumbledoresOfficeRulesFragment(),
            AccusationRulesFragment()
        )

        val adapter = RulesPagerAdapter(this)
        adapter.addFragments(fragmentList)
        fragmentRulesBinding.rulesPager.adapter = adapter

        fragmentRulesBinding.rulesPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                for (i in 0..dotList.lastIndex) {
                    if (i == position)
                        dotList[i].setImageResource(R.drawable.selected)
                    else
                        dotList[i].setImageResource(R.drawable.unselected)
                }
            }
        })
    }
}