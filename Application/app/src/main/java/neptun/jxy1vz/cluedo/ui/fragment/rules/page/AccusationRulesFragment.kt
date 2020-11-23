package neptun.jxy1vz.cluedo.ui.fragment.rules.page

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
import neptun.jxy1vz.cluedo.R

class AccusationRulesFragment: Fragment() {

    private lateinit var image: ImageView
    private var canAnimate = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_accusation_rules, container, false)

        image = root.findViewById(R.id.ivIllustration1Anim)

        return root
    }

    override fun onResume() {
        super.onResume()
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
        image.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(image)
            duration = 2000
            start()
            doOnEnd {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(1000)
                    image.visibility = ImageView.GONE
                    startAnimation()
                }
            }
        }
    }
}