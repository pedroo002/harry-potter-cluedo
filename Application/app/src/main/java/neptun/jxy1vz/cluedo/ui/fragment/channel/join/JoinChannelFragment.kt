package neptun.jxy1vz.cluedo.ui.fragment.channel.join

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.pusher.client.Pusher
import kotlinx.android.synthetic.main.fragment_create_channel.view.numAuthKey1
import kotlinx.android.synthetic.main.fragment_create_channel.view.numAuthKey2
import kotlinx.android.synthetic.main.fragment_create_channel.view.numAuthKey3
import kotlinx.android.synthetic.main.fragment_create_channel.view.numAuthKey4
import kotlinx.android.synthetic.main.fragment_join_channel.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentJoinChannelBinding
import neptun.jxy1vz.cluedo.domain.util.setNumPicker
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.JoinRequest
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.MultiplayerCharacterSelectorFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class JoinChannelFragment : Fragment(), ViewModelListener, MenuListener {

    private lateinit var fragmentJoinChannelBinding: FragmentJoinChannelBinding
    private lateinit var pusher: Pusher
    private lateinit var retrofit: RetrofitInstance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentJoinChannelBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_join_channel, container, false)

        setNumPicker(fragmentJoinChannelBinding.root.numAuthKey1, 0, 9, Color.WHITE)
        setNumPicker(fragmentJoinChannelBinding.root.numAuthKey2, 0, 9, Color.WHITE)
        setNumPicker(fragmentJoinChannelBinding.root.numAuthKey3, 0, 9, Color.WHITE)
        setNumPicker(fragmentJoinChannelBinding.root.numAuthKey4, 0, 9, Color.WHITE)

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
            val pusherChannelName =
                "presence-${fragmentJoinChannelBinding.joinChannelViewModel!!.channel}"

            lifecycleScope.launch(Dispatchers.IO) {
                val pref = context!!.getSharedPreferences(resources.getString(R.string.player_data_pref), Context.MODE_PRIVATE)
                val playerName = pref.getString(resources.getString(R.string.player_name_key), "")
                val joinRequest = JoinRequest(playerName!!, fragmentJoinChannelBinding.joinChannelViewModel!!.authKey)
                val body = retrofit.moshi.adapter(JoinRequest::class.java).toJson(joinRequest).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val channelId = fragmentJoinChannelBinding.joinChannelViewModel!!.channelId
                retrofit.cluedo.joinChannel(channelId, body)

                val editor = pref.edit()
                editor.putString(resources.getString(R.string.channel_id_key), channelId)
                editor.apply()
            }

            pusher.subscribePresence(pusherChannelName)
            pusher.getPresenceChannel(pusherChannelName).bind("game-ready") { p0, p1, p2 ->
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.menuFrame, MultiplayerCharacterSelectorFragment(this)).commit()
            }
        }
    }

    override fun onFragmentClose() {
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}