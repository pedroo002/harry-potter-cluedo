package neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi

import android.content.Context
import android.graphics.Color
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.snackbar.Snackbar
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.android.synthetic.main.fragment_multiplayer_character_selector.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentMultiplayerCharacterSelectorBinding
import neptun.jxy1vz.cluedo.domain.util.debugPrint
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.message.presence.PlayerPresenceMessage
import neptun.jxy1vz.cluedo.network.model.message.selection.CharacterSelectionMessage
import neptun.jxy1vz.cluedo.network.model.message.submit.CharacterSubmitMessage
import neptun.jxy1vz.cluedo.network.model.player.PlayerDomainModel
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.adapter.PlayerListAdapter
import retrofit2.HttpException
import java.lang.Exception

class MultiplayerCharacterSelectorViewModel(
    private val bind: FragmentMultiplayerCharacterSelectorBinding,
    private val context: Context,
    private val isHost: Boolean,
    private val lateArrival: Boolean,
    private val lifecycle: LifecycleCoroutineScope,
    private val viewModelListener: ViewModelListener,
    private val channelListener: ChannelListener
) : BaseObservable(), PlayerListAdapter.AdapterListener {

    interface ChannelListener {
        fun onChannelRemoved()
    }

    private val retrofit: RetrofitInstance = RetrofitInstance.getInstance(context)
    private val pusher = PusherInstance.getInstance()
    private val playerPref = context.getSharedPreferences(context.resources.getString(R.string.player_data_pref), Context.MODE_PRIVATE)
    private val gamePref = context.getSharedPreferences(context.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE)
    val playerName: String = playerPref.getString(context.resources.getString(R.string.player_name_key), "")!!
    val channelId: String = playerPref.getString(context.resources.getString(R.string.channel_id_key), "")!!
    private val playerCount = gamePref.getInt(context.resources.getString(R.string.player_count_key), 3)
    var playerId = -1
    private lateinit var adapter: PlayerListAdapter
    lateinit var channelName: String
    private var isReady = false

    private val playersWhoSelected = ArrayList<String>()
    private val readyPlayers = ArrayList<PlayerDomainModel>()

    init {
        val editor = playerPref.edit()
        editor.putBoolean(context.resources.getString(R.string.is_host_key), isHost)
        editor.apply()

        val subscribedPlayers = ArrayList<String>()
        lifecycle.launch(Dispatchers.IO) {
            val channelId = context.getSharedPreferences(
                context.resources.getString(R.string.player_data_pref),
                Context.MODE_PRIVATE
            ).getString(context.resources.getString(R.string.channel_id_key), "")!!

            try {
                if (isHost) {
                    retrofit.cluedo.stopChannelWaiting(channelId)
                }

                val channel = retrofit.cluedo.getChannel(channelId)
                subscribedPlayers.addAll(channel!!.subscribedUsers)
                channelName = "presence-${channel.channelName}"

                val playersOfChannel = ArrayList<PlayerDomainModel>()
                playersOfChannel.addAll(
                    retrofit.cluedo.getPlayers()
                        ?.filter { playerApiModel -> subscribedPlayers.contains(playerApiModel.name) }
                        ?.map { playerApiModel -> PlayerDomainModel(playerApiModel.name, "", -1) }
                        ?.toList()!!
                )

                withContext(Dispatchers.Main) {
                    adapter = PlayerListAdapter(
                        playersOfChannel,
                        playerName,
                        this@MultiplayerCharacterSelectorViewModel
                    )
                    bind.rvPlayerList.adapter = adapter

                    pusher.getPresenceChannel(channelName)
                        .bind("character-selected", object : PresenceChannelEventListener {
                            override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                                val messageJson = retrofit.moshi.adapter(CharacterSelectionMessage::class.java).fromJson(message!!)!!

                                if (!playersWhoSelected.contains(messageJson.message.playerName))
                                    playersWhoSelected.add(messageJson.message.playerName)

                                if (playersWhoSelected.size == playerCount)
                                    enableButton()

                                if (messageJson.message.playerName == playerName)
                                    return
                                triggerUpdate(messageJson)
                            }

                            override fun onSubscriptionSucceeded(p0: String?) {}
                            override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                            override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                            override fun userSubscribed(p0: String?, p1: User?) {}
                            override fun userUnsubscribed(p0: String?, p1: User?) {}
                        })

                    pusher.getPresenceChannel(channelName).bind("character-submit", object : PresenceChannelEventListener {
                        override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                            val messageJson = retrofit.moshi.adapter(CharacterSubmitMessage::class.java).fromJson(message!!)!!
                            adapter.setCharacterTextColor(messageJson.message.playerName, Color.GREEN)
                            readyPlayers.add(adapter.getPlayer(messageJson.message.playerName))

                            if (readyPlayers.size == playerCount)
                                viewModelListener.onFinish()
                        }

                        override fun onSubscriptionSucceeded(p0: String?) {}
                        override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                        override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                        override fun userSubscribed(p0: String?, p1: User?) {}
                        override fun userUnsubscribed(p0: String?, p1: User?) {}
                    })

                    pusher.getPresenceChannel(channelName).bind("player-leaves", object : PresenceChannelEventListener {
                        override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                            val messageJson = retrofit.moshi.adapter(PlayerPresenceMessage::class.java).fromJson(message!!)!!
                            if (messageJson.message.playerName != this@MultiplayerCharacterSelectorViewModel.playerName) {
                                adapter.deletePlayer(messageJson.message.playerName)
                                readyPlayers.removeAll { player -> player.playerName == messageJson.message.playerName }
                            }
                        }

                        override fun onSubscriptionSucceeded(p0: String?) {}
                        override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                        override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                        override fun userSubscribed(p0: String?, p1: User?) {}
                        override fun userUnsubscribed(p0: String?, p1: User?) {}
                    })

                    pusher.getPresenceChannel(channelName)
                        .bind("player-arrives", object : PresenceChannelEventListener {
                            override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                                val messageJson = retrofit.moshi.adapter(PlayerPresenceMessage::class.java).fromJson(message!!)!!
                                if (messageJson.message.playerName != this@MultiplayerCharacterSelectorViewModel.playerName) {
                                    adapter.addNewPlayer(messageJson.message.playerName)
                                }
                            }

                            override fun onSubscriptionSucceeded(p0: String?) {}
                            override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                            override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                            override fun userSubscribed(p0: String?, p1: User?) {}
                            override fun userUnsubscribed(p0: String?, p1: User?) {}
                        })

                    pusher.getPresenceChannel(channelName).bind("channel-removed-after-join", object : PresenceChannelEventListener {
                        override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                            channelName?.let {
                                channelListener.onChannelRemoved()
                            }
                        }

                        override fun onSubscriptionSucceeded(p0: String?) {}
                        override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                        override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                        override fun userSubscribed(p0: String?, p1: User?) {}
                        override fun userUnsubscribed(p0: String?, p1: User?) {}
                    })

                    if (lateArrival) {
                        withContext(Dispatchers.IO) {
                            retrofit.cluedo.notifyPlayerArrives(channelName, playerName)
                        }
                    }
                }
            }
            catch (ex: HttpException) {
                debugPrint("${ex.code()}: ${ex.message()}")
            }
        }
    }

    fun getReadyPlayers(): ArrayList<PlayerDomainModel> = readyPlayers

    fun ready() {
        if (adapter.areSelectionsDifferent()) {
            playerId = adapter.getCurrentPlayer().playerId
            isReady = true
            lifecycle.launch(Dispatchers.IO) {
                retrofit.cluedo.notifyCharacterSubmit(channelName, playerName)
                withContext(Dispatchers.Main) {
                    bind.multiCharacterSelectorRoot.btnReady.isEnabled = false
                }
            }
        }
        else
            Snackbar.make(bind.multiCharacterSelectorRoot, "Különböző karaktereket válasszatok!", Snackbar.LENGTH_LONG).show()
    }

    private fun triggerUpdate(messageJson: CharacterSelectionMessage) {
        lifecycle.launch(Dispatchers.Main) {
            adapter.updatePlayerSelection(messageJson.message.playerName, messageJson.message.characterName, messageJson.message.token)
        }
    }

    override fun onSelect(playerName: String, characterName: String, tokenSource: Int) {
        lifecycle.launch(Dispatchers.IO) {
            retrofit.cluedo.notifyCharacterSelected(channelName, playerName, characterName, tokenSource)
        }
    }

    override fun catchUp(playerList: ArrayList<PlayerDomainModel>) {
        lifecycle.launch(Dispatchers.IO) {
            playerList.find { player -> player.playerName == playerName }!!.apply {
                if (selectedCharacter.isNotEmpty())
                    retrofit.cluedo.notifyCharacterSelected(channelName, playerName, selectedCharacter, adapter.getTokenFromCharacterName(selectedCharacter))
                if(isReady)
                    retrofit.cluedo.notifyCharacterSubmit(channelName, playerName)
            }
        }
    }

    private fun enableButton() {
        lifecycle.launch(Dispatchers.Main) {
            bind.btnReady.isEnabled = true
        }
    }
}