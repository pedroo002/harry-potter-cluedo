package neptun.jxy1vz.hp_cluedo.ui.fragment.rules.page

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.Guideline
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView

class NoteRulesFragment: Fragment() {

    private lateinit var finger1: ImageView
    private lateinit var tap1: ImageView
    private lateinit var illustration1: ImageView

    private lateinit var finger2: ImageView
    private lateinit var tap2: ImageView
    private lateinit var illustration2: ImageView

    private lateinit var imageList1: List<String>
    private lateinit var imageList2: List<String>

    private lateinit var constraintIdListFinger1First: List<Int>
    private lateinit var constraintIdListFinger1Second: List<Int>
    private lateinit var constraintIdListFinger2First: List<Int>
    private lateinit var constraintIdListFinger2Second: List<Int>

    private var canAnimate = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_note_rules, container, false)

        finger1 = root.findViewById(R.id.ivTapFinger1)
        tap1 = root.findViewById(R.id.ivTapAction1)
        illustration1 = root.findViewById(R.id.ivIllustration1)

        finger2 = root.findViewById(R.id.ivTapFinger2)
        tap2 = root.findViewById(R.id.ivTapAction2)
        illustration2 = root.findViewById(R.id.ivIllustration2)

        constraintIdListFinger1First = listOf(
            root.findViewById<Guideline>(R.id.guidelineCell1Left).id,
            root.findViewById<Guideline>(R.id.guidelineCell1Right).id,
            root.findViewById<Guideline>(R.id.guidelineCell1Top).id,
            illustration1.id
        )

        constraintIdListFinger1Second = listOf(
            constraintIdListFinger1First[0],
            constraintIdListFinger1First[1],
            illustration1.id,
            illustration1.id
        )

        constraintIdListFinger2First = listOf(
            root.findViewById<Guideline>(R.id.guidelineCell2Left).id,
            root.findViewById<Guideline>(R.id.guidelineCell2Right).id,
            root.findViewById<Guideline>(R.id.guidelineCell2Top).id,
            illustration2.id
        )

        constraintIdListFinger2Second = listOf(
            constraintIdListFinger2First[1],
            illustration2.id,
            root.findViewById<Guideline>(R.id.guidelineTickTop).id,
            illustration2.id
        )

        return root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                imageList1 = listOf(
                    getAssetByTag("resources/menu/tutorial/notes1.png")!!.url,
                    getAssetByTag("resources/menu/tutorial/notes2.png")!!.url,
                    getAssetByTag("resources/menu/tutorial/notes3.png")!!.url
                )
                imageList2 = listOf(
                    getAssetByTag("resources/menu/tutorial/notes4.png")!!.url,
                    getAssetByTag("resources/menu/tutorial/notes5.png")!!.url,
                    getAssetByTag("resources/menu/tutorial/notes6.png")!!.url
                )
                val tap = getAssetByTag("resources/menu/tutorial/tap.png")!!.url
                val touchAction = getAssetByTag("resources/menu/tutorial/touch_action.png")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(tap, context!!, finger1)
                    loadUrlImageIntoImageView(tap, context!!, finger2)
                    loadUrlImageIntoImageView(touchAction, context!!, tap1)
                    loadUrlImageIntoImageView(touchAction, context!!, tap2)

                    loadUrlImageIntoImageView(imageList1[0], context!!, illustration1)
                    loadUrlImageIntoImageView(imageList2[0], context!!, illustration2)

                    canAnimate = true
                    startAnimation(tap1, finger1, illustration1, imageList1)
                    startAnimation(tap2, finger2, illustration2, imageList2)
                }
            }
        }
    }

    override fun onPause() {
        canAnimate = false
        finger1.visibility = ImageView.GONE
        tap1.visibility = ImageView.GONE
        finger2.visibility = ImageView.GONE
        tap2.visibility = ImageView.GONE
        super.onPause()
    }

    private fun startAnimation(tap: ImageView, finger: ImageView, illustration: ImageView, images: List<String>) {
        finger.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(finger)
            start()
            doOnEnd {
                animateLongTap(tap, finger, illustration, images)
            }
        }
    }

    private fun animateLongTap(tap: ImageView, finger: ImageView, illustration: ImageView, images: List<String>) {
        if (!canAnimate)
            return
        tap.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(tap)
            duration = 1500
            start()
            doOnEnd {
                tap.visibility = ImageView.GONE
                loadUrlImageIntoImageView(images[1], context!!, illustration)
                if (tap.id == R.id.ivTapAction1) {
                    setConstraints(finger, constraintIdListFinger1Second)
                    setConstraints(tap, constraintIdListFinger1Second)
                }
                else {
                    setConstraints(finger, constraintIdListFinger2Second)
                    setConstraints(tap, constraintIdListFinger2Second)

                    setWidthType(finger, tap, MATCH_CONSTRAINT)
                }
                animateShortTap(tap, finger, illustration, images)
            }
        }
    }

    private fun animateShortTap(tap: ImageView, finger: ImageView, illustration: ImageView, images: List<String>) {
        if (!canAnimate)
            return
        tap.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(context, R.animator.appear) as AnimatorSet).apply {
            setTarget(tap)
            duration = 500
            start()
            doOnEnd {
                lifecycleScope.launch(Dispatchers.Main) {
                    tap.visibility = ImageView.GONE
                    finger.visibility = ImageView.GONE
                    loadUrlImageIntoImageView(images[2], context!!, illustration)
                    if (tap.id == R.id.ivTapAction1) {
                        setConstraints(finger, constraintIdListFinger1First)
                        setConstraints(tap, constraintIdListFinger1First)
                    }
                    else {
                        setConstraints(finger, constraintIdListFinger2First)
                        setConstraints(tap, constraintIdListFinger2First)

                        setWidthType(finger, tap, WRAP_CONTENT)
                    }
                    delay(1500)
                    loadUrlImageIntoImageView(images[0], context!!, illustration)
                    finger.visibility = ImageView.VISIBLE
                    animateLongTap(tap, finger, illustration, images)
                }
            }
        }
    }

    private fun setConstraints(iv: ImageView, idList: List<Int>) {
        val layoutParams = iv.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.startToStart = idList[0]
        layoutParams.endToEnd = idList[1]
        layoutParams.topToTop = idList[2]
        layoutParams.bottomToBottom = idList[3]
        iv.layoutParams = layoutParams
    }

    private fun setWidthType(finger: ImageView, tap: ImageView, width: Int) {
        val layoutParams1 = ConstraintLayout.LayoutParams(width, MATCH_CONSTRAINT)
        val layoutParams2 = ConstraintLayout.LayoutParams(width, MATCH_CONSTRAINT)

        layoutParams1.startToStart = (finger.layoutParams as ConstraintLayout.LayoutParams).startToStart
        layoutParams1.endToEnd = (finger.layoutParams as ConstraintLayout.LayoutParams).endToEnd
        layoutParams1.topToTop = (finger.layoutParams as ConstraintLayout.LayoutParams).topToTop
        layoutParams1.bottomToBottom = (finger.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom

        layoutParams2.startToStart = (tap.layoutParams as ConstraintLayout.LayoutParams).startToStart
        layoutParams2.endToEnd = (tap.layoutParams as ConstraintLayout.LayoutParams).endToEnd
        layoutParams2.topToTop = (tap.layoutParams as ConstraintLayout.LayoutParams).topToTop
        layoutParams2.bottomToBottom = (tap.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom

        finger.layoutParams = layoutParams1
        tap.layoutParams = layoutParams2
    }
}