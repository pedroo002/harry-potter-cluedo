package neptun.jxy1vz.cluedo.ui.mystery_cards

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.MysteryCard
import neptun.jxy1vz.cluedo.model.MysteryType
import neptun.jxy1vz.cluedo.model.Player
import neptun.jxy1vz.cluedo.model.helper.mysteryCards
import neptun.jxy1vz.cluedo.model.helper.playerList
import neptun.jxy1vz.cluedo.ui.map.MapActivity
import kotlin.random.Random

class MysteryCardViewModel(private val context: Context, private val player: Player) : BaseObservable() {

    private lateinit var solution: MutableList<MysteryCard>

    private fun getRandomMysteryCard(type: MysteryType): MysteryCard {
        var card: MysteryCard
        do {
            card = mysteryCards[Random.nextInt(0, mysteryCards.size)]
        } while (card.type != type)
        mysteryCards.remove(card)

        return card
    }

    fun openHogwarts() {
        val mapIntent = Intent(context, MapActivity::class.java)
        mapIntent.putExtra("Player ID", player.id)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mapIntent)
    }

    fun getMysteryCard(type: MysteryType, iv: ImageView) {
        val card = getRandomMysteryCard(type)
        playerList[player.id].mysteryCards.add(card)
        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(iv)
            start()
            doOnEnd {
                iv.setImageResource(card.imageRes)
            }
        }
    }

    fun setSolution() {
        solution = ArrayList()
        solution.add(getRandomMysteryCard(MysteryType.TOOL))
        solution.add(getRandomMysteryCard(MysteryType.SUSPECT))
        solution.add(getRandomMysteryCard(MysteryType.VENUE))

        handOutCardsToOtherPlayers()
    }

    private fun handOutCardsToOtherPlayers() {
        var playerCount = context.getSharedPreferences("Game params", Context.MODE_PRIVATE).getInt("player_count", 0) - 1

        val playersToDelete: MutableList<Player> = ArrayList()

        for (p in playerList) {
            if (p.id != player.id && playerCount > 0) {
                p.mysteryCards.add(getRandomMysteryCard(MysteryType.TOOL))
                p.mysteryCards.add(getRandomMysteryCard(MysteryType.SUSPECT))
                p.mysteryCards.add(getRandomMysteryCard(MysteryType.VENUE))
                playerCount--
            }
            else if (p.id != player.id && playerCount == 0)
                playersToDelete.add(p)
        }

        for (player in playersToDelete) {
            playerList.remove(player)
        }
    }
}