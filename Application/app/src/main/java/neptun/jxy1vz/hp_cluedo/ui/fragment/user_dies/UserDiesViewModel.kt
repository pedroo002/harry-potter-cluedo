package neptun.jxy1vz.hp_cluedo.ui.fragment.user_dies

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_user_dies.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.FragmentUserDiesBinding
import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel

class UserDiesViewModel(private val bind: FragmentUserDiesBinding, context: Context, player: Player, private val listener: ViewModelListener) : BaseObservable() {

    init {
        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).assetDao().apply {
                val playerCards = getAssetsByPrefix(AssetPrefixes.PLAYER_CARDS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                val bwPlayers = playerCards.filter { asset -> playerCards.indexOf(asset) % 2 == 1 }
                val toolTokens = getAssetsByPrefix(AssetPrefixes.MYSTERY_TOOL_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                val roomTokens = getAssetsByPrefix(AssetPrefixes.MYSTERY_ROOM_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                val suspectTokens = getAssetsByPrefix(AssetPrefixes.MYSTERY_SUSPECT_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                withContext(Dispatchers.Main) {
                    val bwRes = bwPlayers[player.id]
                    val res = player.card.imageRes
                    loadUrlImageIntoImageView(bwRes, context, bind.userDiesRoot.ivDeadPlayer)
                    loadUrlImageIntoImageView(res, context, bind.userDiesRoot.ivPlayer)

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

                    solution.forEach { solutionParameter ->
                        when {
                            suspectList.contains(solutionParameter.name) -> loadUrlImageIntoImageView(suspectTokens[suspectList.indexOf(solutionParameter.name) * 2], context, bind.userDiesRoot.ivSuspectToken)
                            toolList.contains(solutionParameter.name) -> loadUrlImageIntoImageView(toolTokens[toolList.indexOf(solutionParameter.name) * 2], context, bind.userDiesRoot.ivToolToken)
                            else -> loadUrlImageIntoImageView(roomTokens[roomList.indexOf(solutionParameter.name) * 2], context, bind.userDiesRoot.ivRoomToken)
                        }
                    }
                }
            }
        }
    }

    fun close() {
        listener.onFinish()
    }
}