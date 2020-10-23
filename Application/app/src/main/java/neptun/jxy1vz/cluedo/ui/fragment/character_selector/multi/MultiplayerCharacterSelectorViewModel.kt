package neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi

import android.content.Context
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
    val playerName: String =
        context.getSharedPreferences(
            context.resources.getString(R.string.player_data_pref),
            Context.MODE_PRIVATE
        ).getString(context.resources.getString(R.string.player_name_key), "")!!
    val channelId: String = context.getSharedPreferences(
        context.resources.getString(R.string.player_data_pref),
        Context.MODE_PRIVATE
    ).getString(context.resources.getString(R.string.channel_id_key), "")!!
    var playerId = -1
    private lateinit var adapter: PlayerListAdapter
    lateinit var channelName: String
    private var waitFor = -1

    init {
        val subscribedPlayers = ArrayList<String>()
        lifecycle.launch(Dispatchers.IO) {
            val channelId = context.getSharedPreferences(
                context.resources.getString(R.string.player_data_pref),
                Context.MODE_PRIVATE
            ).getString(context.resources.getString(R.string.channel_id_key), "")!!

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
                waitFor = adapter.itemCount

                pusher.getPresenceChannel(channelName)
                    .bind("character-selected", object : PresenceChannelEventListener {
                        override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                            val messageJson = retrofit.moshi.adapter(CharacterSelectionMessage::class.java).fromJson(message!!)!!
                            if (messageJson.message.playerName == playerName)
                                return
                            debugPrint("${messageJson.message.playerName} selected ${messageJson.message.characterName}")
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
                        debugPrint("${messageJson.message.playerName} is ready")
                        waitFor--
                        debugPrint("Waiting for $waitFor players")
                        Snackbar.make(bind.multiCharacterSelectorRoot, "${messageJson.message.playerName} készen áll.", Snackbar.LENGTH_SHORT).show()
                        if (waitFor == 0)
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
                        debugPrint("${messageJson.message.playerName} leaves")
                        if (messageJson.message.playerName != this@MultiplayerCharacterSelectorViewModel.playerName) {
                            //Snackbar.make(bind.root.rootView, "${messageJson.message.playerName} lelépett.", Snackbar.LENGTH_SHORT).show()
                            adapter.deletePlayer(messageJson.message.playerName)
                        }
                    }

                    override fun onSubscriptionSucceeded(p0: String?) {}
                    override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                    override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                    override fun userSubscribed(p0: String?, p1: User?) {}
                    override fun userUnsubscribed(p0: String?, p1: User?) {}
                })

                pusher.getPresenceChannel(channelName).bind("player-arrives", object : PresenceChannelEventListener {
                    override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                        val messageJson = retrofit.moshi.adapter(PlayerPresenceMessage::class.java).fromJson(message!!)!!
                        debugPrint("${messageJson.message.playerName} arrives")
                        if (messageJson.message.playerName != this@MultiplayerCharacterSelectorViewModel.playerName) {
                            //Snackbar.make(bind.root.rootView, "${messageJson.message.playerName} belépett.", Snackbar.LENGTH_SHORT).show()
                            adapter.addNewPlayer(messageJson.message.playerName)
                        }
                    }

                    override fun onSubscriptionSucceeded(p0: String?) {}
                    override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                    override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                    override fun userSubscribed(p0: String?, p1: User?) {}
                    override fun userUnsubscribed(p0: String?, p1: User?) {}
                })
            }

            pusher.getPresenceChannel(channelName).bind("channel-removed-after-join", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    debugPrint("Channel '$channelName' removed")
                    channelName?.let {
                        //Snackbar.make(bind.multiCharacterSelectorRoot, "Megszűnt a szerver.", Snackbar.LENGTH_SHORT).show()
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

    fun ready() {
        if (adapter.areSelectionsDifferent()) {
            playerId = adapter.getCurrentPlayer().playerId
            debugPrint("Player '$playerName' is ready.")
            lifecycle.launch(Dispatchers.IO) {
                retrofit.cluedo.notifyCharacterSubmit(channelName, playerName)
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
        bind.multiCharacterSelectorRoot.btnReady.isEnabled = true

        lifecycle.launch(Dispatchers.IO) {
            retrofit.cluedo.notifyCharacterSelected(channelName, playerName, characterName, tokenSource)
        }
    }

    override fun catchUp(playerName: String, playerList: ArrayList<PlayerDomainModel>) {
        lifecycle.launch(Dispatchers.IO) {
            playerList.filter { player -> player.playerName != playerName && player.selectedCharacter.isNotEmpty() }.forEach {
                debugPrint("channelName: $channelName\nplayerName: ${it.playerName}\nselectedCharacter: ${it.selectedCharacter}")
                retrofit.cluedo.notifyCharacterSelected(channelName, it.playerName, it.selectedCharacter, adapter.getTokenFromCharacterName(it.selectedCharacter))
            }
        }
    }
}