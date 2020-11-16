package neptun.jxy1vz.cluedo.ui.fragment.incrimination.incrimination_details

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.android.synthetic.main.fragment_incrimination_details.view.*
import kotlinx.coroutines.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentIncriminationDetailsBinding
import neptun.jxy1vz.cluedo.domain.model.card.MysteryCard
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.domain.model.helper.*
import neptun.jxy1vz.cluedo.domain.util.debugPrint
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.message.card_event.CardEventMessage
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import okhttp3.internal.wait
import java.lang.Exception

class IncriminationDetailsViewModel(
    private val bind: FragmentIncriminationDetailsBinding,
    private val context: Context,
    private val suspect: Suspect,
    private val listener: ViewModelListener
) : BaseObservable() {

    interface DetailsFragmentListener {
        fun deliverInformation(needToTakeNotes: Boolean)
    }

    private var finished = false
    private var playerShowedCard = false

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels

    private val roomList = context.resources.getStringArray(R.array.rooms)
    private val toolList = context.resources.getStringArray(R.array.tools)
    private val suspectList = context.resources.getStringArray(R.array.suspects)

    private lateinit var retrofit: RetrofitInstance
    private var waitForPlayers = 0

    init {
        val layoutParams = bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin =
            ((bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight * screenHeight / 2).toInt()
        bind.ivSuspectToken.layoutParams = layoutParams

        bind.ivRoomToken.setImageResource(roomTokens[roomList.indexOf(suspect.room)])
        bind.ivToolToken.setImageResource(toolTokens[toolList.indexOf(suspect.tool)])
        bind.ivSuspectToken.setImageResource(suspectTokens[suspectList.indexOf(suspect.suspect)])

        bind.ivPlayerWhoSuspects.setImageResource(MapViewModel.playerHandler.getPlayerById(suspect.playerId).card.imageRes)

        if (MapViewModel.isGameModeMulti()) {
            GlobalScope.launch(Dispatchers.Main) {
                bind.detailsRoot.btnOk.isEnabled = false
                withContext(Dispatchers.IO) {
                    subscribeToEvents()
                }
            }
        } else
            processSuspect(suspect)
    }

    private suspend fun subscribeToEvents() {
        PusherInstance.getInstance().getPresenceChannel(MapViewModel.channelName).apply {
            retrofit = RetrofitInstance.getInstance(context)
            if (suspect.playerId == MapViewModel.mPlayerId) {
                waitForPlayers = MapViewModel.gameModels.playerList.size - 1
                bind("incrimination-details-ready", object : PresenceChannelEventListener {
                    override fun onEvent(
                        channelName: String?,
                        eventName: String?,
                        message: String?
                    ) {
                        waitForPlayers--
                        if (waitForPlayers == 0) {
                            val myIdx =
                                MapViewModel.gameModels.playerList.indexOf(MapViewModel.player)
                            sendRequestToNextPlayer(myIdx)
                        }
                    }

                    override fun onSubscriptionSucceeded(p0: String?) {}
                    override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                    override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                    override fun userSubscribed(p0: String?, p1: User?) {}
                    override fun userUnsubscribed(p0: String?, p1: User?) {}
                })
            } else {
                bind("card-reveal-obligation", object : PresenceChannelEventListener {
                    override fun onEvent(
                        channelName: String?,
                        eventName: String?,
                        message: String?
                    ) {
                        val playerId = message!!.toInt()
                        processRequest(playerId)
                    }

                    override fun onSubscriptionSucceeded(p0: String?) {}
                    override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                    override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                    override fun userSubscribed(p0: String?, p1: User?) {}
                    override fun userUnsubscribed(p0: String?, p1: User?) {}
                })

                retrofit.cluedo.notifyIncriminationDetailsReady(MapViewModel.channelName)
            }

            bind("incrimination-finished", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    waitForPlayers--
                    if (waitForPlayers == 0)
                        close()
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("skip-reveal", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    val playerId = message!!.toInt()
                    processSkip(playerId)
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("helper-card-shown", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    val messageJson =
                        retrofit.moshi.adapter(CardEventMessage::class.java).fromJson(message!!)!!
                    if (messageJson.playerId != MapViewModel.mPlayerId)
                        processCardReveal(messageJson)
                    else {
                        waitForPlayers = MapViewModel.gameModels.playerList.size
                        MapViewModel.dialogHandler.setWaitingQueueSize(MapViewModel.gameModels.playerList.size)
                    }
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("nobody-showed-card", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    processNobodyShowedCard()
                    if (suspect.playerId == MapViewModel.mPlayerId)
                        MapViewModel.userFinishedHisTurn = true
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })
        }
    }

    private fun sendRequestToNextPlayer(currentIndex: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val nextIndex =
                if (currentIndex - 1 < 0) MapViewModel.gameModels.playerList.lastIndex else currentIndex - 1
            if (nextIndex == MapViewModel.gameModels.playerList.indexOf(MapViewModel.player)) {
                retrofit.cluedo.notifyNobodyCouldShow(MapViewModel.channelName)
            } else
                askPlayerToReveal(nextIndex)
        }
    }

    private suspend fun askPlayerToReveal(currentIndex: Int) {
        withContext(Dispatchers.Main) {
            bind.ivPlayerWhoShows.setImageResource(MapViewModel.gameModels.playerList[currentIndex].card.imageRes)
        }
        retrofit.cluedo.triggerPlayerToReveal(
            MapViewModel.channelName,
            MapViewModel.gameModels.playerList[currentIndex].id
        )
    }

    private fun processRequest(playerId: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            bind.ivPlayerWhoShows.setImageResource(MapViewModel.playerHandler.getPlayerById(playerId).card.imageRes)
            bind.ivRoomToken.setImageResource(roomTokens[roomList.indexOf(suspect.room)])
            bind.ivToolToken.setImageResource(toolTokens[toolList.indexOf(suspect.tool)])
            bind.ivSuspectToken.setImageResource(suspectTokens[suspectList.indexOf(suspect.suspect)])

            if (playerId == MapViewModel.mPlayerId) {
                val properMysteryCards = MapViewModel.cardHandler.revealMysteryCards(
                    MapViewModel.gameModels.playerList.indexOf(MapViewModel.player),
                    suspect.room,
                    suspect.tool,
                    suspect.suspect
                )
                properMysteryCards?.let {
                    showCaution(properMysteryCards)
                }

                if (properMysteryCards.isNullOrEmpty()) {
                    bind.ivRoomToken.setImageResource(roomTokensBW[roomList.indexOf(suspect.room)])
                    bind.ivToolToken.setImageResource(toolTokensBW[toolList.indexOf(suspect.tool)])
                    bind.ivSuspectToken.setImageResource(suspectTokensBW[suspectList.indexOf(suspect.suspect)])
                    bind.detailsRoot.btnSkip.apply {
                        isEnabled = true
                        visibility = Button.VISIBLE
                    }
                }
            }
        }
    }

    private fun processSkip(playerId: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            bind.detailsRoot.ivSkipBubble.visibility = ImageView.VISIBLE
            (AnimatorInflater.loadAnimator(
                bind.detailsRoot.context,
                R.animator.appear
            ) as AnimatorSet).apply {
                setTarget(bind.detailsRoot.ivSkipBubble)
                start()
                doOnEnd {
                    (AnimatorInflater.loadAnimator(
                        bind.detailsRoot.context,
                        R.animator.disappear
                    ) as AnimatorSet).apply {
                        setTarget(bind.detailsRoot.ivSkipBubble)
                        start()
                        doOnEnd {
                            bind.detailsRoot.ivSkipBubble.visibility = ImageView.GONE
                            if (suspect.playerId == MapViewModel.mPlayerId) {
                                val currentIdx = MapViewModel.gameModels.playerList.indexOf(
                                    MapViewModel.playerHandler.getPlayerById(playerId)
                                )
                                sendRequestToNextPlayer(currentIdx)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun processCardReveal(data: CardEventMessage) {
        GlobalScope.launch(Dispatchers.Main) {
            val card =
                MapViewModel.playerHandler.getPlayerById(data.playerId).mysteryCards.find { card -> card.name == data.cardName }!!
            floatCard(card)
            waitForPlayers = MapViewModel.gameModels.playerList.size
            bind.detailsRoot.btnOk.isEnabled = true

            MapViewModel.dialogHandler.setWaitingQueueSize(MapViewModel.gameModels.playerList.size)
        }
    }

    private fun sendCardToPlayer(card: MysteryCard) {
        GlobalScope.launch(Dispatchers.IO) {
            retrofit.cluedo.showCard(MapViewModel.channelName, MapViewModel.mPlayerId!!, card.name)
        }
    }

    private fun processNobodyShowedCard() {
        GlobalScope.launch(Dispatchers.Main) {
            if (!MapViewModel.isGameModeMulti())
                MapViewModel.interactionHandler.letOtherPlayersKnow(suspect)
            else {
                waitForPlayers = MapViewModel.gameModels.playerList.size
                bind.detailsRoot.btnOk.isEnabled = true
                MapViewModel.dialogHandler.setWaitingQueueSize(MapViewModel.gameModels.playerList.size)
            }

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

                    showCaution(cards)
                    someoneShowedSomething = true
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
            processNobodyShowedCard()
        }

        if (suspect.playerId == MapViewModel.mPlayerId)
            MapViewModel.userFinishedHisTurn = true
    }

    private fun showCaution(cards: List<MysteryCard>) {
        (AnimatorInflater.loadAnimator(
            bind.detailsRoot.context,
            R.animator.appear
        ) as AnimatorSet).apply {
            setTarget(bind.detailsRoot.tvShowCard)
            startDelay = 200
            start()
            doOnEnd {
                bind.detailsRoot.tvShowCard.visibility = TextView.VISIBLE
            }
        }

        for (card in cards) {
            if (roomList.contains(card.name))
                showCard(
                    card,
                    roomTokens,
                    roomTokensBW,
                    bind.ivRoomToken,
                    roomList,
                    bind.detailsRoot.tvShowCard
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
                    bind.detailsRoot.tvShowCard
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
                    bind.detailsRoot.tvShowCard
                )
            else
                bind.ivSuspectToken.setImageResource(
                    suspectTokensBW[suspectList.indexOf(
                        suspect.suspect
                    )]
                )
        }
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

            ivToken.setOnClickListener {
                if (!playerShowedCard) {
                    if (MapViewModel.isGameModeMulti())
                        sendCardToPlayer(card)

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
                            bind.detailsRoot.tvShowCard.visibility = TextView.GONE
                        }
                    }

                    playerShowedCard = true
                    floatCard(card)
                    bind.detailsRoot.btnOk.isEnabled = true
                }
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

    private suspend fun blinkToken(
        token: ImageView,
        bwRes: Int,
        res: Int,
        repeat: Int,
        delayTimeMillis: Long
    ) {
        for (x in 1..repeat) {
            token.setImageResource(bwRes)
            delay(delayTimeMillis)
            token.setImageResource(res)
            delay(delayTimeMillis)
        }
    }

    fun close() {
        if (MapViewModel.isGameModeMulti()) {
            if (!finished)
                GlobalScope.launch(Dispatchers.IO) {
                    retrofit.cluedo.notifyIncriminationFinished(MapViewModel.channelName)
                    finished = true
                    bind.detailsRoot.btnOk.isEnabled = false
                }
            else
                GlobalScope.launch(Dispatchers.Main) {
                    listener.onFinish()
                }
        }
        else
            listener.onFinish()
    }

    fun skip() {
        GlobalScope.launch(Dispatchers.IO) {
            retrofit.cluedo.skipCardReveal(MapViewModel.channelName, MapViewModel.mPlayerId!!)
            withContext(Dispatchers.Main) {
                bind.detailsRoot.btnSkip.apply {
                    isEnabled = false
                    visibility = Button.GONE
                }
            }
        }
    }
}