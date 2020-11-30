package neptun.jxy1vz.hp_cluedo.ui.fragment.rules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.fragment.rules.adapter.RulesPagerAdapter
import neptun.jxy1vz.hp_cluedo.ui.fragment.rules.page.*

class RulesFragment : Fragment() {

    private lateinit var dotList: ArrayList<ImageView>
    private lateinit var pager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rules, container, false)
        pager = root.findViewById(R.id.rulesPager)

        dotList = ArrayList()
        dotList.add(root.findViewById(R.id.ivDot1))
        dotList.add(root.findViewById(R.id.ivDot2))
        dotList.add(root.findViewById(R.id.ivDot3))
        dotList.add(root.findViewById(R.id.ivDot4))
        dotList.add(root.findViewById(R.id.ivDot5))
        dotList.add(root.findViewById(R.id.ivDot6))

        return root
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                val selected = getAssetByTag("resources/menu/tutorial/selected.png")!!.url
                val unselected = getAssetByTag("resources/menu/tutorial/unselected.png")!!.url
                withContext(Dispatchers.Main) {
                    val fragmentList = listOf(
                        GameTurnStartRulesFragment(),
                        NoteRulesFragment(),
                        DiceRulesFragment(),
                        IncriminationRulesFragment(),
                        DumbledoresOfficeRulesFragment(),
                        AccusationRulesFragment()
                    )

                    val adapter = RulesPagerAdapter(this@RulesFragment)
                    adapter.addFragments(fragmentList)
                    pager.adapter = adapter

                    pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                            for (i in 0..dotList.lastIndex) {
                                if (i == position)
                                    loadUrlImageIntoImageView(selected, context!!, dotList[i])
                                else
                                    loadUrlImageIntoImageView(unselected, context!!, dotList[i])
                            }
                        }
                    })
                }
            }
        }
    }
}