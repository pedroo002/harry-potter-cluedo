package neptun.jxy1vz.cluedo.ui.fragment.cards.mystery.reveal

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentRevealMysteryCardBinding
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class RevealMysteryCardViewModel(private val bind: FragmentRevealMysteryCardBinding, private val context: Context, private val cardResource: Int, private val name: String, private val listener: ViewModelListener) : BaseObservable() {

    fun getDialogTitle(): String {
        return name + context.getString(R.string.someone_shows_you_this_card)
    }

    init {
        val scale = context.resources.displayMetrics.density
        bind.ivMysteryCard.cameraDistance = 8000 * scale

        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(bind.ivMysteryCard)
            start()
            doOnEnd {
                bind.ivMysteryCard.setImageResource(cardResource)
            }
        }
    }

    fun close() {
        listener.onFinish()
    }

}