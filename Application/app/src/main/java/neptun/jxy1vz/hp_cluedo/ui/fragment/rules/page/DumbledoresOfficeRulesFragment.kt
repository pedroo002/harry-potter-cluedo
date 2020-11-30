package neptun.jxy1vz.hp_cluedo.ui.fragment.rules.page

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView

class DumbledoresOfficeRulesFragment: Fragment() {

    private lateinit var ivIllustration: ImageView
    private lateinit var ivIllustrationAnim: ImageView
    private lateinit var ivIllustration2: ImageView
    private var canAnimate = true

    private var currentIndex = 1
    private lateinit var imageList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dumbledores_office_rules, container, false)

        ivIllustration = root.findViewById(R.id.ivIllustration)
        ivIllustrationAnim = root.findViewById(R.id.ivIllustrationAnim)
        ivIllustration2 = root.findViewById(R.id.ivIllustration2)

        return root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                imageList = listOf(
                    getAssetByTag("resources/menu/tutorial/dumbledore1.png")!!.url,
                    getAssetByTag("resources/menu/tutorial/dumbledore2.png")!!.url,
                    getAssetByTag("resources/menu/tutorial/dumbledore3.png")!!.url,
                    getAssetByTag("resources/menu/tutorial/dumbledore4.png")!!.url
                )
                val unusedCardsTemplate = getAssetByTag("resources/menu/tutorial/unused_cards_template.png")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(unusedCardsTemplate, context!!, ivIllustration2)
                    canAnimate = true
                    startAnimation()
                }
            }
        }
    }

    override fun onPause() {
        canAnimate = false
        super.onPause()
    }

    private fun startAnimation() {
        if (!canAnimate)
            return
        ivIllustrationAnim.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(ivIllustrationAnim)
            duration = 1000
            start()
            doOnEnd {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(500)
                    ivIllustrationAnim.visibility = ImageView.GONE
                    loadUrlImageIntoImageView(imageList[currentIndex], context!!, ivIllustration)
                    currentIndex = if (currentIndex + 1 == imageList.size) 0 else currentIndex + 1
                    loadUrlImageIntoImageView(imageList[currentIndex], context!!, ivIllustrationAnim)
                    startAnimation()
                }
            }
        }
    }
}