package neptun.jxy1vz.cluedo.ui.dialog.card_dialog.reveal_mystery_card

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogCardRevealBinding

class CardRevealViewModel(private val bind: DialogCardRevealBinding, private val context: Context, private val cardResource: Int, private val name: String) : BaseObservable() {

    fun getDialogTitle(): String {
        return name + context.getString(R.string.someone_shows_you_this_card)
    }

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