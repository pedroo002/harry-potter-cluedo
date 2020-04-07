package neptun.jxy1vz.cluedo.ui.dialog.card_dialog.helper

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogHelperCardBinding

class HelperCardViewModel(private val bind: DialogHelperCardBinding, context: Context, private val cardResource: Int) : BaseObservable() {

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
}