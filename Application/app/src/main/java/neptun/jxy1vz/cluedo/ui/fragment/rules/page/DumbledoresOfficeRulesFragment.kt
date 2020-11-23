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

class DumbledoresOfficeRulesFragment: Fragment() {

    private lateinit var baseImage: ImageView
    private lateinit var image: ImageView
    private var canAnimate = true

    private var currentIndex = 1
    private val imageList = listOf(R.drawable.dumbledore1, R.drawable.dumbledore2, R.drawable.dumbledore3, R.drawable.dumbledore4)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dumbledores_office_rules, container, false)

        baseImage = root.findViewById(R.id.ivIllustration)
        image = root.findViewById(R.id.ivIllustrationAnim)

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
            duration = 1000
            start()
            doOnEnd {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(500)
                    image.visibility = ImageView.GONE
                    baseImage.setImageResource(imageList[currentIndex])
                    currentIndex = if (currentIndex + 1 == imageList.size) 0 else currentIndex + 1
                    image.setImageResource(imageList[currentIndex])
                    startAnimation()
                }
            }
        }
    }
}