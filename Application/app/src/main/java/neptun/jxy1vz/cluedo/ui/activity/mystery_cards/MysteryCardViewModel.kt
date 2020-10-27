package neptun.jxy1vz.cluedo.ui.activity.mystery_cards

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
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
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.concurrent.thread

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

    private val gameModeList = context.resources.getStringArray(R.array.playmodes)
    private val gameMode =
        gamePref.getString(context.resources.getString(R.string.play_mode_key), gameModeList[0])

    private lateinit var db: CluedoDatabase

    private lateinit var playerList: List<Player>

    private var retrofit: RetrofitInstance? = null
    private var pusherChannel: String? = null
    private var playersToWait: Int? = null

    init {
        bind.btnGo.isEnabled = false
        GlobalScope.launch(Dispatchers.IO) {
            db = CluedoDatabase.getInstance(context)

            db.playerDao().getPlayers()?.forEach {
                println("${it.playerId}: ${it.playerName}")
            }

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

                withContext(Dispatchers.Main) {
                    if (!playerPref.getBoolean(context.resources.getString(R.string.is_host_key), false)) {
                        PusherInstance.getInstance().getPresenceChannel(pusherChannel)
                            .bind("mystery-card-pairs", object : PresenceChannelEventListener {
                                override fun onEvent(
                                    channelName: String?,
                                    eventName: String?,
                                    message: String?
                                ) {
                                    debugPrint(message!!)

                                    val messageJson =
                                        retrofit!!.moshi.adapter(MysteryCardsMessage::class.java)
                                            .fromJson(message!!)!!
                                    convertJsonToPairsAndLoadMysteryCards(messageJson)
                                }

                                override fun onSubscriptionSucceeded(p0: String?) {}
                                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                                override fun userSubscribed(p0: String?, p1: User?) {}
                                override fun userUnsubscribed(p0: String?, p1: User?) {}
                            })
                    }
                }
            }
            handOutCardsToPlayers()
        }
    }

    fun openHogwarts() {
        when (gameMode) {
            gameModeList[0] -> {
                makeMapIntent()
            }
            gameModeList[1] -> {
                playersToWait = playerList.size
                GlobalScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        PusherInstance.getInstance().getPresenceChannel(pusherChannel).bind("ready-to-game", object : PresenceChannelEventListener {
                            override fun onEvent(channelName: String?, eventName: String?, message: String?) {
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
                    RetrofitInstance.getInstance(context).cluedo.readyToLoadMap(pusherChannel!!)
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

                    debugPrint(body.toString())

                    retrofit!!.cluedo.sendMysteryCardPairs(pusherChannel!!, body)
                    debugPrint(pusherChannel!!)

                    withContext(Dispatchers.Main) {
                        loadMysteryCards(cards)

                        (cards as ArrayList).sortBy { card -> card.second }
                        cards.forEach {
                            println("${it.second} --> ${it.first}")
                        }
                    }
                }
            }
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
                cards.sortBy { card -> card.second }
                cards.forEach {
                    println("${it.second} --> ${it.first}")
                }
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