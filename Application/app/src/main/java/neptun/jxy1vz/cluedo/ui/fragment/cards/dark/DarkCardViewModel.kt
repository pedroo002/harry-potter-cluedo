package neptun.jxy1vz.cluedo.ui.fragment.cards.dark

import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.core.view.marginBottom
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_dark_card.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentDarkCardBinding
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.helper.safeIcons
import neptun.jxy1vz.cluedo.domain.model.helper.unsafeIcons
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class DarkCardViewModel(
    private val bind: FragmentDarkCardBinding,
    context: Context,
    playerList: List<Player>,
    playerIds: List<Int>,
    private val listener: ViewModelListener
) : BaseObservable() {

    private val safePlayerIcons = HashMap<String, Int>()
    private val playerIcons = HashMap<String, Int>()

    init {
        val playerNameList = context.resources.getStringArray(R.array.characters)
        for (playerName in playerNameList) {
            safePlayerIcons[playerName] = safeIcons[playerNameList.indexOf(playerName)]
            playerIcons[playerName] = unsafeIcons[playerNameList.indexOf(playerName)]
        }

        val radius = (context.resources.displayMetrics.heightPixels - (bind.darkCardRoot.btnClose.height + bind.darkCardRoot.btnClose.marginBottom)) / 4

        for (player in playerList) {
            val i = playerList.indexOf(player)
            val imgRes =
                if (playerIds.contains(player.id))
                    playerIcons[player.card.name]!!
                else
                    safePlayerIcons[player.card.name]!!
            drawImage(
                imgRes,
                radius * sin(i * (2 * PI / playerList.size)),
                radius * cos(i * (2 * PI / playerList.size))
            )
        }
    }

    private fun drawImage(imgRes: Int, tranX: Double, tranY: Double) {
        val layoutParams = ConstraintLayout.LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT)
        layoutParams.matchConstraintPercentWidth = 0.15f
        layoutParams.matchConstraintPercentHeight = 0.15f
        layoutParams.topToTop = bind.darkCardRoot.ivDarkMark.id
        layoutParams.bottomToBottom = bind.darkCardRoot.ivDarkMark.id
        layoutParams.startToStart = bind.darkCardRoot.ivDarkMark.id
        layoutParams.endToEnd = bind.darkCardRoot.ivDarkMark.id
        val image = ImageView(bind.darkCardRoot.context)
        image.setImageResource(imgRes)
        image.visibility = ImageView.VISIBLE
        image.layoutParams = layoutParams
        image.translationX = tranX.toFloat() - image.width / 2
        image.translationY = tranY.toFloat() - image.height / 2
        bind.darkCardRoot.addView(image)
    }

    fun close() {
        listener.onFinish()
    }
}