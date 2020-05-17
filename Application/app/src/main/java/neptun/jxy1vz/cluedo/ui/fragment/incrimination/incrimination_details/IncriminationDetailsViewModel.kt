package neptun.jxy1vz.cluedo.ui.fragment.incrimination.incrimination_details

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_incrimination_details.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentIncriminationDetailsBinding
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.domain.model.helper.*
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel

class IncriminationDetailsViewModel(
    private val bind: FragmentIncriminationDetailsBinding,
    private val context: Context,
    private val suspect: Suspect,
    private val listener: ViewModelListener,
    private val fragmentListener: DetailsFragmentListener
) : BaseObservable() {

    interface DetailsFragmentListener {
        fun deliverInformation(needToTakeNotes: Boolean)
    }

    private var playerShowedCard = false

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels

    private val roomList = context.resources.getStringArray(R.array.rooms)
    private val toolList = context.resources.getStringArray(R.array.tools)
    private val suspectList = context.resources.getStringArray(R.array.suspects)

    init {
        val layoutParams = bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin =
            ((bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight * screenHeight / 2).toInt()
        bind.ivSuspectToken.layoutParams = layoutParams

        bind.ivRoomToken.setImageResource(roomTokens[roomList.indexOf(suspect.room)])
        bind.ivToolToken.setImageResource(toolTokens[toolList.indexOf(suspect.tool)])
        bind.ivSuspectToken.setImageResource(suspectTokens[suspectList.indexOf(suspect.suspect)])

        bind.ivPlayerWhoSuspects.setImageResource(MapViewModel.playerHandler.getPlayerById(suspect.playerId).card.imageRes)

        processSuspect(suspect)
    }

    private fun processSuspect(suspect: Suspect) {
        var someoneShowedSomething = false
        var playerIdx = MapViewModel.gameModels.playerList.indexOf(
            MapViewModel.playerHandler.getPlayerById(suspect.playerId)
        )
        for (i in 0 until MapViewModel.gameModels.playerList.size - 1) {
            playerIdx--
            if (playerIdx < 0)
                playerIdx = MapViewModel.gameModels.playerList.lastIndex
            if (playerIdx == MapViewModel.gameModels.playerList.indexOf(MapViewModel.player)) {
                val cards =
                    MapViewModel.cardHandler.revealMysteryCards(
                        playerIdx,
                        suspect.room,
                        suspect.tool,
                        suspect.suspect
                    )
                if (cards != null) {
                    bind.ivPlayerWhoShows.setImageResource(MapViewModel.player.card.imageRes)
                    bind.detailsRoot.btnOk.isEnabled = false
                    val caution = TextView(bind.detailsRoot.context)
                    val params = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    params.startToStart = bind.ivSuspectToken.id
                    params.endToEnd = bind.ivSuspectToken.id
                    params.bottomToTop = bind.ivSuspectToken.id
                    params.bottomMargin = 15
                    caution.layoutParams = params
                    caution.text = context.resources.getString(R.string.show_card)
                    caution.setTextColor(Color.WHITE)
                    caution.setTypeface(null, Typeface.BOLD)
                    bind.detailsRoot.addView(caution)

                    (AnimatorInflater.loadAnimator(
                        bind.detailsRoot.context,
                        R.animator.appear
                    ) as AnimatorSet).apply {
                        setTarget(caution)
                        startDelay = 200
                        start()
                    }

                    for (card in cards) {
                        if (roomList.contains(card.name))
                            showCard(
                                card,
                                roomTokens,
                                roomTokensBW,
                                bind.ivRoomToken,
                                roomList,
                                caution
                            )
                        else
                            bind.ivRoomToken.setImageResource(roomTokensBW[roomList.indexOf(suspect.room)])

                        if (toolList.contains(card.name))
                            showCard(
                                card,
                                toolTokens,
                                toolTokensBW,
                                bind.ivToolToken,
                                toolList,
                                caution
                            )
                        else
                            bind.ivToolToken.setImageResource(toolTokensBW[toolList.indexOf(suspect.tool)])

                        if (suspectList.contains(card.name))
                            showCard(
                                card,
                                suspectTokens,
                                suspectTokensBW,
                                bind.ivSuspectToken,
                                suspectList,
                                caution
                            )
                        else
                            bind.ivSuspectToken.setImageResource(
                                suspectTokensBW[suspectList.indexOf(
                                    suspect.suspect
                                )]
                            )
                    }
                    someoneShowedSomething = true
                    fragmentListener.deliverInformation(false)
                }
            } else {
                val cards =
                    MapViewModel.cardHandler.revealMysteryCards(
                        playerIdx,
                        suspect.room,
                        suspect.tool,
                        suspect.suspect
                    )
                if (cards != null) {
                    bind.ivPlayerWhoShows.setImageResource(MapViewModel.gameModels.playerList[playerIdx].card.imageRes)
                    val revealedCard =
                        MapViewModel.gameModels.playerList[playerIdx].revealCardToPlayer(
                            suspect.playerId,
                            cards
                        )
                    someoneShowedSomething = true
                    MapViewModel.interactionHandler.letOtherPlayersKnow(
                        suspect,
                        MapViewModel.gameModels.playerList[playerIdx].id,
                        revealedCard.name
                    )
                    floatCard(revealedCard)
                }
            }
            if (someoneShowedSomething)
                break
        }
        if (!someoneShowedSomething) {
            MapViewModel.interactionHandler.letOtherPlayersKnow(suspect)
            bind.detailsRoot.ivPlayerWhoShows.setImageResource(R.drawable.szereplo_hatlap)

            val crossImage = ImageView(bind.detailsRoot.context)
            val layoutParams = ConstraintLayout.LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT)
            layoutParams.matchConstraintPercentWidth =
                (bind.ivPlayerWhoShows.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth
            layoutParams.matchConstraintPercentHeight =
                (bind.ivPlayerWhoShows.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth
            layoutParams.startToStart = bind.ivPlayerWhoShows.id
            layoutParams.endToEnd = bind.ivPlayerWhoShows.id
            layoutParams.topToTop = bind.ivPlayerWhoShows.id
            layoutParams.bottomToBottom = bind.ivPlayerWhoShows.id
            crossImage.setImageResource(R.drawable.cross)
            crossImage.layoutParams = layoutParams
            bind.detailsRoot.addView(crossImage)

            (AnimatorInflater.loadAnimator(
                bind.detailsRoot.context,
                R.animator.appear
            ) as AnimatorSet).apply {
                setTarget(crossImage)
                start()
                doOnEnd {
                    bind.ivRoomToken.setImageResource(roomTokensBW[roomList.indexOf(suspect.room)])
                    bind.ivToolToken.setImageResource(toolTokensBW[toolList.indexOf(suspect.tool)])
                    bind.ivSuspectToken.setImageResource(suspectTokensBW[suspectList.indexOf(suspect.suspect)])
                }
            }
        }

        if (suspect.playerId == MapViewModel.mPlayerId)
            MapViewModel.userFinishedHisTurn = true
    }

    private fun showCard(
        card: MysteryCard,
        tokenList: List<Int>,
        tokenListBW: List<Int>,
        ivToken: ImageView,
        nameList: Array<String>,
        cautionTextView: TextView
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val res = tokenList[nameList.indexOf(card.name)]
            val bwRes = tokenListBW[nameList.indexOf(card.name)]
            blinkToken(ivToken, bwRes, res, 3, 200)
        }

        ivToken.setOnClickListener {
            if (!playerShowedCard) {
                MapViewModel.interactionHandler.letOtherPlayersKnow(
                    suspect,
                    MapViewModel.mPlayerId,
                    card.name
                )

                (AnimatorInflater.loadAnimator(
                    bind.detailsRoot.context,
                    R.animator.disappear
                ) as AnimatorSet).apply {
                    setTarget(cautionTextView)
                    start()
                    doOnEnd {
                        bind.detailsRoot.removeView(cautionTextView)
                    }
                }

                playerShowedCard = true
                bind.detailsRoot.btnOk.isEnabled = true
                floatCard(card)
            }
        }
    }

    private fun watchCard(
        ivToken1: ImageView,
        ivToken2: ImageView,
        ivTargetToken: ImageView,
        bwTokenList1: List<Int>,
        bwTokenList2: List<Int>,
        bwTargetTokenList: List<Int>,
        targetTokenList: List<Int>,
        nameList1: Array<String>,
        nameList2: Array<String>,
        targetNameList: Array<String>,
        name1: String,
        name2: String,
        targetName: String
    ) {
        ivToken1.setImageResource(bwTokenList1[nameList1.indexOf(name1)])
        ivToken2.setImageResource(bwTokenList2[nameList2.indexOf(name2)])
        val targetImageView: ImageView = ivTargetToken
        val bwToken: Int = bwTargetTokenList[targetNameList.indexOf(targetName)]
        val token: Int = targetTokenList[targetNameList.indexOf(targetName)]

        GlobalScope.launch(Dispatchers.Main) {
            blinkToken(targetImageView, bwToken, token, 3, 200)
        }
    }

    private fun floatCard(revealedCard: MysteryCard) {
        val playerWhoSuspectsLayoutParams =
            bind.ivPlayerWhoSuspects.layoutParams as ConstraintLayout.LayoutParams
        val playerWhoShowsLayoutParams =
            bind.ivPlayerWhoShows.layoutParams as ConstraintLayout.LayoutParams
        val distance =
            (screenWidth - playerWhoShowsLayoutParams.marginEnd - playerWhoShowsLayoutParams.matchConstraintPercentWidth * screenWidth) - (playerWhoSuspectsLayoutParams.marginStart + playerWhoSuspectsLayoutParams.matchConstraintPercentWidth * screenWidth)

        val mysteryCardImage = ImageView(bind.detailsRoot.context)

        val layoutParams =
            ConstraintLayout.LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT)
        layoutParams.topToTop = bind.ivPlayerWhoShows.id
        layoutParams.bottomToBottom = bind.ivPlayerWhoShows.id
        layoutParams.endToStart = bind.ivPlayerWhoShows.id
        layoutParams.matchConstraintPercentWidth =
            playerWhoShowsLayoutParams.matchConstraintPercentWidth / 2
        layoutParams.matchConstraintPercentHeight =
            playerWhoShowsLayoutParams.matchConstraintPercentHeight / 2
        mysteryCardImage.layoutParams = layoutParams

        val miniCardWidth = layoutParams.matchConstraintPercentWidth * screenWidth
        mysteryCardImage.translationX = miniCardWidth / 2
        if (playerShowedCard)
            mysteryCardImage.setImageResource(revealedCard.imageRes)
        else
            mysteryCardImage.setImageResource(R.drawable.rejtely_hatlap)
        bind.detailsRoot.addView(mysteryCardImage)

        (AnimatorInflater.loadAnimator(
            bind.detailsRoot.context,
            R.animator.appear
        ) as AnimatorSet).apply {
            setTarget(mysteryCardImage)
            start()
            ObjectAnimator.ofFloat(
                mysteryCardImage,
                "translationX",
                mysteryCardImage.translationX,
                mysteryCardImage.translationX - distance
            ).apply {
                duration = 4000
                startDelay = 1000
                start()
                ObjectAnimator.ofFloat(
                    mysteryCardImage,
                    "translationY",
                    mysteryCardImage.translationY,
                    mysteryCardImage.translationY - 100f
                ).apply {
                    duration = 2000
                    start()
                    doOnEnd {
                        ObjectAnimator.ofFloat(
                            mysteryCardImage,
                            "translationY",
                            mysteryCardImage.translationY,
                            mysteryCardImage.translationY + 100f
                        ).apply {
                            duration = 2000
                            start()
                        }
                    }
                }
                doOnEnd {
                    (AnimatorInflater.loadAnimator(
                        bind.detailsRoot.context,
                        R.animator.disappear
                    ) as AnimatorSet).apply {
                        setTarget(mysteryCardImage)
                        start()
                        doOnEnd {
                            mysteryCardImage.visibility = ImageView.GONE
                            bind.detailsRoot.removeView(mysteryCardImage)

                            if (suspect.playerId == MapViewModel.mPlayerId) {
                                when {
                                    toolList.contains(revealedCard.name) -> {
                                        watchCard(
                                            bind.ivRoomToken,
                                            bind.ivSuspectToken,
                                            bind.ivToolToken,
                                            roomTokensBW,
                                            suspectTokensBW,
                                            toolTokensBW,
                                            toolTokens,
                                            roomList,
                                            suspectList,
                                            toolList,
                                            suspect.room,
                                            suspect.suspect,
                                            suspect.tool
                                        )
                                    }
                                    roomList.contains(revealedCard.name) -> {
                                        watchCard(
                                            bind.ivToolToken,
                                            bind.ivSuspectToken,
                                            bind.ivRoomToken,
                                            toolTokensBW,
                                            suspectTokensBW,
                                            roomTokensBW,
                                            roomTokens,
                                            toolList,
                                            suspectList,
                                            roomList,
                                            suspect.tool,
                                            suspect.suspect,
                                            suspect.room
                                        )
                                    }
                                    else -> {
                                        watchCard(
                                            bind.ivToolToken,
                                            bind.ivRoomToken,
                                            bind.ivSuspectToken,
                                            toolTokensBW,
                                            roomTokensBW,
                                            suspectTokensBW,
                                            suspectTokens,
                                            toolList,
                                            roomList,
                                            suspectList,
                                            suspect.tool,
                                            suspect.room,
                                            suspect.suspect
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun blinkToken(token: ImageView, bwRes: Int, res: Int, repeat: Int, delayTimeMillis: Long) {
        for (x in 1..repeat) {
            token.setImageResource(bwRes)
            delay(delayTimeMillis)
            token.setImageResource(res)
            delay(delayTimeMillis)
        }
    }

    fun close() {
        listener.onFinish()
    }
}