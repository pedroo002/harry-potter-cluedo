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

    init {
        val bwRes = bwPlayers[player.id]
        val res = player.card.imageRes
        bind.playerDiesRoot.ivDeadPlayer.setImageResource(bwRes)
        bind.playerDiesRoot.ivPlayer.setImageResource(res)

        (AnimatorInflater.loadAnimator(bind.playerDiesRoot.context, R.animator.disappear) as AnimatorSet).apply {
            setTarget(bind.playerDiesRoot.ivPlayer)
            startDelay = 500
            start()
            doOnEnd {
                bind.playerDiesRoot.ivPlayer.visibility = ImageView.GONE
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