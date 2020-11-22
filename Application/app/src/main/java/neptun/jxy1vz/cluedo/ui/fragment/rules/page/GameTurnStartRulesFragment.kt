package neptun.jxy1vz.cluedo.ui.fragment.rules.page

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R

class GameTurnStartRulesFragment: Fragment() {

    private lateinit var finger1: ImageView
    private lateinit var tap1: ImageView

    private lateinit var finger2: ImageView
    private lateinit var tap2: ImageView

    private lateinit var image: ImageView

    private var canAnimate = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_game_turn_start_rules, container, false)

        finger1 = root.findViewById(R.id.ivTapFinger1)
        tap1 = root.findViewById(R.id.ivTapAction1)

        finger2 = root.findViewById(R.id.ivTapFinger2)
        tap2 = root.findViewById(R.id.ivTapAction2)

        image = root.findViewById(R.id.ivIllustration1Anim)

        return root
    }

    override fun onResume() {
        super.onResume()
        canAnimate = true
        startFirstAnimation()
        startSecondAnimation()
    }

    override fun onPause() {
        canAnimate = false
        finger1.visibility = ImageView.GONE
        tap1.visibility = ImageView.GONE
        finger2.visibility = ImageView.GONE
        tap2.visibility = ImageView.GONE
        image.visibility = ImageView.GONE
        super.onPause()
    }

    private fun startFirstAnimation() {
        if (!canAnimate)
            return
        finger1.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(finger1)
            duration = 1500
            start()
            doOnEnd {
                animateFirstFingerTap()
            }
        }
    }

    private fun animateFirstFingerTap() {
        if (!canAnimate)
            return
        tap1.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(tap1)
            duration = 500
            start()
            doOnEnd {
                finger1.visibility = ImageView.GONE
                tap1.visibility = ImageView.GONE
                image.visibility = ImageView.VISIBLE
                (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
                    setTarget(image)
                    duration = 1000
                    start()
                    doOnEnd {
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(1000)
                            image.visibility = ImageView.GONE
                            startFirstAnimation()
                        }
                    }
                }
            }
        }
    }

    private fun startSecondAnimation() {
        if (!canAnimate)
            return
        finger2.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(finger2)
            start()
            doOnEnd {
                animateLeftSide()
            }
        }
    }

    private fun animateLeftSide() {
        if (!canAnimate)
            return
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(tap2)
            duration = 500
            start()
            tap2.visibility = ImageView.VISIBLE
            doOnEnd {
                tap2.visibility = ImageView.GONE
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(500)
                    changeSides()
                    animateRightSide()
                }
            }
        }
    }

    private fun animateRightSide() {
        if (!canAnimate)
            return
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(tap2)
            duration = 500
            start()
            tap2.visibility = ImageView.VISIBLE
            doOnEnd {
                tap2.visibility = ImageView.GONE
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(500)
                    changeSides()
                    animateLeftSide()
                }
            }
        }
    }

    private fun changeSides() {
        val layoutParams = finger2.layoutParams as ConstraintLayout.LayoutParams
        val startToStart = layoutParams.startToStart
        val endToEnd = layoutParams.endToEnd
        layoutParams.startToStart = endToEnd
        layoutParams.endToEnd = startToStart
        finger2.layoutParams = layoutParams
        tap2.layoutParams = layoutParams
    }
}