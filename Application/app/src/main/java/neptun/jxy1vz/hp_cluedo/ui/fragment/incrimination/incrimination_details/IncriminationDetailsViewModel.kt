package neptun.jxy1vz.hp_cluedo.ui.fragment.incrimination.incrimination_details

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import com.google.android.material.snackbar.Snackbar
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.android.synthetic.main.fragment_incrimination_details.view.*
import kotlinx.coroutines.*
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.FragmentIncriminationDetailsBinding
import neptun.jxy1vz.hp_cluedo.domain.model.Suspect
import neptun.jxy1vz.hp_cluedo.domain.model.card.MysteryCard
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.data.network.model.message.CardEventMessage
import neptun.jxy1vz.hp_cluedo.data.network.pusher.PusherInstance
import neptun.jxy1vz.hp_cluedo.domain.model.ThinkingPlayer
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import retrofit2.HttpException
import java.net.SocketTimeoutException

class IncriminationDetailsViewModel(
    private val bind: FragmentIncriminationDetailsBinding,
    private val context: Context,
    private val suspect: Suspect,
    private val listener: ViewModelListener
) : BaseObservable() {

    private var finished = false
    private var playerShowedCard = false

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels

    private val roomList = context.resources.getStringArray(R.array.rooms)
    private val toolList = context.resources.getStringArray(R.array.tools)
    private val suspectList = context.resources.getStringArray(R.array.suspects)

    private lateinit var retrofit: RetrofitInstance
    private var waitForPlayers = 0

    private lateinit var roomTokens: List<String>
    private lateinit var toolTokens: List<String>
    private lateinit var suspectTokens: List<String>

    init {
        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).assetDao().apply {
                roomTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_ROOM_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                toolTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_TOOL_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                suspectTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_SUSPECT_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }

                withContext(Dispatchers.Main) {
                    val layoutParams = bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.bottomMargin =
                        ((bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight * screenHeight / 2).toInt()
                    bind.ivSuspectToken.layoutParams = layoutParams

                    loadUrlImageIntoImageView(roomTokens[roomList.indexOf(suspect.room) * 2], context, bind.ivRoomToken)
                    loadUrlImageIntoImageView(toolTokens[toolList.indexOf(suspect.tool) * 2], context, bind.ivToolToken)
                    loadUrlImageIntoImageView(suspectTokens[suspectList.indexOf(suspect.suspect) * 2], context, bind.ivSuspectToken)

                    loadUrlImageIntoImageView(MapViewModel.playerHandler.getPlayerById(suspect.playerId).card.imageRes, context, bind.ivPlayerWhoSuspects)

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
            }
        }
    }

    private suspend fun subscribeToEvents() {
        PusherInstance.getInstance().getPresenceChannel(MapViewModel.channelName).apply {
            retrofit = RetrofitInstance.getInstance(context)
            if (MapViewModel.playerInTurn == MapViewModel.mPlayerId) {
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
                    if (MapViewModel.playerInTurn == MapViewModel.mPlayerId)
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
                try {
                    retrofit.cluedo.notifyNobodyCouldShow(MapViewModel.channelName)
                }
                catch (ex: HttpException) {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(bind.root, ex.message ?: "Hiba lépett fel a hálózatban.", Snackbar.LENGTH_LONG).setAction("Újra") {
                            sendRequestToNextPlayer(currentIndex)
                        }.show()
                    }
                }
                catch (ex: SocketTimeoutException) {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(bind.root, "A kapcsolat túllépte az időkorlátot!", Snackbar.LENGTH_LONG).setAction("Újra") {
                            sendRequestToNextPlayer(currentIndex)
                        }.show()
                    }
                }
            } else
                askPlayerToReveal(nextIndex)
        }
    }

    private suspend fun askPlayerToReveal(currentIndex: Int) {
        withContext(Dispatchers.Main) {
            loadUrlImageIntoImageView(MapViewModel.gameModels.playerList[currentIndex].card.imageRes, context, bind.ivPlayerWhoShows)
        }
        try {
            retrofit.cluedo.triggerPlayerToReveal(
                MapViewModel.channelName,
                MapViewModel.gameModels.playerList[currentIndex].id
            )
        }
        catch (ex: HttpException) {
            withContext(Dispatchers.Main) {
                Snackbar.make(bind.root, ex.message ?: "Hiba lépett fel a hálózatban.", Snackbar.LENGTH_LONG).show()
            }
        }
        catch (ex: SocketTimeoutException) {
            withContext(Dispatchers.Main) {
                Snackbar.make(bind.root, "A kapcsolat túllépte az időkorlátot!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun processRequest(playerId: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            loadUrlImageIntoImageView(MapViewModel.playerHandler.getPlayerById(playerId).card.imageRes, context, bind.ivPlayerWhoShows)
            loadUrlImageIntoImageView(roomTokens[roomList.indexOf(suspect.room) * 2], context, bind.ivRoomToken)
            loadUrlImageIntoImageView(toolTokens[toolList.indexOf(suspect.tool) * 2], context, bind.ivToolToken)
            loadUrlImageIntoImageView(suspectTokens[suspectList.indexOf(suspect.suspect) * 2], context, bind.ivSuspectToken)

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
                    loadUrlImageIntoImageView(roomTokens[roomList.indexOf(suspect.room) * 2 + 1], context, bind.ivRoomToken)
                    loadUrlImageIntoImageView(toolTokens[toolList.indexOf(suspect.tool) * 2 + 1], context, bind.ivToolToken)
                    loadUrlImageIntoImageView(suspectTokens[suspectList.indexOf(suspect.suspect) * 2 + 1], context, bind.ivSuspectToken)
                    bind.detailsRoot.btnSkip.apply {
                        isEnabled = true
                        visibility = Button.VISIBLE
                    }
                }
            }
        }
    }

    private fun showSkipBubble() {
        bind.detailsRoot.ivSkipBubble.visibility = ImageView.VISIBLE
        (AnimatorInflater.loadAnimator(
            bind.detailsRoot.context,
            R.animator.appear
        ) as AnimatorSet).apply {
            setTarget(bind.detailsRoot.ivSkipBubble)
            duration = 2000
            start()
            doOnEnd {
                (AnimatorInflater.loadAnimator(
                    bind.detailsRoot.context,
                    R.animator.disappear
                ) as AnimatorSet).apply {
                    setTarget(bind.detailsRoot.ivSkipBubble)
                    duration = 2500
                    start()
                    doOnEnd {
                        bind.detailsRoot.ivSkipBubble.visibility = ImageView.GONE
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
                            if (MapViewModel.isGameModeMulti()) {
                                if (MapViewModel.playerInTurn == MapViewModel.mPlayerId) {
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
            try {
                retrofit.cluedo.showCard(MapViewModel.channelName, MapViewModel.mPlayerId!!, card.name)
            }
            catch (ex: HttpException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(bind.root, ex.message ?: "Hiba lépett fel a hálózatban.", Snackbar.LENGTH_LONG).show()
                }
            }
            catch (ex: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(bind.root, "A kapcsolat túllépte az időkorlátot!", Snackbar.LENGTH_LONG).show()
                }
            }
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

            loadUrlImageIntoImageView(MapViewModel.gameModels.playerList[0].card.verso, context, bind.detailsRoot.ivPlayerWhoShows)

            (AnimatorInflater.loadAnimator(
                bind.detailsRoot.context,
                R.animator.appear
            ) as AnimatorSet).apply {
                setTarget(bind.detailsRoot.ivCross)
                start()
                doOnEnd {
                    bind.detailsRoot.ivCross.visibility = ImageView.VISIBLE
                    loadUrlImageIntoImageView(roomTokens[roomList.indexOf(suspect.room) * 2 + 1], context, bind.ivRoomToken)
                    loadUrlImageIntoImageView(toolTokens[toolList.indexOf(suspect.tool) * 2 + 1], context, bind.ivToolToken)
                    loadUrlImageIntoImageView(suspectTokens[suspectList.indexOf(suspect.suspect) * 2 + 1], context, bind.ivSuspectToken)
                }
            }
        }
    }

    private suspend fun processSuspect(suspect: Suspect) {
        var someoneShowedSomething = false
        var playerIdx = MapViewModel.gameModels.playerList.indexOf(
            MapViewModel.playerHandler.getPlayerById(suspect.playerId)
        )
        for (i in 0 until MapViewModel.gameModels.playerList.size - 1) {
            playerIdx--
            if (playerIdx < 0)
                playerIdx = MapViewModel.gameModels.playerList.lastIndex
            loadUrlImageIntoImageView(MapViewModel.gameModels.playerList[playerIdx].card.imageRes, context, bind.ivPlayerWhoShows)
            if (playerIdx == MapViewModel.gameModels.playerList.indexOf(MapViewModel.player)) {
                val cards =
                    MapViewModel.cardHandler.revealMysteryCards(
                        playerIdx,
                        suspect.room,
                        suspect.tool,
                        suspect.suspect
                    )
                if (cards != null) {
                    loadUrlImageIntoImageView(MapViewModel.player.card.imageRes, context, bind.ivPlayerWhoShows)
                    bind.detailsRoot.btnOk.isEnabled = false

                    showCaution(cards)
                    someoneShowedSomething = true
                }
                else {
                    showSkipBubble()
                    delay(6000)
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
                    val revealedCard =
                        (MapViewModel.gameModels.playerList[playerIdx] as ThinkingPlayer).revealCardToPlayer(
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
                else {
                    showSkipBubble()
                    delay(6000)
                }
            }
            if (someoneShowedSomething)
                break
        }
        if (!someoneShowedSomething) {
            processNobodyShowedCard()
        }

        if (MapViewModel.playerInTurn == MapViewModel.mPlayerId)
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

        cards.forEach { card ->
            if (roomList.contains(card.name))
                showCard(
                    card,
                    roomTokens,
                    bind.ivRoomToken,
                    roomList,
                    bind.detailsRoot.tvShowCard
                )
            else
                loadUrlImageIntoImageView(roomTokens[roomList.indexOf(suspect.room) * 2 + 1], context, bind.ivRoomToken)

            if (toolList.contains(card.name))
                showCard(
                    card,
                    toolTokens,
                    bind.ivToolToken,
                    toolList,
                    bind.detailsRoot.tvShowCard
                )
            else
                loadUrlImageIntoImageView(toolTokens[toolList.indexOf(suspect.tool) * 2 + 1], context, bind.ivToolToken)

            if (suspectList.contains(card.name))
                showCard(
                    card,
                    suspectTokens,
                    bind.ivSuspectToken,
                    suspectList,
                    bind.detailsRoot.tvShowCard
                )
            else
                loadUrlImageIntoImageView(suspectTokens[suspectList.indexOf(suspect.suspect) * 2 + 1], context, bind.ivSuspectToken)
        }
    }

    private fun showCard(
        card: MysteryCard,
        tokenList: List<String>,
        ivToken: ImageView,
        nameList: Array<String>,
        cautionTextView: TextView
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val res = tokenList[nameList.indexOf(card.name) * 2]
            val bwRes = tokenList[nameList.indexOf(card.name) * 2 + 1]
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
        bwTokenList1: List<String>,
        bwTokenList2: List<String>,
        bwTargetTokenList: List<String>,
        targetTokenList: List<String>,
        nameList1: Array<String>,
        nameList2: Array<String>,
        targetNameList: Array<String>,
        name1: String,
        name2: String,
        targetName: String
    ) {
        loadUrlImageIntoImageView(bwTokenList1[nameList1.indexOf(name1)], context, ivToken1)
        loadUrlImageIntoImageView(bwTokenList2[nameList2.indexOf(name2)], context, ivToken2)
        val bwToken = bwTargetTokenList[targetNameList.indexOf(targetName)]
        val token = targetTokenList[targetNameList.indexOf(targetName)]

        GlobalScope.launch(Dispatchers.Main) {
            blinkToken(ivTargetToken, bwToken, token, 3, 200)
        }
    }

    private fun floatCard(revealedCard: MysteryCard) {
        val playerWhoSuspectsLayoutParams =
            bind.ivPlayerWhoSuspects.layoutParams as ConstraintLayout.LayoutParams
        val playerWhoShowsLayoutParams =
            bind.ivPlayerWhoShows.layoutParams as ConstraintLayout.LayoutParams
        val distance =
            (screenWidth - playerWhoShowsLayoutParams.marginEnd - playerWhoShowsLayoutParams.matchConstraintPercentWidth * screenWidth) - (playerWhoSuspectsLayoutParams.marginStart + playerWhoSuspectsLayoutParams.matchConstraintPercentWidth * screenWidth)

        val miniCardWidth = (bind.detailsRoot.ivFloatingCard.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth * screenWidth
        bind.detailsRoot.ivFloatingCard.translationX = miniCardWidth / 2

        (AnimatorInflater.loadAnimator(
            bind.detailsRoot.context,
            R.animator.appear
        ) as AnimatorSet).apply {
            setTarget(bind.detailsRoot.ivFloatingCard)
            bind.detailsRoot.ivFloatingCard.visibility = ImageView.VISIBLE
            start()
            doOnEnd {
                ObjectAnimator.ofFloat(
                    bind.detailsRoot.ivFloatingCard,
                    "translationX",
                    bind.detailsRoot.ivFloatingCard.translationX,
                    bind.detailsRoot.ivFloatingCard.translationX - distance
                ).apply {
                    duration = 4000
                    startDelay = 1000
                    start()
                    ObjectAnimator.ofFloat(
                        bind.detailsRoot.ivFloatingCard,
                        "translationY",
                        bind.detailsRoot.ivFloatingCard.translationY,
                        bind.detailsRoot.ivFloatingCard.translationY - 100f
                    ).apply {
                        duration = 2000
                        start()
                        doOnEnd {
                            ObjectAnimator.ofFloat(
                                bind.detailsRoot.ivFloatingCard,
                                "translationY",
                                bind.detailsRoot.ivFloatingCard.translationY,
                                bind.detailsRoot.ivFloatingCard.translationY + 100f
                            ).apply {
                                duration = 2000
                                start()
                            }
                        }
                    }
                    doOnEnd {
                        if (playerShowedCard)
                            loadUrlImageIntoImageView(revealedCard.imageRes, context, bind.detailsRoot.ivFloatingCard)

                        (AnimatorInflater.loadAnimator(
                            bind.detailsRoot.context,
                            R.animator.disappear
                        ) as AnimatorSet).apply {
                            setTarget(bind.detailsRoot.ivFloatingCard)
                            startDelay = 250
                            start()
                            doOnEnd {
                                bind.detailsRoot.ivFloatingCard.visibility = ImageView.GONE

                                if (MapViewModel.playerInTurn == MapViewModel.mPlayerId) {
                                    when {
                                        toolList.contains(revealedCard.name) -> {
                                            watchCard(
                                                bind.ivRoomToken,
                                                bind.ivSuspectToken,
                                                bind.ivToolToken,
                                                roomTokens.filter { token -> roomTokens.indexOf(token) % 2 == 1 },
                                                suspectTokens.filter { token -> suspectTokens.indexOf(token) % 2 == 1 },
                                                toolTokens.filter { token -> toolTokens.indexOf(token) % 2 == 1 },
                                                toolTokens.filter { token -> toolTokens.indexOf(token) % 2 == 0 },
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
                                                toolTokens.filter { token -> toolTokens.indexOf(token) % 2 == 1 },
                                                suspectTokens.filter { token -> suspectTokens.indexOf(token) % 2 == 1 },
                                                roomTokens.filter { token -> roomTokens.indexOf(token) % 2 == 1 },
                                                roomTokens.filter { token -> roomTokens.indexOf(token) % 2 == 0 },
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
                                                toolTokens.filter { token -> toolTokens.indexOf(token) % 2 == 1 },
                                                roomTokens.filter { token -> roomTokens.indexOf(token) % 2 == 1 },
                                                suspectTokens.filter { token -> suspectTokens.indexOf(token) % 2 == 1 },
                                                suspectTokens.filter { token -> suspectTokens.indexOf(token) % 2 == 0 },
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
    }

    private suspend fun blinkToken(
        token: ImageView,
        bwRes: String,
        res: String,
        repeat: Int,
        delayTimeMillis: Long
    ) {
        for (x in 1..repeat) {
            loadUrlImageIntoImageView(bwRes, context, token)
            delay(delayTimeMillis)
            loadUrlImageIntoImageView(res, context, token)
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
            try {
                retrofit.cluedo.skipCardReveal(MapViewModel.channelName, MapViewModel.mPlayerId!!)
                withContext(Dispatchers.Main) {
                    bind.detailsRoot.btnSkip.apply {
                        isEnabled = false
                        visibility = Button.GONE
                    }
                }
            }
            catch (ex: HttpException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(bind.root, ex.message ?: "Hiba lépett fel a hálózatban.", Snackbar.LENGTH_LONG).show()
                }
            }
            catch (ex: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(bind.root, "A kapcsolat túllépte az időkorlátot!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}