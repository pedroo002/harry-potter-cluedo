package neptun.jxy1vz.cluedo.domain.handler

import android.animation.ObjectAnimator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.model.*
import neptun.jxy1vz.cluedo.ui.dialog.card_dialog.helper.HelperCardDialog
import neptun.jxy1vz.cluedo.ui.dialog.dice.DiceRollerViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.fm
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mContext
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.otherPlayerStepsOnStar
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.player
import kotlin.math.abs
import kotlin.math.floor

class CardHandler(private val map: MapViewModel.Companion) {
    fun handOutHelperCards() {
        if (!isGameRunning) {
            GlobalScope.launch(Dispatchers.Main) {
                for (p in gameModels.playerList) {
                    if (p.id != player.id) {
                        map.interactionHandler.getCard(p.id, DiceRollerViewModel.CardType.HELPER)
                        delay(5000)
                    }
                }
                map.cameraHandler.moveCameraToPlayer(player.id)
                map.interactionHandler.getCard(player.id, DiceRollerViewModel.CardType.HELPER)
                isGameRunning = true
                map.gameSequenceHandler.moveToNextPlayer()
            }
        }
    }

    fun typeOf(mysteryName: String): MysteryType {
        return when {
            mContext!!.resources.getStringArray(R.array.rooms).contains(mysteryName) -> MysteryType.VENUE
            mContext!!.resources.getStringArray(R.array.suspects).contains(mysteryName) -> MysteryType.SUSPECT
            else -> MysteryType.TOOL
        }
    }

    fun revealMysteryCards(
        playerIdx: Int,
        room: String,
        tool: String,
        suspect: String
    ): List<MysteryCard>? {
        val cardList: MutableList<MysteryCard> = ArrayList()
        for (card in gameModels.playerList[playerIdx].mysteryCards) {
            if (card.name == room || card.name == tool || card.name == suspect)
                cardList.add(card)
        }

        if (cardList.isNotEmpty())
            return cardList
        return null
    }

    fun showCard(playerId: Int, card: Card, type: DiceRollerViewModel.CardType) {
        GlobalScope.launch {
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

            val mapSizeX = mapRoot.mapLayout.ivMap.right.toFloat()
            val mapSizeY = mapRoot.mapLayout.ivMap.bottom.toFloat()
            val screenSizeX = mContext!!.resources.displayMetrics.widthPixels
            val screenSizeY = mContext!!.resources.displayMetrics.heightPixels
            val panX = mapRoot.panX
            val panY = mapRoot.panY

            val possibleRow =
                when {
                    panY < screenSizeY -> floor(abs(MapViewModel.ROWS * panY / mapSizeY)).toInt()
                    panY > mapSizeY - screenSizeY -> floor(abs((mapSizeY - screenSizeY) / mapSizeY) * MapViewModel.ROWS).toInt()
                    else -> floor(
                        abs((mapSizeY - panY) / mapSizeY) * MapViewModel.ROWS
                    ).toInt()
                }
            val possibleCol =
                when {
                    panX < screenSizeX -> floor(abs(MapViewModel.COLS * panX / mapSizeX)).toInt()
                    panX > mapSizeX - screenSizeX -> floor(abs((mapSizeX - screenSizeX) / mapSizeX) * MapViewModel.COLS).toInt()
                    else -> floor(
                        abs((mapSizeX - panX) / mapSizeX) * MapViewModel.COLS
                    ).toInt()
                }

            map.uiHandler.setLayoutConstraintTop(cardImage, gameModels.rows[possibleRow + 1])
            map.uiHandler.setLayoutConstraintStart(cardImage, gameModels.cols[possibleCol + 1])

            cardImage.translationX = -1 * cardImage.width.toFloat()
            cardImage.visibility = ImageView.VISIBLE

            withContext(Dispatchers.Main) {
                mapRoot.mapLayout.addView(cardImage)
                ObjectAnimator.ofFloat(cardImage, "translationX", cardImage.width.toFloat())
                    .apply {
                        duration = 1000
                        start()
                        doOnEnd {
                            cardImage.translationX = 0f
                            ObjectAnimator.ofFloat(
                                cardImage,
                                "translationX",
                                -1 * cardImage.width.toFloat()
                            ).apply {
                                duration = 1000
                                startDelay = 2000
                                start()
                                doOnEnd {
                                    mapRoot.mapLayout.removeView(cardImage)
                                    if (playerId != player.id)
                                        evaluateCard(playerId, card, type)
                                }
                            }
                        }
                    }
            }
        }
    }

    fun evaluateCard(playerId: Int, randomCard: Card, type: DiceRollerViewModel.CardType) {
        when (type) {
            DiceRollerViewModel.CardType.HELPER -> {
                map.gameSequenceHandler.continueGame()
                if (otherPlayerStepsOnStar) {
                    map.gameSequenceHandler.moveToNextPlayer()
                    otherPlayerStepsOnStar = false
                }

                if (map.playerHandler.getPlayerById(playerId).helperCards.isNullOrEmpty()) {
                    map.playerHandler.getPlayerById(playerId).helperCards = ArrayList()
                }
                map.playerHandler.getPlayerById(playerId).helperCards!!.add(randomCard as HelperCard)

                if (playerId == player.id)
                    HelperCardDialog(randomCard.imageRes, map.dialogHandler).show(
                        fm,
                        "DIALOG_HELPER"
                    )
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