package neptun.jxy1vz.cluedo.ui.mystery_cards

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.cluedo.domain.model.MysteryType
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
            var i = 0
            for (card in cards) {
                if (card.second == playerId) {
                    val iv = when (card.first.type) {
                        MysteryType.TOOL -> bind.ivMysteryCardTool
                        MysteryType.SUSPECT -> bind.ivMysteryCardSuspect
                        else -> bind.ivMysteryCardVenue
                    }
                    (AnimatorInflater.loadAnimator(
                        context,
                        R.animator.card_flip
                    ) as AnimatorSet).apply {
                        setTarget(iv)
                        start()
                        doOnEnd {
                            iv.setImageResource(card.first.imageRes)
                            i++
                            if (i == 3)
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

        var playerCount = context.getSharedPreferences("Game params", Context.MODE_PRIVATE).getInt("player_count", 0) - 1

        for (p in gameModel.playerList) {
            if (p.id != player.id && playerCount > 0) {
                idList.add(p.id)
                playerCount--
            }
        }
        idList.add(-1)
        getMysteryCards(idList)
    }
}