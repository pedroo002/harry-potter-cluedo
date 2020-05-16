package neptun.jxy1vz.cluedo.ui.fragment.user_dies

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_player_dies.view.ivDeadPlayer
import kotlinx.android.synthetic.main.fragment_player_dies.view.ivPlayer
import kotlinx.android.synthetic.main.fragment_user_dies.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentUserDiesBinding
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.helper.bwPlayers
import neptun.jxy1vz.cluedo.domain.model.helper.roomTokens
import neptun.jxy1vz.cluedo.domain.model.helper.suspectTokens
import neptun.jxy1vz.cluedo.domain.model.helper.toolTokens
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.map.MapViewModel

class UserDiesViewModel(private val bind: FragmentUserDiesBinding, private val context: Context, private val player: Player, private val listener: ViewModelListener) : BaseObservable() {

    init {
        val bwRes = bwPlayers[player.id]
        val res = player.card.imageRes
        bind.userDiesRoot.ivDeadPlayer.setImageResource(bwRes)
        bind.userDiesRoot.ivPlayer.setImageResource(res)

        (AnimatorInflater.loadAnimator(bind.userDiesRoot.context, R.animator.disappear) as AnimatorSet).apply {
            setTarget(bind.userDiesRoot.ivPlayer)
            startDelay = 500
            start()
            doOnEnd {
                bind.userDiesRoot.ivPlayer.visibility = ImageView.GONE
            }
        }

        val layoutParams = bind.userDiesRoot.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin =
            ((bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight * context.resources.displayMetrics.heightPixels / 2).toInt()
        bind.userDiesRoot.ivSuspectToken.layoutParams = layoutParams

        val roomList = context.resources.getStringArray(R.array.rooms)
        val toolList = context.resources.getStringArray(R.array.tools)
        val suspectList = context.resources.getStringArray(R.array.suspects)

        val solution = MapViewModel.gameModels.gameSolution

        for (solutionParameter in solution) {
            when {
                suspectList.contains(solutionParameter.name) -> bind.userDiesRoot.ivSuspectToken.setImageResource(suspectTokens[suspectList.indexOf(solutionParameter.name)])
                toolList.contains(solutionParameter.name) -> bind.userDiesRoot.ivToolToken.setImageResource(toolTokens[toolList.indexOf(solutionParameter.name)])
                else -> bind.userDiesRoot.ivRoomToken.setImageResource(roomTokens[roomList.indexOf(solutionParameter.name)])
            }
        }
    }

    fun close() {
        listener.onFinish()
    }
}