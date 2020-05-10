package neptun.jxy1vz.cluedo.ui.fragment.cards.helper

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentHelperCardBinding
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class HelperCardViewModel(bind: FragmentHelperCardBinding, context: Context, cardResource: Int, private val listener: ViewModelListener) : BaseObservable() {

    init {
        val scale = context.resources.displayMetrics.density
        bind.ivHelperCard.cameraDistance = 8000 * scale

        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(bind.ivHelperCard)
            start()
            doOnEnd {
                bind.ivHelperCard.setImageResource(cardResource)
            }
        }
    }

    fun close() {
        listener.onFinish()
    }
}