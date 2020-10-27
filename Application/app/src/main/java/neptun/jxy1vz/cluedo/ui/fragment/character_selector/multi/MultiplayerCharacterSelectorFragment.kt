package neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.database.CluedoDatabase
import neptun.jxy1vz.cluedo.database.model.PlayerDBmodel
import neptun.jxy1vz.cluedo.databinding.FragmentMultiplayerCharacterSelectorBinding
import neptun.jxy1vz.cluedo.domain.util.debugPrint
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuActivity
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.activity.mystery_cards.MysteryCardActivity
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import retrofit2.HttpException

class MultiplayerCharacterSelectorFragment(private val host: Boolean, private val isLate: Boolean, private val listener: MenuListener) : Fragment(),
    ViewModelListener, MultiplayerCharacterSelectorViewModel.ChannelListener {

    companion object {
        const val TAG = "FRAGMENT-MULTIPLAYER-CHARACTER-SELECTOR"
    }

    private lateinit var fragmentMultiplayerCharacterSelectorBinding: FragmentMultiplayerCharacterSelectorBinding
    private var cancellation = false
    private lateinit var retrofit: RetrofitInstance
    private lateinit var parentActivity: MenuActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMultiplayerCharacterSelectorBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_multiplayer_character_selector,
            container,
            false
        )
        fragmentMultiplayerCharacterSelectorBinding.characterSelectorViewModel =
            MultiplayerCharacterSelectorViewModel(
                fragmentMultiplayerCharacterSelectorBinding,
                context!!,
                host,
                isLate,
                lifecycleScope,
                this,
                this
            )

        retrofit = RetrofitInstance.getInstance(context!!)
        parentActivity = activity!! as MenuActivity

        return fragmentMultiplayerCharacterSelectorBinding.root
    }

    suspend fun onBackPressed() {
        cancellation = true
        if (host) {
            try {
                debugPrint("Delete channel: Multiplayerblablabla")
                retrofit.cluedo.notifyChannelRemovedAfterJoin(vm().channelName)
                retrofit.cluedo.deleteChannel(vm().channelId)
            }
            catch (ex: HttpException) {
                if (ex.code() == 404) {
                    debugPrint("Delete channel ${vm().channelName} - 404")
                }
            }
        }
        else {
            retrofit.cluedo.notifyPlayerLeaves(vm().channelName, vm().playerName)
            retrofit.cluedo.leaveChannel(vm().channelId, vm().playerName)
        }
        PusherInstance.getInstance().apply {
            unsubscribe(vm().channelName)
            disconnect()
        }
        onFinish()
    }

    override fun onFinish() {
        parentActivity.supportFragmentManager.beginTransaction()
            .remove(this@MultiplayerCharacterSelectorFragment).commit()
        listener.onFragmentClose()

        if (cancellation) {
            for (i in 1..parentActivity.supportFragmentManager.backStackEntryCount)
                parentActivity.supportFragmentManager.popBackStack()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val db = CluedoDatabase.getInstance(context!!)
            db.playerDao().deletePlayers()

            vm().getReadyPlayers().map { playerDomainModel -> PlayerDBmodel(0, playerDomainModel.playerName, playerDomainModel.playerId, playerDomainModel.selectedCharacter) }.forEach {
                db.playerDao().insertIntoTable(it)
            }
            withContext(Dispatchers.Main) {
                val mysteryCardIntent = Intent(context, MysteryCardActivity::class.java)
                mysteryCardIntent.putExtra(
                    context!!.resources.getString(R.string.player_id),
                    vm().playerId
                )
                mysteryCardIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context!!.startActivity(mysteryCardIntent)
            }
        }
    }

    override fun onChannelRemoved() {
        cancellation = true
        PusherInstance.getInstance().apply {
            unsubscribe(vm().channelName)
            disconnect()
        }
        onFinish()
    }

    private fun vm(): MultiplayerCharacterSelectorViewModel = fragmentMultiplayerCharacterSelectorBinding.characterSelectorViewModel!!
}