package neptun.jxy1vz.cluedo.ui.mystery_cards

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.cluedo.ui.map.MapActivity

class MysteryCardViewModel(
    private val gameModel: GameModels,
    private val context: Context,
    private val playerId: Int,
    private val bind: ActivityMysteryCardBinding
) : BaseObservable() {

    private lateinit var player: Player

    init {
        bind.btnGo.isEnabled = false
        GlobalScope.launch(Dispatchers.IO) {
            val playerList = gameModel.loadPlayers()
            withContext(Dispatchers.Main) {
                player = playerList[playerId]
                handOutCardsToPlayers()
            }
        }
    }

    fun openHogwarts() {
        val mapIntent = Intent(context, MapActivity::class.java)
        mapIntent.putExtra("Player ID", player.id)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mapIntent)
    }

    private suspend fun getMysteryCards(playerIds: List<Int>) {
        val cards = gameModel.db.getMysteryCardsForPlayers(playerIds)

        withContext(Dispatchers.Main) {
            val cols = listOf(
                bind.guidelineColumn1Left,
                bind.guidelineColumn1Right,
                bind.guidelineColumn2Left,
                bind.guidelineColumn2Right,
                bind.guidelineColumn3Left,
                bind.guidelineColumn3Right
            )
            val rows = listOf(
                bind.guidelineRow1Top,
                bind.guidelineRow1Bottom,
                bind.guidelineRow2Top,
                bind.guidelineRow2Bottom
            )

            var i = 0
            var gatheredCards = 0
            for (card in cards) {
                if (card.second == playerId)
                    gatheredCards++
            }
            for (card in cards) {
                if (card.second == playerId) {
                    val iv = ImageView(bind.cardImages.context)
                    iv.setImageResource(card.first.verso)
                    iv.layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                    )
                    iv.visibility = ImageView.VISIBLE
                    val row = i / 3
                    val col = i % 3
                    val rowAddition = if (gatheredCards == 3) 3 else 1
                    setLayoutConstraintHorizontal(iv, cols[col * 2].id, cols[col * 2 + 1].id)
                    setLayoutConstraintVertical(iv, rows[row * 2].id, rows[row * 2 + rowAddition].id)
                    bind.cardImages.addView(iv)
                    i++

                    (AnimatorInflater.loadAnimator(
                        context,
                        R.animator.card_flip
                    ) as AnimatorSet).apply {
                        setTarget(iv)
                        start()
                        doOnEnd {
                            iv.setImageResource(card.first.imageRes)
                            if (i == gatheredCards)
                                bind.btnGo.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private suspend fun handOutCardsToPlayers() {
        val idList = ArrayList<Int>()
        idList.add(playerId)

        var playerCount = context.getSharedPreferences("Game params", Context.MODE_PRIVATE).getInt(
            "player_count",
            0
        ) - 1

        for (p in gameModel.playerList) {
            if (p.id != player.id && playerCount > 0) {
                idList.add(p.id)
                playerCount--
            }
        }
        idList.add(-1)
        getMysteryCards(idList)
    }

    private fun setLayoutConstraintVertical(view: View, top: Int, bottom: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = top
        layoutParams.bottomToBottom = bottom
        view.layoutParams = layoutParams
    }

    private fun setLayoutConstraintHorizontal(view: View, start: Int?, end: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        start?.let {
            layoutParams.startToStart = start
        }
        layoutParams.endToEnd = end
        view.layoutParams = layoutParams
    }
}