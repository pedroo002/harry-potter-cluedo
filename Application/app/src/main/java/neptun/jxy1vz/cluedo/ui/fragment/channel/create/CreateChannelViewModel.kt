package neptun.jxy1vz.cluedo.ui.fragment.channel.create

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.snackbar.Snackbar
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.ChannelRequest
import neptun.jxy1vz.cluedo.network.model.JoinRequest
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class CreateChannelViewModel(
    private val bind: FragmentCreateChannelBinding,
    private val context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val listener: ViewModelListener
) : BaseObservable() {

    private val retrofit = RetrofitInstance.getInstance(context)

    fun createChannel() {
        val channelName = bind.txtChannelName.text.toString()
        val authKey =
            "${bind.numAuthKey1.value}${bind.numAuthKey2.value}${bind.numAuthKey3.value}${bind.numAuthKey4.value}"

        val playerCount = context.getSharedPreferences(
            context.resources.getString(R.string.game_params_pref),
            Context.MODE_PRIVATE
        ).getInt(context.resources.getString(R.string.player_count_key), 5)

        val adapter = retrofit.moshi.adapter(ChannelRequest::class.java)
        val channelRequest = ChannelRequest(channelName, authKey, playerCount.toString())
        val moshiJson = adapter.toJson(channelRequest)
        val jsonBody =
            moshiJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        lifecycleScope.launch(Dispatchers.IO) {
            val res = retrofit.cluedo.createChannel(jsonBody)
            if (res != null) {
                val pref = context.getSharedPreferences(
                    context.resources.getString(R.string.player_data_pref),
                    Context.MODE_PRIVATE
                )
                val playerName = pref.getString(context.resources.getString(R.string.player_name_key), "")

                val joinRequest = JoinRequest(playerName!!, authKey)
                val json = retrofit.moshi.adapter(JoinRequest::class.java).toJson(joinRequest).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                retrofit.cluedo.joinChannel(res.id, json)

                val editor = pref.edit()
                editor.putString(context.resources.getString(R.string.channel_id_key), res.id)
                editor.apply()

                withContext(Dispatchers.Main) {
                    bind.numAuthKey1.isEnabled = false
                    bind.numAuthKey2.isEnabled = false
                    bind.numAuthKey3.isEnabled = false
                    bind.numAuthKey4.isEnabled = false
                    bind.btnCreateChannel.isEnabled = false
                    bind.txtChannelName.isEnabled = false

                    bind.ivLoading.visibility = ImageView.VISIBLE
                    bind.tvWaitForPlayers.visibility = TextView.VISIBLE
                }

                val pusher = PusherInstance.getInstance()
                pusher.connect()

                var playersToWait = playerCount - 1

                val pusherChannelName = "presence-${res.channelName}"
                pusher.subscribePresence(pusherChannelName, object : PresenceChannelEventListener {
                    override fun onEvent(p0: String?, p1: String?, p2: String?) {}
                    override fun onSubscriptionSucceeded(p0: String?) {}
                    override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                    override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}

                    override fun userSubscribed(p0: String?, p1: User?) {
                        playersToWait--

                        Snackbar.make(bind.root, "Valaki megjött!", Snackbar.LENGTH_LONG).show()
                        if (playersToWait == 0) {
                            Snackbar.make(bind.root, "Mindenki megjött!", Snackbar.LENGTH_LONG)
                                .show()
                            lifecycleScope.launch(Dispatchers.IO) {
                                retrofit.cluedo.notifyGameReady(pusherChannelName)
                            }
                            listener.onFinish()
                        }
                    }

                    override fun userUnsubscribed(p0: String?, p1: User?) {}
                })
            }
        }
    }
}