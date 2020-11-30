package neptun.jxy1vz.hp_cluedo.ui.fragment.cards.dark

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.core.animation.doOnEnd
import androidx.core.view.marginBottom
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.android.synthetic.main.fragment_dark_card.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.FragmentDarkCardBinding
import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.domain.model.card.*
import neptun.jxy1vz.hp_cluedo.domain.model.helper.getHelperObjects
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.domain.util.removePlayer
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.data.network.model.message.CardEventMessage
import neptun.jxy1vz.hp_cluedo.data.network.pusher.PusherInstance
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.hp_cluedo.ui.fragment.cards.card_loss.CardLossFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.player_dies.PlayerDiesOrLeavesFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.user_dies.UserDiesFragment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class DarkCardViewModel(
    private val bind: FragmentDarkCardBinding,
    private val context: Context,
    private val playerList: List<Player>,
    playerIds: List<Int>,
    card: DarkCard,
    private val listener: ViewModelListener,
    private val fm: FragmentManager
) : BaseObservable(), CardLossFragment.ThrowCardListener {

    private val safePlayerIcons = HashMap<String, String>()
    private val playerIcons = HashMap<String, String>()
    private var radius = 0

    private val thrownCards = HashMap<String, String>()

    private var waitForPlayers = playerList.size

    init {
        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).assetDao().apply {
                val darkCardFragmentImages = getAssetsByPrefix(AssetPrefixes.DARK_CARD_FRAGMENT.string())!!.map { assetDBmodel -> assetDBmodel.url }
                val darkMark = darkCardFragmentImages[0]
                val safeIcons = darkCardFragmentImages.filter { url -> url.contains("safe") }
                val unsafeIcons = darkCardFragmentImages.filter { url -> !url.contains("safe") && !url.contains("dark_mark") }
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(darkMark, context, bind.ivDarkMark)
                    val playerNameList = context.resources.getStringArray(R.array.characters)
                    playerNameList.forEach { playerName ->
                        safePlayerIcons[playerName] = safeIcons[playerNameList.indexOf(playerName)]
                        playerIcons[playerName] = unsafeIcons[playerNameList.indexOf(playerName)]
                    }

                    radius = (context.resources.displayMetrics.heightPixels - (bind.darkCardRoot.btnClose.height + bind.darkCardRoot.btnClose.marginBottom)) / 4

                    playerList.forEach { player ->
                        val i = playerList.indexOf(player)

                        val imgRes = if (playerIds.contains(player.id)) {
                            val helperObjects = getHelperObjects(player, card)

                            if (helperObjects.isEmpty()) {
                                getLoss(player, card)
                                playerIcons[player.card.name]!!
                            } else {
                                helperObjects.forEach { tool ->
                                    player.helperCards!!.filter { it.name == tool }.forEach { helperCard ->
                                        helperCard.numberOfHelpingCases++
                                    }
                                }
                                safePlayerIcons[player.card.name]!!
                            }
                        } else
                            safePlayerIcons[player.card.name]!!

                        var lossTextView: TextView? = null
                        if (card.lossType == LossType.HP && playerIds.contains(player.id) && imgRes != safePlayerIcons[player.card.name]!!) {
                            val lossString = "-${card.hpLoss} HP"
                            lossTextView = TextView(bind.darkCardRoot.context)
                            lossTextView.text = lossString
                            lossTextView.setTextColor(Color.RED)
                            lossTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                            lossTextView.setTypeface(null, Typeface.BOLD)
                        }

                        var thrownCard: ImageView? = null
                        if (thrownCards.containsKey(player.card.name)) {
                            thrownCard = ImageView(bind.darkCardRoot.context)
                            loadUrlImageIntoImageView(thrownCards[player.card.name]!!, context, thrownCard)
                        }

                        drawImage(
                            imgRes,
                            radius * sin(i * (2 * PI / playerList.size)),
                            radius * cos(i * (2 * PI / playerList.size)),
                            lossTextView,
                            thrownCard,
                            player
                        )
                    }

                    if (MapViewModel.isGameModeMulti()) {
                        subscribeToEvents()
                    }
                }
            }
        }
    }

    private fun subscribeToEvents() {
        PusherInstance.getInstance().getPresenceChannel(MapViewModel.channelName).apply {
            bind("dark-cards-ready", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    waitForPlayers--
                    if (waitForPlayers == 0)
                        sendCloseSignal()
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("dark-cards-close", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    gotCloseSignal()
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("helper-card-thrown", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    val messageJson = RetrofitInstance.getInstance(context).moshi.adapter(
                        CardEventMessage::class.java).fromJson(message!!)!!
                    if (messageJson.playerId == MapViewModel.mPlayerId)
                        return
                    val player = MapViewModel.playerHandler.getPlayerById(messageJson.playerId)
                    val cardToRemove = player.helperCards!!.find { card -> card.name == messageJson.cardName }!!
                    val thrownCard = ImageView(bind.darkCardRoot.context)
                    loadUrlImageIntoImageView(cardToRemove.imageRes, context, thrownCard)
                    processCardThrownEvent(playerIcons[player.card.name]!!, thrownCard, player)
                    MapViewModel.playerHandler.getPlayerById(messageJson.playerId).helperCards!!.remove(cardToRemove)
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })
        }
    }

    private fun sendCloseSignal() {
        GlobalScope.launch(Dispatchers.IO) {
            RetrofitInstance.getInstance(context).cluedo.notifyDarkCardsClose(MapViewModel.channelName)
        }
    }

    private fun sendReadySignal() {
        GlobalScope.launch(Dispatchers.IO) {
            RetrofitInstance.getInstance(context).cluedo.notifyDarkCardsReady(MapViewModel.channelName)
        }
    }

    private fun gotCloseSignal() {
        GlobalScope.launch(Dispatchers.Main) {
            listener.onFinish()
        }
    }

    private fun processCardThrownEvent(imgRes: String, thrownCard: ImageView, player: Player) {
        GlobalScope.launch(Dispatchers.Main) {
            val i = playerList.indexOf(player)
            drawImage(
                imgRes,
                radius * sin(i * (2 * PI / playerList.size)),
                radius * cos(i * (2 * PI / playerList.size)),
                null,
                thrownCard,
                player
            )
        }
    }

    private fun drawImage(
        imgRes: String,
        tranX: Double,
        tranY: Double,
        lossTextView: TextView?,
        thrownCard: ImageView?,
        player: Player
    ) {
        val layoutParams = ConstraintLayout.LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT)
        layoutParams.matchConstraintPercentWidth = 0.2f
        layoutParams.matchConstraintPercentHeight = 0.2f
        layoutParams.topToTop = bind.darkCardRoot.ivDarkMark.id
        layoutParams.bottomToBottom = bind.darkCardRoot.ivDarkMark.id
        layoutParams.startToStart = bind.darkCardRoot.ivDarkMark.id
        layoutParams.endToEnd = bind.darkCardRoot.ivDarkMark.id
        val image = ImageView(bind.darkCardRoot.context)
        loadUrlImageIntoImageView(imgRes, context, image)
        image.visibility = ImageView.VISIBLE
        image.layoutParams = layoutParams
        image.translationX = tranX.toFloat() - image.width / 2
        image.translationY = tranY.toFloat() - image.height / 2
        bind.darkCardRoot.addView(image)

        lossTextView?.let {
            val textLayoutParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            textLayoutParams.topToTop = bind.darkCardRoot.ivDarkMark.id
            textLayoutParams.bottomToBottom = bind.darkCardRoot.ivDarkMark.id
            textLayoutParams.startToStart = bind.darkCardRoot.ivDarkMark.id
            textLayoutParams.endToEnd = bind.darkCardRoot.ivDarkMark.id
            textLayoutParams.marginStart = 5
            textLayoutParams.bottomMargin = 10
            lossTextView.layoutParams = textLayoutParams
            val imgW = (image.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth * context.resources.displayMetrics.widthPixels
            val imgH = (image.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight * context.resources.displayMetrics.heightPixels
            lossTextView.translationX = image.translationX + imgW / 3
            lossTextView.translationY = image.translationY - imgH / 3
            lossTextView.visibility = TextView.VISIBLE
            bind.darkCardRoot.addView(lossTextView)

            (AnimatorInflater.loadAnimator(
                bind.darkCardRoot.context,
                R.animator.appear
            ) as AnimatorSet).apply {
                setTarget(lossTextView)
                start()
                ObjectAnimator.ofFloat(
                    lossTextView,
                    "translationY",
                    lossTextView.translationY,
                    lossTextView.translationY - 30f
                )
                    .apply {
                        duration = 2000
                        start()
                        doOnEnd {
                            (AnimatorInflater.loadAnimator(
                                bind.darkCardRoot.context,
                                R.animator.disappear
                            ) as AnimatorSet).apply {
                                setTarget(lossTextView)
                                start()
                                doOnEnd {
                                    lossTextView.visibility = TextView.GONE
                                    bind.darkCardRoot.removeView(lossTextView)
                                }
                            }
                            if (player.hp <= 0) {
                                (AnimatorInflater.loadAnimator(
                                    bind.darkCardRoot.context,
                                    R.animator.disappear
                                ) as AnimatorSet).apply {
                                    setTarget(image)
                                    start()
                                    doOnEnd {
                                        image.visibility = ImageView.GONE
                                        bind.darkCardRoot.removeView(image)

                                        if (player.id == MapViewModel.mPlayerId) {
                                            val fragment =
                                                UserDiesFragment.newInstance(player, MapViewModel.dialogHandler)
                                            MapViewModel.insertFragment(fragment, true)
                                        } else if (MapViewModel.player.hp > 0) {
                                            MapViewModel.isGameAbleToContinue = false
                                            if (MapViewModel.playerInTurn == player.id)
                                                MapViewModel.playerInTurnDied = true

                                            removePlayer(player)

                                            val title = "${player.card.name} ${context.resources.getString(R.string.he_lost_his_hps)}"
                                            val fragment = PlayerDiesOrLeavesFragment.newInstance(
                                                player,
                                                PlayerDiesOrLeavesFragment.ExitScenario.DEAD,
                                                MapViewModel.dialogHandler,
                                                title
                                            )
                                            MapViewModel.insertFragment(fragment, true)
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }

        thrownCard?.let {
            val cardLayoutParams = ConstraintLayout.LayoutParams(200, 400)
            cardLayoutParams.topToTop = bind.darkCardRoot.ivDarkMark.id
            cardLayoutParams.bottomToBottom = bind.darkCardRoot.ivDarkMark.id
            cardLayoutParams.startToStart = bind.darkCardRoot.ivDarkMark.id
            cardLayoutParams.endToEnd = bind.darkCardRoot.ivDarkMark.id
            cardLayoutParams.marginEnd = 300
            cardLayoutParams.bottomMargin = 250
            thrownCard.layoutParams = cardLayoutParams
            thrownCard.translationX = image.translationX
            thrownCard.translationY = image.translationY
            thrownCard.visibility = ImageView.VISIBLE
            bind.darkCardRoot.addView(thrownCard)

            (AnimatorInflater.loadAnimator(
                bind.darkCardRoot.context,
                R.animator.appear
            ) as AnimatorSet).apply {
                setTarget(lossTextView)
                start()
                ObjectAnimator.ofFloat(
                    thrownCard,
                    "translationY",
                    thrownCard.translationY,
                    thrownCard.translationY - 30f
                )
                    .apply {
                        duration = 2000
                        start()
                        doOnEnd {
                            (AnimatorInflater.loadAnimator(
                                bind.darkCardRoot.context,
                                R.animator.disappear
                            ) as AnimatorSet).apply {
                                setTarget(thrownCard)
                                start()
                                doOnEnd {
                                    thrownCard.visibility = ImageView.GONE
                                    bind.darkCardRoot.removeView(thrownCard)
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun getLoss(player: Player, card: DarkCard) {
        when (card.lossType) {
            LossType.HP -> {
                player.hp -= card.hpLoss
            }
            else -> {
                if (player.helperCards != null) {
                    val properHelperCards = getProperHelperCards(player, card.lossType)

                    if (properHelperCards.isNotEmpty()) {
                        if (player.id == MapViewModel.mPlayerId) {
                            val title = when (card.lossType) {
                                LossType.TOOL -> context.resources.getString(R.string.tool_loss)
                                LossType.SPELL -> context.resources.getString(R.string.spell_loss)
                                else -> context.resources.getString(R.string.ally_loss)
                            }
                            fm.beginTransaction().replace(R.id.cardLossFrame, CardLossFragment.newInstance(title, properHelperCards, this)).commit()
                            bind.darkCardRoot.btnClose.isEnabled = false
                        }
                        else if (!MapViewModel.isGameModeMulti()) {
                            val cardToThrow = chooseWisely(properHelperCards)
                            thrownCards[player.card.name] = cardToThrow.imageRes
                            MapViewModel.playerHandler.getPlayerById(player.id).helperCards!!.remove(cardToThrow)
                        }
                    }
                }
            }
        }
    }

    private fun getProperHelperCards(player: Player, lossType: LossType): List<HelperCard> {
        val properHelperCards: ArrayList<HelperCard> = ArrayList()
        player.helperCards!!.filter { (it.type as HelperType).compareTo(lossType) }.forEach { helperCard ->
            properHelperCards.add(helperCard)
        }
        return properHelperCards
    }

    private fun chooseWisely(cardList: List<HelperCard>): HelperCard {
        cardList.filter { countOccurrences(it, cardList) > 1 }.forEach { card ->
            return card
        }
        cardList.sortedBy {
            it.numberOfHelpingCases
        }
        return cardList[0]
    }

    private fun countOccurrences(card: HelperCard, list: List<HelperCard>): Int {
        var count = 0
        repeat(list.filter { c -> c.name == card.name }.size) {
            count++
        }
        return count
    }

    fun close() {
        if (MapViewModel.isGameModeMulti()) {
            sendReadySignal()
            bind.darkCardRoot.btnClose.isEnabled = false
        }
        else
            listener.onFinish()
    }

    override fun onThrow() {
        bind.darkCardRoot.btnClose.isEnabled = true
    }
}