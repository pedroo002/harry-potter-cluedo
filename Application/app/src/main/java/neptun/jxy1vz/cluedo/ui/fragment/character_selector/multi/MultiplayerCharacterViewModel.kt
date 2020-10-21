package neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentMultiplayerCharacterSelectorBinding
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.PlayerDomainModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.adapter.PlayerListAdapter

class MultiplayerCharacterViewModel(
    private val bind: FragmentMultiplayerCharacterSelectorBinding,
    private val context: Context,
    private val lifecycle: LifecycleCoroutineScope,
    private val listener: ViewModelListener
) : BaseObservable() {

    private val retrofit: RetrofitInstance = RetrofitInstance.getInstance(context)
    private val playerName: String =
        context.getSharedPreferences(
            context.resources.getString(R.string.player_data_pref),
            Context.MODE_PRIVATE
        ).getString(context.resources.getString(R.string.player_name_key), "")!!
    var playerId = -1

    init {
        val subscribedPlayers = ArrayList<String>()
        lifecycle.launch(Dispatchers.IO) {
            val channelId = context.getSharedPreferences(
                context.resources.getString(R.string.player_data_pref),
                Context.MODE_PRIVATE
            ).getString(context.resources.getString(R.string.channel_id_key), "")

            val channel = retrofit.cluedo.getChannel(channelId!!)
            subscribedPlayers.addAll(channel!!.subscribedUsers)

            val playersOfChannel = ArrayList<PlayerDomainModel>()
            playersOfChannel.addAll(
                retrofit.cluedo.getPlayers()
                    ?.filter { playerApiModel -> subscribedPlayers.contains(playerApiModel.name) }
                    ?.map { playerApiModel -> PlayerDomainModel(playerApiModel.name, "", -1) }
                    ?.toList()!!
            )

            withContext(Dispatchers.Main) {
                val adapter = PlayerListAdapter(playersOfChannel, playerName)
                bind.rvPlayerList.adapter = adapter
            }
        }
    }

    fun ready() {
        val adapter = (bind.rvPlayerList.adapter as PlayerListAdapter)
        if (adapter.areSelectionsDifferent()) {
            playerId = adapter.getCurrentPlayer().playerId
            listener.onFinish()
        }
    }
}