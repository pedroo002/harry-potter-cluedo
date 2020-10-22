package neptun.jxy1vz.cluedo.ui.fragment.channel.join

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.pusher.client.Pusher
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.android.synthetic.main.fragment_join_channel.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentJoinChannelBinding
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.JoinRequest
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.MultiplayerCharacterSelectorFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class JoinChannelFragment : Fragment(), ViewModelListener, MenuListener {

    companion object {
        const val TAG = "FRAGMENT-join"
    }

    private lateinit var fragmentJoinChannelBinding: FragmentJoinChannelBinding
    private lateinit var pusher: Pusher
    private lateinit var retrofit: RetrofitInstance

    private lateinit var playerName: String
    private lateinit var channelId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentJoinChannelBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_join_channel, container, false)

        retrofit = RetrofitInstance.getInstance(context!!)
        pusher = PusherInstance.getInstance()
        pusher.connect()

        fragmentJoinChannelBinding.joinChannelViewModel =
            JoinChannelViewModel(fragmentJoinChannelBinding, context!!, lifecycleScope, this)
        return fragmentJoinChannelBinding.root
    }

    override fun onFinish() {
        if (fragmentJoinChannelBinding.joinChannelViewModel!!.joinPressed) {
            fragmentJoinChannelBinding.root.btnJoin.isEnabled = false
            val channelName = fragmentJoinChannelBinding.joinChannelViewModel!!.channel
            val pusherChannelName = "presence-$channelName"

            lifecycleScope.launch(Dispatchers.IO) {
                val pref = context!!.getSharedPreferences(resources.getString(R.string.player_data_pref), Context.MODE_PRIVATE)
                playerName = pref.getString(resources.getString(R.string.player_name_key), "")!!
                val joinRequest = JoinRequest(playerName!!, fragmentJoinChannelBinding.joinChannelViewModel!!.authKey)
                val body = retrofit.moshi.adapter(JoinRequest::class.java).toJson(joinRequest).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                channelId = fragmentJoinChannelBinding.joinChannelViewModel!!.channelId
                retrofit.cluedo.joinChannel(channelId, body)

                val editor = pref.edit()
                editor.putString(resources.getString(R.string.channel_id_key), channelId)
                editor.apply()
            }

            pusher.subscribePresence(pusherChannelName)

            pusher.getPresenceChannel(pusherChannelName).bind("game-ready", object : PresenceChannelEventListener {
                override fun onEvent(p0: String?, p1: String?, p2: String?) {
                    openCharacterSelector()
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            pusher.getPresenceChannel(pusherChannelName).bind("channel-removed", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    if (channelName == pusherChannelName) {
                        Snackbar.make(fragmentJoinChannelBinding.root, "A szerver megszűnt.", Snackbar.LENGTH_LONG).show()
                        pusher.disconnect()
                        onFragmentClose()
                    }
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })
        }
    }

    fun openCharacterSelector() {
        lifecycleScope.launch(Dispatchers.Main) {
            activity!!.supportFragmentManager.beginTransaction().add(R.id.menuFrame, MultiplayerCharacterSelectorFragment(this@JoinChannelFragment)).addToBackStack("CharacterSelectorMulti").commit()
        }
    }

    override fun onFragmentClose() {
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }

    suspend fun onBackPressed() {
        pusher.disconnect()
        if (!this::channelId.isInitialized)
            return
        retrofit.cluedo.leaveChannel(channelId, playerName)
    }
}