package neptun.jxy1vz.cluedo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.ui.activity.adapter.PagerAdapter
import fr.castorflex.android.verticalviewpager.VerticalViewPager

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: VerticalViewPager
    private lateinit var pagerAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
    }
}
