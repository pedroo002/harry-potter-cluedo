package neptun.jxy1vz.hp_cluedo.ui.fragment.character_selector.multi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.PlayerDBmodel
import neptun.jxy1vz.hp_cluedo.databinding.FragmentMultiplayerCharacterSelectorBinding
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.data.network.pusher.PusherInstance
import neptun.jxy1vz.hp_cluedo.ui.activity.menu.MenuActivity
import neptun.jxy1vz.hp_cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.hp_cluedo.ui.activity.mystery_cards.MysteryCardActivity
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import retrofit2.HttpException
import java.net.SocketTimeoutException

class MultiplayerCharacterSelectorFragment : Fragment(),
    ViewModelListener, MultiplayerCharacterSelectorViewModel.ChannelListener {

    private var host: Boolean = false
    private var isLate: Boolean = false
    private lateinit var listener: MenuListener

    fun setArgs(h: Boolean, late: Boolean, l: MenuListener) {
        host = h
        isLate = late
        listener = l
    }

    companion object {
        const val TAG = "FRAGMENT-MULTIPLAYER-CHARACTER-SELECTOR"

        fun newInstance(host: Boolean, isLate: Boolean, listener: MenuListener): MultiplayerCharacterSelectorFragment {
            val fragment = MultiplayerCharacterSelectorFragment()
            fragment.setArgs(host, isLate, listener)
            return fragment
        }
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
        retrofit = RetrofitInstance.getInstance(context!!)
        parentActivity = activity!! as MenuActivity
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
        return fragmentMultiplayerCharacterSelectorBinding.root
    }

    suspend fun onBackPressed() {
        cancellation = true
        if (host) {
            try {
                retrofit.cluedo.notifyChannelRemovedAfterJoin(vm().channelName)
                retrofit.cluedo.deleteChannel(vm().channelId)
            }
            catch (ex: HttpException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(fragmentMultiplayerCharacterSelectorBinding.root, ex.message ?: "Hiba lépett fel a hálózatban.", Snackbar.LENGTH_LONG).show()
                }
            }
            catch (ex: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(fragmentMultiplayerCharacterSelectorBinding.root, "A kapcsolat túllépte az időkorlátot!", Snackbar.LENGTH_LONG).show()
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
        if (cancellation) {
            popFragments()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val db = CluedoDatabase.getInstance(context!!)
            db.playerDao().deletePlayers()

            vm().getReadyPlayers().map { playerDomainModel -> PlayerDBmodel(0, playerDomainModel.playerName, playerDomainModel.playerId, playerDomainModel.selectedCharacter, vm().channelName) }.forEach {
                db.playerDao().insertIntoTable(it)
            }
            withContext(Dispatchers.Main) {
                popFragments()

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

    private fun popFragments() {
        for (i in 1..parentActivity.supportFragmentManager.backStackEntryCount)
            parentActivity.supportFragmentManager.popBackStack()
        listener.onFragmentClose()
    }
}