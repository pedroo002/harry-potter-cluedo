package neptun.jxy1vz.cluedo.ui.mystery_cards

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.cluedo.model.MysteryCard
import neptun.jxy1vz.cluedo.model.MysteryType
import neptun.jxy1vz.cluedo.model.helper.mysteryCards
import neptun.jxy1vz.cluedo.model.helper.playerList
import kotlin.random.Random

class MysteryCardActivity : AppCompatActivity() {

    private lateinit var activityMysteryCardBinding: ActivityMysteryCardBinding
    private var playerId: Int = 0

    private fun getRandomMysteryCard(type: MysteryType): MysteryCard {
        var card: MysteryCard
        do {
            card = mysteryCards[Random.nextInt(0, mysteryCards.size)]
        } while (card.type != type)
        mysteryCards.remove(card)

        return card
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playerId = intent.getIntExtra("Player ID", 0)

        activityMysteryCardBinding = DataBindingUtil.setContentView(this, R.layout.activity_mystery_card)
        activityMysteryCardBinding.mysteryCardViewModel = MysteryCardViewModel(applicationContext, playerList[playerId])
    }

    override fun onResume() {
        super.onResume()

        (AnimatorInflater.loadAnimator(applicationContext, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(activityMysteryCardBinding.ivMysteryCardTool)
            start()
            doOnEnd {
                val tool = getRandomMysteryCard(MysteryType.TOOL)
                playerList[playerId].mysteryCards.add(tool)
                activityMysteryCardBinding.ivMysteryCardTool.setImageResource(tool.imageRes)
            }
        }

        (AnimatorInflater.loadAnimator(applicationContext, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(activityMysteryCardBinding.ivMysteryCardSuspect)
            start()
            doOnEnd {
                val suspect = getRandomMysteryCard(MysteryType.SUSPECT)
                playerList[playerId].mysteryCards.add(suspect)
                activityMysteryCardBinding.ivMysteryCardSuspect.setImageResource(suspect.imageRes)
            }
        }

        (AnimatorInflater.loadAnimator(applicationContext, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(activityMysteryCardBinding.ivMysteryCardVenue)
            start()
            doOnEnd {
                val venue = getRandomMysteryCard(MysteryType.VENUE)
                playerList[playerId].mysteryCards.add(venue)
                activityMysteryCardBinding.ivMysteryCardVenue.setImageResource(venue.imageRes)
            }
        }
    }
}