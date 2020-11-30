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
import neptun.jxy1vz.hp_cluedo.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.database.model.string
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView

class IncriminationRulesFragment: Fragment() {

    private lateinit var ivIllustration1: ImageView
    private lateinit var ivIllustration1_2: ImageView
    private lateinit var ivIllustration2: ImageView
    private lateinit var ivIllustration3: ImageView
    private lateinit var ivIllustration4: ImageView
    private lateinit var ivIllustration5: ImageView
    private var canAnimate = true

    private lateinit var incriminationTemplateList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_incrimination_rules, container, false)

        ivIllustration1 = root.findViewById(R.id.ivIllustration1)
        ivIllustration1_2 = root.findViewById(R.id.ivIllustration1_2)
        ivIllustration2 = root.findViewById(R.id.ivIllustration2)
        ivIllustration3 = root.findViewById(R.id.ivIllustration3)
        ivIllustration4 = root.findViewById(R.id.ivIllustration4)
        ivIllustration5 = root.findViewById(R.id.ivIllustration5)

        return root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                incriminationTemplateList = getAssetsByPrefix(AssetPrefixes.TUTORIAL.string())!!.map { assetDBmodel -> assetDBmodel.url }.filter { url -> url.contains("incrimination") }
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(incriminationTemplateList[1], context!!, ivIllustration1)
                    loadUrlImageIntoImageView(incriminationTemplateList[2], context!!, ivIllustration1_2)
                    loadUrlImageIntoImageView(incriminationTemplateList[3], context!!, ivIllustration2)
                    loadUrlImageIntoImageView(incriminationTemplateList[0], context!!, ivIllustration3)
                    loadUrlImageIntoImageView(incriminationTemplateList[4], context!!, ivIllustration4)
                    loadUrlImageIntoImageView(incriminationTemplateList[5], context!!, ivIllustration5)
                }
            }
        }
        canAnimate = true
        startAnimation()
    }

    override fun onPause() {
        canAnimate = false
        super.onPause()
    }

    private fun startAnimation() {
        if (!canAnimate)
            return
        ivIllustration1_2.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(ivIllustration1_2)
            start()
            doOnEnd {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(1000)
                    ivIllustration1_2.visibility = ImageView.GONE
                    startAnimation()
                }
            }
        }
    }
}