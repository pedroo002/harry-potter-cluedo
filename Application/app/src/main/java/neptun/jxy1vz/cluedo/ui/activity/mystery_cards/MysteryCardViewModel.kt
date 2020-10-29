package neptun.jxy1vz.cluedo.ui.activity.mystery_cards

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.database.CluedoDatabase
import neptun.jxy1vz.cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.card.MysteryCard
import neptun.jxy1vz.cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.cluedo.domain.util.debugPrint
import neptun.jxy1vz.cluedo.domain.util.toDomainModel
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.message.mystery_card.MysteryCardMessageBody
import neptun.jxy1vz.cluedo.network.model.message.mystery_card.MysteryCardPlayerPair
import neptun.jxy1vz.cluedo.network.model.message.mystery_card.MysteryCardsMessage
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.activity.map.MapActivity
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.CardFragment
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.adapter.CardPagerAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class MysteryCardViewModel(
    private val gameModel: GameModels,
    private val context: Context,
    private val playerId: Int,
    private val bind: ActivityMysteryCardBinding,
    private val fm: FragmentManager
) : BaseObservable() {

    private lateinit var player: Player
    private lateinit var adapter: CardPagerAdapter

    private val gamePref = context.getSharedPreferences(
        context.resources.getString(R.string.game_params_pref),
        Context.MODE_PRIVATE
    )
    private val playerPref = context.getSharedPreferences(
        context.resources.getString(R.string.player_data_pref),
        Context.MODE_PRIVATE
    )

    private var playerName = ""

    private val gameModeList = context.resources.getStringArray(R.array.playmodes)
    private val gameMode =
        gamePref.getString(context.resources.getString(R.string.play_mode_key), gameModeList[0])

    private lateinit var db: CluedoDatabase

    private lateinit var playerList: List<Player>

    private var retrofit: RetrofitInstance? = null
    private var pusherChannel: String? = null
    private var playersToWait: Int? = null

    private var cardsArrived = false

    private lateinit var cardPairsRequestBody: RequestBody

    init {
        bind.btnGo.isEnabled = false
        playerName = playerPref.getString(context.resources.getString(R.string.player_name_key), "")!!
        GlobalScope.launch(Dispatchers.IO) {
            db = CluedoDatabase.getInstance(context)

            playerList = when (gameMode) {
                gameModeList[0] -> gameModel.loadPlayers()
                else -> db.playerDao().getPlayers()?.map { dbModel ->
                    gameModel.loadPlayers().find { player -> player.id == dbModel.playerId }!!
                }!!
            }

            player = playerList.find { p -> p.id == playerId }!!

            if (gameMode == gameModeList[1]) {
                retrofit = RetrofitInstance.getInstance(context)
                val channelId =
                    playerPref.getString(context.resources.getString(R.string.channel_id_key), "")!!
                pusherChannel = "presence-${retrofit!!.cluedo.getChannel(channelId)!!.channelName}"

                val pusher = PusherInstance.getInstance()

                playersToWait = playerList.size

                withContext(Dispatchers.Main) {
                    if (!playerPref.getBoolean(context.resources.getString(R.string.is_host_key), false)) {
                        bind.refreshLayoutRoot.setOnRefreshListener {
                            sendRequest()
                        }

                        pusher.getPresenceChannel(pusherChannel)
                            .bind("mystery-card-pairs", object : PresenceChannelEventListener {
                                override fun onEvent(
                                    channelName: String?,
                                    eventName: String?,
                                    message: String?
                                ) {
                                    if (cardsArrived)
                                        return
                                    val messageJson =
                                        retrofit!!.moshi.adapter(MysteryCardsMessage::class.java)
                                            .fromJson(message!!)!!
                                    convertJsonToPairsAndLoadMysteryCards(messageJson)
                                    cardsArrived = true
                                }

                                override fun onSubscriptionSucceeded(p0: String?) {}
                                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                                override fun userSubscribed(p0: String?, p1: User?) {}
                                override fun userUnsubscribed(p0: String?, p1: User?) {}
                            })
                    }
                    else {
                        bind.refreshLayoutRoot.isEnabled = false

                        pusher.getPresenceChannel(pusherChannel).bind("fetch-cards", object : PresenceChannelEventListener {
                            override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                                sendPairsToClients()
                            }

                            override fun onSubscriptionSucceeded(p0: String?) {}
                            override fun onAuthenticationFailure(p0: String?, p1: java.lang.Exception?) {}
                            override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                            override fun userSubscribed(p0: String?, p1: User?) {}
                            override fun userUnsubscribed(p0: String?, p1: User?) {}
                        })

                        handOutCardsToPlayers()
                    }

                    pusher.getPresenceChannel(pusherChannel).bind("ready-to-game", object : PresenceChannelEventListener {
                        override fun onEvent(channelName: String?, eventName: String?, playerName: String?) {
                            if (playerName != this@MysteryCardViewModel.playerName)
                                Snackbar.make(bind.cardImages, "$playerName készen áll!", Snackbar.LENGTH_LONG).show()

                            playersToWait = playersToWait!! - 1
                            if (playersToWait!! == 0) {
                                makeMapIntent()
                            }
                        }

                        override fun onSubscriptionSucceeded(p0: String?) {}
                        override fun onAuthenticationFailure(p0: String?, p1: java.lang.Exception?) {}
                        override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                        override fun userSubscribed(p0: String?, p1: User?) {}
                        override fun userUnsubscribed(p0: String?, p1: User?) {}
                    })
                }
            }
            else
                handOutCardsToPlayers()
        }
    }

    private fun sendRequest() {
        GlobalScope.launch(Dispatchers.IO) {
            retrofit!!.cluedo.sendCardRequestToHost(pusherChannel!!)
            withContext(Dispatchers.Main) {
                bind.refreshLayoutRoot.isRefreshing = false
            }
        }
    }

    fun openHogwarts() {
        when (gameMode) {
            gameModeList[0] -> {
                makeMapIntent()
            }
            gameModeList[1] -> {
                GlobalScope.launch(Dispatchers.IO) {
                    RetrofitInstance.getInstance(context).cluedo.readyToLoadMap(pusherChannel!!, playerName)
                }
            }
        }
    }

    private fun makeMapIntent() {
        val mapIntent = Intent(context, MapActivity::class.java)
        mapIntent.putExtra(context.getString(R.string.player_id), player.id)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mapIntent)
    }

    private suspend fun getMysteryCards(playerIds: List<Int>) {
        when (gameMode) {
            gameModeList[0] -> {
                val cards = gameModel.db.getMysteryCardsForPlayers(playerIds)
                withContext(Dispatchers.Main) {
                    loadMysteryCards(cards)
                }
            }
            else -> {
                if (playerPref.getBoolean(
                        context.resources.getString(R.string.is_host_key),
                        false
                    )
                ) {
                    val cards = gameModel.db.getMysteryCardsForPlayers(playerIds)
                    val queryPairs = MysteryCardsMessage(MysteryCardMessageBody(cards.map { pair ->
                        MysteryCardPlayerPair(
                            pair.first.name,
                            pair.second
                        )
                    }))
                    val moshiJson =
                        retrofit!!.moshi.adapter(MysteryCardsMessage::class.java).toJson(queryPairs)
                    val body =
                        moshiJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                    cardPairsRequestBody = body

                    sendPairsToClients()

                    withContext(Dispatchers.Main) {
                        loadMysteryCards(cards)
                    }
                }
            }
        }
    }

    private fun sendPairsToClients() {
        GlobalScope.launch(Dispatchers.IO) {
            retrofit!!.cluedo.sendMysteryCardPairs(pusherChannel!!, cardPairsRequestBody)
        }
    }

    private fun convertJsonToPairsAndLoadMysteryCards(message: MysteryCardsMessage) {
        GlobalScope.launch(Dispatchers.IO) {
            val cards: ArrayList<Pair<MysteryCard, Int>> = ArrayList()

            cards.addAll(message.message.pairs.map { pair ->
                Pair(
                    db.cardDao().getCardByName(pair.cardName)?.toDomainModel() as MysteryCard,
                    pair.ownerPlayerId
                )
            })
            withContext(Dispatchers.Main) {
                loadMysteryCards(cards)
            }
        }
    }

    private fun loadMysteryCards(cards: List<Pair<MysteryCard, Int>>) {
        val fragmentList = ArrayList<CardFragment>()
        for (card in cards) {
            if (card.second == playerId)
                fragmentList.add(
                    CardFragment(
                        card.first.imageRes
                    )
                )
        }
        (AnimatorInflater.loadAnimator(context, R.animator.disappear) as AnimatorSet).apply {
            setTarget(bind.ivLoadingScreen)
            start()
            doOnEnd {
                bind.ivLoadingScreen.visibility = ImageView.GONE

                adapter = CardPagerAdapter(fm, fragmentList)
                bind.cardPager.adapter = adapter
                bind.btnGo.isEnabled = true
                Toast.makeText(
                    context,
                    context.getString(R.string.slide_for_more),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private suspend fun handOutCardsToPlayers() {
        val idList = ArrayList<Int>()
        idList.add(-1)

        when (gameMode) {
            gameModeList[0] -> {
                idList.add(playerId)
                var playerCount =
                    gamePref.getInt(context.getString(R.string.player_count_key), 0) - 1
                for (p in playerList) {
                    if (p.id != player.id && playerCount > 0) {
                        idList.add(p.id)
                        playerCount--
                    }
                }
            }
            gameModeList[1] -> {
                idList.addAll(playerList.map { p -> p.id })
            }
        }

        getMysteryCards(idList)
    }
}