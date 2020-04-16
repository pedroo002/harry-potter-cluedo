package neptun.jxy1vz.cluedo.ui.mystery_cards

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.coroutines.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.domain.model.MysteryType
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.helper.DatabaseAccess
import neptun.jxy1vz.cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.cluedo.ui.map.MapActivity

class MysteryCardViewModel(private val gameModel: GameModels, private val context: Context, private val playerId: Int, private val bind: ActivityMysteryCardBinding) : BaseObservable() {

    private val db = DatabaseAccess(context)
    private lateinit var player: Player

    init {
        bind.btnGo.isEnabled = false
        GlobalScope.launch(Dispatchers.IO) {
            val playerList = gameModel.loadPlayers()
            delay(5000)
            player = playerList[playerId]
            handOutCardsToPlayers()
        }
    }

    private suspend fun getRandomMysteryCards(playerId: Int): List<MysteryCard> = withContext(Dispatchers.IO) {
        return@withContext db.getMysteryCardsForPlayer(playerId)
    }

    fun openHogwarts() {
        val mapIntent = Intent(context, MapActivity::class.java)
        mapIntent.putExtra("Player ID", player.id)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mapIntent)
    }

    private suspend fun getMysteryCards(playerId: Int) {
        val cards = getRandomMysteryCards(playerId)
        gameModel.playerList[player.id].mysteryCards.addAll(cards)

        GlobalScope.launch(Dispatchers.Main) {
            if (playerId == player.id) {
                for (card in cards) {
                    val iv = when (card.type) {
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
                            iv.setImageResource(card.imageRes)
                        }
                    }
                }
            }
        }
    }

    private suspend fun setSolution() {
        getMysteryCards(-1)
        GlobalScope.launch(Dispatchers.Main) {
            bind.btnGo.isEnabled = true
        }
    }

    private suspend fun handOutCardsToPlayers() {
        getMysteryCards(player.id)

        var playerCount = context.getSharedPreferences("Game params", Context.MODE_PRIVATE).getInt("player_count", 0) - 1

        for (p in gameModel.playerList) {
            if (p.id != player.id && playerCount > 0) {
                getMysteryCards(p.id)
                playerCount--
            }
        }
        setSolution()
    }
}