package neptun.jxy1vz.cluedo.domain.handler

import android.animation.ObjectAnimator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.model.card.*
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.channelName
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.finishedCardCheck
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.isGameModeMulti
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.mContext
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.mPlayerId
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.otherPlayerStepsOnStar
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.retrofit
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.userFinishedHisTurn
import neptun.jxy1vz.cluedo.ui.fragment.dice_roller.DiceRollerViewModel
import kotlin.math.abs

class CardHandler(private val map: MapViewModel.Companion) {
    fun handOutHelperCards() {
        GlobalScope.launch(Dispatchers.Main) {
            for (p in gameModels.playerList) {
                if (p.id != mPlayerId) {
                    map.interactionHandler.getCard(
                        p.id,
                        DiceRollerViewModel.CardType.HELPER
                    )
                    delay(5000)
                }
            }
            map.cameraHandler.moveCameraToPlayer(mPlayerId!!)
            delay(1000)
            map.interactionHandler.getCard(mPlayerId!!, DiceRollerViewModel.CardType.HELPER)
        }
    }

    suspend fun handOutHelperCardMulti(playerId: Int) {
        map.cameraHandler.moveCameraToPlayer(playerId)
        delay(1000)
        val card = gameModels.db.getCardBySuperType(
            playerId,
            mContext!!.getString(R.string.helper_prefix)
        ) as HelperCard
        showCard(playerId, card, DiceRollerViewModel.CardType.HELPER)
        retrofit.cluedo.sendCardEvent(channelName, playerId, card.name)
    }

    fun typeOf(mysteryName: String): MysteryType {
        return when {
            mContext!!.resources.getStringArray(R.array.rooms)
                .contains(mysteryName) -> MysteryType.VENUE
            mContext!!.resources.getStringArray(R.array.suspects)
                .contains(mysteryName) -> MysteryType.SUSPECT
            else -> MysteryType.TOOL
        }
    }

    fun revealMysteryCards(
        playerIdx: Int,
        room: String,
        tool: String,
        suspect: String
    ): List<MysteryCard>? {
        val cardList: ArrayList<MysteryCard> = ArrayList()
        for (card in gameModels.playerList[playerIdx].mysteryCards) {
            if (card.name == room || card.name == tool || card.name == suspect)
                cardList.add(card)
        }

        if (cardList.isNotEmpty())
            return cardList
        return null
    }

    suspend fun showCard(playerId: Int, card: Card, type: DiceRollerViewModel.CardType?) {
        withContext(Dispatchers.Main) {
            map.cameraHandler.moveCameraToPlayer(playerId)
        }
        delay(1000)

        val cardImage = ImageView(mapRoot.mapLayout.context)
        cardImage.layoutParams = ConstraintLayout.LayoutParams(
            mContext!!.resources.displayMetrics.widthPixels / 3,
            3 * mContext!!.resources.displayMetrics.heightPixels / 4
        )
        cardImage.setImageResource(card.imageRes)

        map.uiHandler.setLayoutConstraintTop(cardImage, gameModels.rows[0])
        map.uiHandler.setLayoutConstraintStart(cardImage, gameModels.cols[0])
        cardImage.translationX = abs(mapRoot.panX)
        cardImage.translationY = abs(mapRoot.panY)

        cardImage.translationX -= cardImage.width.toFloat()
        cardImage.visibility = ImageView.VISIBLE

        withContext(Dispatchers.Main) {
            mapRoot.mapLayout.addView(cardImage)
            ObjectAnimator.ofFloat(
                cardImage,
                "translationX",
                cardImage.translationX,
                cardImage.translationX + cardImage.width.toFloat()
            )
                .apply {
                    duration = 1000
                    start()
                    doOnEnd {
                        ObjectAnimator.ofFloat(
                            cardImage,
                            "translationX",
                            cardImage.translationX,
                            cardImage.translationX - cardImage.width.toFloat()
                        ).apply {
                            duration = 1000
                            startDelay = 2000
                            start()
                            doOnEnd {
                                mapRoot.mapLayout.removeView(cardImage)
                                evaluateCard(playerId, card, type)
                            }
                        }
                    }
                }
        }
    }

    private fun evaluateCard(playerId: Int, randomCard: Card, type: DiceRollerViewModel.CardType?) {
        when (type) {
            DiceRollerViewModel.CardType.HELPER -> {
                finishedCardCheck = true

                map.gameSequenceHandler.continueGame()
                if (otherPlayerStepsOnStar) {
                    map.gameSequenceHandler.moveToNextPlayer()
                    otherPlayerStepsOnStar = false
                }
                if (playerId == mPlayerId && userFinishedHisTurn)
                    map.gameSequenceHandler.moveToNextPlayer()

                if (map.playerHandler.getPlayerById(playerId).helperCards.isNullOrEmpty()) {
                    map.playerHandler.getPlayerById(playerId).helperCards = ArrayList()
                }

                map.playerHandler.getPlayerById(playerId).helperCards!!.add(randomCard as HelperCard)

                if (!isGameRunning && !isGameModeMulti() && playerId == mPlayerId) {
                    map.gameSequenceHandler.startGame()
                } else if (!isGameRunning && isGameModeMulti() && gameModels.playerList.last().id == playerId) {
                    map.gameSequenceHandler.startGame()
                }
            }
            else -> {
                GlobalScope.launch(Dispatchers.IO) {
                    val helperCards =
                        gameModels.db.getHelperCardsAgainstDarkCard(randomCard as DarkCard)
                    withContext(Dispatchers.Main) {
                        helperCards?.let {
                            val idList = ArrayList<Int>()
                            for (card in helperCards)
                                idList.add(card.id)
                            randomCard.helperIds = idList
                        }
                        map.playerHandler.harmToAffectedPlayers(randomCard)
                    }
                }
            }
        }
    }
}