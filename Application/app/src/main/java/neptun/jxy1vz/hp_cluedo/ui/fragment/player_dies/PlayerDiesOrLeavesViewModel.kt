package neptun.jxy1vz.hp_cluedo.ui.fragment.player_dies

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.android.synthetic.main.fragment_player_dies.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.FragmentPlayerDiesBinding
import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class PlayerDiesOrLeavesViewModel(
    private val bind: FragmentPlayerDiesBinding,
    context: Context,
    player: Player,
    title: String,
    lifecycleScope: LifecycleCoroutineScope,
    private val listener: ViewModelListener
) : BaseObservable() {

    private lateinit var title: String

    init {
        lifecycleScope.launch(Dispatchers.IO) {
            val playerCards = CluedoDatabase.getInstance(context).assetDao()
                .getAssetsByPrefix(AssetPrefixes.PLAYER_CARDS.string())!!
                .map { assetDBmodel -> assetDBmodel.url }
            val resList = playerCards.filter { asset -> playerCards.indexOf(asset) % 2 == 0 }
            val bwResList = playerCards.filter { asset -> playerCards.indexOf(asset) % 2 == 1 }
            withContext(Dispatchers.Main) {
                setTitle(title)
                val res = player.card.imageRes
                loadUrlImageIntoImageView(bwResList[resList.indexOf(res)], context, bind.playerDiesRoot.ivDeadPlayer)
                loadUrlImageIntoImageView(res, context, bind.playerDiesRoot.ivPlayer)

                (AnimatorInflater.loadAnimator(
                    bind.playerDiesRoot.context,
                    R.animator.disappear
                ) as AnimatorSet).apply {
                    setTarget(bind.playerDiesRoot.ivPlayer)
                    startDelay = 500
                    start()
                    doOnEnd {
                        bind.playerDiesRoot.ivPlayer.visibility = ImageView.GONE
                    }
                }
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