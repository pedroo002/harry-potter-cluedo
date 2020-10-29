package neptun.jxy1vz.cluedo.ui.fragment.player_dies

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_player_dies.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.database.CluedoDatabase
import neptun.jxy1vz.cluedo.databinding.FragmentPlayerDiesBinding
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.helper.bwPlayers
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class PlayerDiesOrLeavesViewModel(
    private val bind: FragmentPlayerDiesBinding,
    context: Context,
    player: Player,
    dead: Boolean,
    lifecycleScope: LifecycleCoroutineScope,
    private val listener: ViewModelListener
) : BaseObservable() {

    private lateinit var title: String

    init {
        lifecycleScope.launch(Dispatchers.IO) {
            val title = if (dead)
                "${player.card.name} ${context.resources.getString(R.string.he_lost_his_hps)}"
            else {
                val playerName = CluedoDatabase.getInstance(context).playerDao().getPlayers()!!
                    .find { p -> p.characterName == player.card.name }!!.playerName
                "$playerName ${context.resources.getString(R.string.left_the_game)}"
            }
            setTitle(title)
        }

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

    private fun setTitle(text: String) {
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