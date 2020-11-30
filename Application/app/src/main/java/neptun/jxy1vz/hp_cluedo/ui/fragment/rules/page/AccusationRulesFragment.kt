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

class AccusationRulesFragment: Fragment() {

    private lateinit var ivIllustration1: ImageView
    private lateinit var ivIllustration1Anim: ImageView
    private lateinit var ivIllustration2: ImageView
    private lateinit var ivIllustration3: ImageView
    private var canAnimate = true

    private lateinit var accusation1: String
    private lateinit var accusation2: String
    private lateinit var accusation3: String
    private lateinit var accusation4: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_accusation_rules, container, false)

        ivIllustration1 = root.findViewById(R.id.ivIllustration1)
        ivIllustration1Anim = root.findViewById(R.id.ivIllustration1Anim)
        ivIllustration2 = root.findViewById(R.id.ivIllustration2)
        ivIllustration3 = root.findViewById(R.id.ivIllustration3)

        return root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                accusation1 = getAssetByTag("resources/menu/tutorial/accusation1.png")!!.url
                accusation2 = getAssetByTag("resources/menu/tutorial/accusation2.png")!!.url
                accusation3 = getAssetByTag("resources/menu/tutorial/accusation3.png")!!.url
                accusation4 = getAssetByTag("resources/menu/tutorial/accusation4.png")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(accusation1, context!!, ivIllustration1)
                    loadUrlImageIntoImageView(accusation2, context!!, ivIllustration1Anim)
                    loadUrlImageIntoImageView(accusation3, context!!, ivIllustration2)
                    loadUrlImageIntoImageView(accusation4, context!!, ivIllustration3)

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
        ivIllustration1Anim.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(ivIllustration1Anim)
            duration = 2000
            start()
            doOnEnd {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(1000)
                    ivIllustration1Anim.visibility = ImageView.GONE
                    startAnimation()
                }
            }
        }
    }
}