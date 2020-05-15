package neptun.jxy1vz.cluedo.ui.fragment.player_dies

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_player_dies.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentPlayerDiesBinding
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.helper.bwPlayers
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class PlayerDiesViewModel(
    private val bind: FragmentPlayerDiesBinding,
    private val player: Player,
    private val listener: ViewModelListener
) : BaseObservable() {

    private lateinit var title: String

    fun loadPage() {
        val bwRes = bwPlayers[player.id]
        val res = player.card.imageRes

        bind.playerDiesRoot.ivDeadPlayer.setImageResource(bwRes)

        val coloredImage = ImageView(bind.playerDiesRoot.context)
        coloredImage.layoutParams = bind.playerDiesRoot.ivDeadPlayer.layoutParams
        coloredImage.setImageResource(res)
        bind.playerDiesRoot.addView(coloredImage)
        coloredImage.bringToFront()

        (AnimatorInflater.loadAnimator(bind.playerDiesRoot.context, R.animator.disappear) as AnimatorSet).apply {
            setTarget(coloredImage)
            startDelay = 500
            start()
            doOnEnd {
                coloredImage.visibility = ImageView.GONE
                bind.playerDiesRoot.removeView(coloredImage)
                bind.playerDiesRoot.ivDeadPlayer.bringToFront()
            }
        }
    }

    fun setTitle(text: String) {
        title = text
        notifyChange()
    }

    fun getTitle(): String {
        return title
    }

    fun close() {
        listener.onFinish()
    }
}