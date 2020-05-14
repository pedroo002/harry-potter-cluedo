package neptun.jxy1vz.cluedo.ui.fragment.incrimination.incrimination_details

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentIncriminationDetailsBinding
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.domain.model.helper.*
import neptun.jxy1vz.cluedo.ui.dialog.show_card.ShowCardDialog
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.map.MapViewModel

class IncriminationDetailsViewModel(
    private val bind: FragmentIncriminationDetailsBinding,
    context: Context,
    suspect: Suspect,
    private val listener: ViewModelListener,
    private val fragmentListener: DetailsFragmentListener
) : BaseObservable() {

    interface DetailsFragmentListener {
        fun deliverInformation(needToTakeNotes: Boolean)
    }

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels

    private val roomList = context.resources.getStringArray(R.array.rooms)
    private val toolList = context.resources.getStringArray(R.array.tools)
    private val suspectList = context.resources.getStringArray(R.array.suspects)

    init {
        val layoutParams = bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin =
            ((bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight * screenHeight / 2).toInt() // (context.resources.displayMetrics.densityDpi / 160)).toInt()
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
                    ShowCardDialog(
                        suspect,
                        MapViewModel.playerHandler.getPlayerById(suspect.playerId).card.name,
                        cards,
                        MapViewModel.dialogHandler
                    ).show(MapViewModel.fm, ShowCardDialog.TAG)
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
                                            var targetImageView: ImageView
                                            var bwToken: Int
                                            var token: Int
                                            when {
                                                toolList.contains(revealedCard.name) -> {
                                                    bind.ivRoomToken.setImageResource(roomTokensBW[roomList.indexOf(suspect.room)])
                                                    bind.ivSuspectToken.setImageResource(suspectTokensBW[suspectList.indexOf(suspect.suspect)])
                                                    targetImageView = bind.ivToolToken
                                                    bwToken = toolTokensBW[toolList.indexOf(suspect.tool)]
                                                    token = toolTokens[toolList.indexOf(suspect.tool)]
                                                }
                                                roomList.contains(revealedCard.name) -> {
                                                    bind.ivToolToken.setImageResource(toolTokensBW[toolList.indexOf(suspect.tool)])
                                                    bind.ivSuspectToken.setImageResource(suspectTokensBW[suspectList.indexOf(suspect.suspect)])
                                                    targetImageView = bind.ivRoomToken
                                                    bwToken = roomTokensBW[roomList.indexOf(suspect.room)]
                                                    token = roomTokens[roomList.indexOf(suspect.room)]
                                                }
                                                else -> {
                                                    bind.ivToolToken.setImageResource(toolTokensBW[toolList.indexOf(suspect.tool)])
                                                    bind.ivRoomToken.setImageResource(roomTokensBW[roomList.indexOf(suspect.room)])
                                                    targetImageView = bind.ivSuspectToken
                                                    bwToken = suspectTokensBW[suspectList.indexOf(suspect.suspect)]
                                                    token = suspectTokens[suspectList.indexOf(suspect.suspect)]
                                                }
                                            }

                                            GlobalScope.launch(Dispatchers.Main) {
                                                for (x in 1..3) {
                                                    targetImageView.setImageResource(bwToken)
                                                    delay(500)
                                                    targetImageView.setImageResource(token)
                                                    delay(500)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (someoneShowedSomething)
                break
        }
        if (!someoneShowedSomething) {
            MapViewModel.interactionHandler.letOtherPlayersKnow(suspect)

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
            bind.detailsRoot.addView(crossImage)

            (AnimatorInflater.loadAnimator(
                bind.detailsRoot.context,
                R.animator.appear
            ) as AnimatorSet).apply {
                setTarget(crossImage)
                start()
            }
        }

        if (suspect.playerId == MapViewModel.mPlayerId)
            MapViewModel.userFinishedHisTurn = true
    }

    fun close() {
        listener.onFinish()
    }
}