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
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentJoinChannelBinding
import neptun.jxy1vz.cluedo.domain.util.debugPrint
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.channel.JoinRequest
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.MultiplayerCharacterSelectorFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class JoinChannelFragment : Fragment(), ViewModelListener, MenuListener {

    companion object {
        const val TAG = "FRAGMENT-join"
    }

    private lateinit var fragmentJoinChannelBinding: FragmentJoinChannelBinding
    private lateinit var pusher: Pusher
    private lateinit var retrofit: RetrofitInstance

    private lateinit var playerName: String
    private lateinit var channelId: String
    private lateinit var pusherChannelName: String

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
            pusherChannelName = "presence-$channelName"

            lifecycleScope.launch(Dispatchers.IO) {
                val pref = context!!.getSharedPreferences(resources.getString(R.string.player_data_pref), Context.MODE_PRIVATE)
                playerName = pref.getString(resources.getString(R.string.player_name_key), "")!!
                val joinRequest = JoinRequest(playerName, fragmentJoinChannelBinding.joinChannelViewModel!!.authKey)
                val body = retrofit.moshi.adapter(JoinRequest::class.java).toJson(joinRequest).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                channelId = fragmentJoinChannelBinding.joinChannelViewModel!!.channelId

                try {
                    val channelApiModel = retrofit.cluedo.getChannel(channelId)
                    retrofit.cluedo.joinChannel(channelId, body)

                    val editor = pref.edit()
                    editor.putString(resources.getString(R.string.channel_id_key), channelId)
                    editor.apply()

                    withContext(Dispatchers.Main) {
                        pusher.subscribePresence(pusherChannelName)

                        if (channelApiModel!!.isWaiting)
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
                        else
                            openCharacterSelector(true)

                        pusher.getPresenceChannel(pusherChannelName).bind("channel-removed-before-join", object : PresenceChannelEventListener {
                            override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                                if (channelName == pusherChannelName) {
                                    Snackbar.make(fragmentJoinChannelBinding.root, "A szerver megszűnt.", Snackbar.LENGTH_LONG).show()
                                    pusher.unsubscribe(pusherChannelName)
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
                catch (ex: HttpException) {
                    val message = when (ex.code()) {
                        400 -> "Már csatlakozott!"
                        401 -> "A szerver megtelt."
                        403 -> "Helytelen kulcs."
                        404 -> "Szerver nem található."
                        500 -> "Szerverhiba, próbálja újra!"
                        else -> ex.message()
                    }
                    withContext(Dispatchers.Main) {
                        Snackbar.make(fragmentJoinChannelBinding.root.rootView, message, Snackbar.LENGTH_LONG).show()
                        fragmentJoinChannelBinding.root.btnJoin.isEnabled = true
                    }
                }
            }
        }
    }

    fun openCharacterSelector(isLate: Boolean = false) {
        lifecycleScope.launch(Dispatchers.Main) {
            activity!!.supportFragmentManager.beginTransaction().add(R.id.menuFrame, MultiplayerCharacterSelectorFragment(false, isLate, this@JoinChannelFragment), MultiplayerCharacterSelectorFragment.TAG).addToBackStack("CharacterSelectorMulti").commit()
            onFragmentClose()
        }
    }

    override fun onFragmentClose() {
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }

    suspend fun onBackPressed() {
        if (!this::channelId.isInitialized)
            return
        try {
            retrofit.cluedo.leaveChannel(channelId, playerName)
        }
        catch (ex: HttpException) {
            if (ex.code() == 404)
                debugPrint("Channel $channelId does not exist any more.")
        }
        finally {
            pusher.unsubscribe(pusherChannelName)
            pusher.disconnect()
            onFragmentClose()
        }
    }
}