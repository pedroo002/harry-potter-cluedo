package neptun.jxy1vz.cluedo.ui.dialog.card_dialog.reveal_mystery_card

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogCardRevealBinding

class CardRevealViewModel(private val bind: DialogCardRevealBinding, context: Context, private val cardResource: Int, private val playerName: String) : BaseObservable() {

    private val name = playerName

    fun getDialogTitle(): String {
        return "$name ezt a kártyát mutatja neked:"
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