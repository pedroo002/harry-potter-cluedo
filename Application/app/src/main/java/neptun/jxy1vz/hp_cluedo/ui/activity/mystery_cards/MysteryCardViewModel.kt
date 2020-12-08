package neptun.jxy1vz.hp_cluedo.ui.activity.mystery_cards

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
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
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.CardDBmodel
import neptun.jxy1vz.hp_cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.hp_cluedo.domain.model.ThinkingPlayer
import neptun.jxy1vz.hp_cluedo.domain.model.card.MysteryCard
import neptun.jxy1vz.hp_cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.hp_cluedo.domain.util.toDatabaseModel
import neptun.jxy1vz.hp_cluedo.domain.util.toDomainModel
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.data.network.model.message.MysteryCardMessageBody
import neptun.jxy1vz.hp_cluedo.data.network.model.message.MysteryCardPlayerPair
import neptun.jxy1vz.hp_cluedo.data.network.model.message.MysteryCardsMessage
import neptun.jxy1vz.hp_cluedo.data.network.pusher.PusherInstance
import neptun.jxy1vz.hp_cluedo.domain.model.BasePlayer
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapActivity
import neptun.jxy1vz.hp_cluedo.ui.fragment.card_pager.CardFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.card_pager.adapter.CardPagerAdapter
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

    private lateinit var player: BasePlayer
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

    private lateinit var playerList: List<BasePlayer>

    private lateinit var retrofit: RetrofitInstance
    private lateinit var apiChannelName: String
    private lateinit var pusherChannel: String
    private var playersToWait: Int? = null

    private var cardsArrived = false

    private lateinit var cardPairsRequestBody: RequestBody

    init {
        bind.btnGo.isEnabled = false
        playerName =
            playerPref.getString(context.resources.getString(R.string.player_name_key), "")!!
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
                apiChannelName = retrofit.cluedo.getChannel(channelId)!!.channelName
                pusherChannel = "presence-${apiChannelName}"

                val pusher = PusherInstance.getInstance()

                playersToWait = playerList.size

                withContext(Dispatchers.Main) {
                    if (!playerPref.getBoolean(
                            context.resources.getString(R.string.is_host_key),
                            false
                        )
                    ) {
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
                                        retrofit.moshi.adapter(MysteryCardsMessage::class.java)
                                            .fromJson(message!!)!!
                                    convertJsonToPairsAndLoadMysteryCards(messageJson)
                                    cardsArrived = true
                                }

                                override fun onSubscriptionSucceeded(p0: String?) {}
                                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                                override fun onUsersInformationReceived(
                                    p0: String?,
                                    p1: MutableSet<User>?
                                ) {
                                }

                                override fun userSubscribed(p0: String?, p1: User?) {}
                                override fun userUnsubscribed(p0: String?, p1: User?) {}
                            })

                        withContext(Dispatchers.IO) {
                            retrofit.cluedo.notifyMysteryCardsLoaded(apiChannelName)
                        }
                    } else {
                        pusher.getPresenceChannel(pusherChannel)
                            .bind("fetch-cards", object : PresenceChannelEventListener {
                                override fun onEvent(
                                    channelName: String?,
                                    eventName: String?,
                                    message: String?
                                ) {
                                    sendPairsToClients()
                                }

                                override fun onSubscriptionSucceeded(p0: String?) {}
                                override fun onAuthenticationFailure(
                                    p0: String?,
                                    p1: java.lang.Exception?
                                ) {
                                }

                                override fun onUsersInformationReceived(
                                    p0: String?,
                                    p1: MutableSet<User>?
                                ) {
                                }

                                override fun userSubscribed(p0: String?, p1: User?) {}
                                override fun userUnsubscribed(p0: String?, p1: User?) {}
                            })

                        handOutCardsToPlayers()
                    }

                    pusher.getPresenceChannel(pusherChannel)
                        .bind("ready-to-game", object : PresenceChannelEventListener {
                            override fun onEvent(
                                channelName: String?,
                                eventName: String?,
                                playerName: String?
                            ) {
                                if (playerName != this@MysteryCardViewModel.playerName)
                                    Snackbar.make(
                                        bind.cardImages,
                                        "$playerName készen áll!",
                                        Snackbar.LENGTH_LONG
                                    ).show()

                                playersToWait = playersToWait!! - 1
                                if (playersToWait!! == 0) {
                                    makeMapIntent()
                                }
                            }

                            override fun onSubscriptionSucceeded(p0: String?) {}
                            override fun onAuthenticationFailure(
                                p0: String?,
                                p1: java.lang.Exception?
                            ) {
                            }

                            override fun onUsersInformationReceived(
                                p0: String?,
                                p1: MutableSet<User>?
                            ) {
                            }

                            override fun userSubscribed(p0: String?, p1: User?) {}
                            override fun userUnsubscribed(p0: String?, p1: User?) {}
                        })
                }
            } else
                handOutCardsToPlayers()
        }
    }

    fun openHogwarts() {
        when (gameMode) {
            gameModeList[0] -> {
                makeMapIntent()
            }
            gameModeList[1] -> {
                GlobalScope.launch(Dispatchers.IO) {
                    RetrofitInstance.getInstance(context).cluedo.readyToLoadMap(
                        pusherChannel,
                        playerName
                    )
                    withContext(Dispatchers.Main) {
                        bind.btnGo.isEnabled = false
                    }
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
                        retrofit.moshi.adapter(MysteryCardsMessage::class.java).toJson(queryPairs)
                    val body =
                        moshiJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                    cardPairsRequestBody = body

                    retrofit.cluedo.notifyMysteryCardsLoaded(apiChannelName)

                    withContext(Dispatchers.Main) {
                        loadMysteryCards(cards)
                    }
                }
            }
        }
    }

    private fun sendPairsToClients() {
        GlobalScope.launch(Dispatchers.IO) {
            retrofit.cluedo.sendMysteryCardPairs(pusherChannel, cardPairsRequestBody)
        }
    }

    private fun convertJsonToPairsAndLoadMysteryCards(message: MysteryCardsMessage) {
        GlobalScope.launch(Dispatchers.IO) {
            val cards: ArrayList<Pair<MysteryCard, Int>> = ArrayList()

            gameModel.db.resetCards()

            cards.addAll(message.message.pairs.map { pair ->
                Pair(
                    db.cardDao().getCardByName(pair.cardName)?.toDomainModel(context) as MysteryCard,
                    pair.ownerPlayerId
                )
            })

            cards.map { card ->
                val cardTag = gameModel.db.interactor.getAssetByUrl(card.first.imageRes)!!.tag
                val versoTag = gameModel.db.interactor.getAssetByUrl(card.first.verso)!!.tag
                CardDBmodel(
                    card.first.id.toLong(),
                    card.first.name,
                    cardTag,
                    versoTag,
                    card.first.type.toDatabaseModel().toString(),
                    card.second
                )
            }.forEach {
                gameModel.db.interactor.updateCards(it)
            }

            withContext(Dispatchers.Main) {
                loadMysteryCards(cards)
            }
        }
    }

    private fun loadMysteryCards(cards: List<Pair<MysteryCard, Int>>) {
        val fragmentList = ArrayList<CardFragment>()
        cards.filter { card -> card.second == playerId }.forEach { card ->
            fragmentList.add(CardFragment.newInstance(card.first.imageRes))
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
                playerList.filter { p -> p.id != player.id }.forEach {
                    if (playerCount > 0) {
                        idList.add(it.id)
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