package neptun.jxy1vz.cluedo.ui.fragment.channel.create

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.snackbar.Snackbar
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.android.synthetic.main.fragment_create_channel.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.channel.ChannelRequest
import neptun.jxy1vz.cluedo.network.model.channel.JoinRequest
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.channel.num_picker.NumPickerFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class CreateChannelViewModel(
    private val bind: FragmentCreateChannelBinding,
    private val context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    fm: FragmentManager,
    private val listener: ViewModelListener
) : BaseObservable(), NumPickerFragment.NumPickerChangeListener {

    private val retrofit = RetrofitInstance.getInstance(context)
    private val pusher = PusherInstance.getInstance()
    private var pusherChannelName: String? = null

    private lateinit var playerName: String
    private lateinit var channelId: String
    private var isWaiting = true

    private var fragmentKilled = false

    private var num1 = 0
    private var num2 = 0
    private var num3 = 0
    private var num4 = 0

    private var numPicker: NumPickerFragment = NumPickerFragment.newInstance(this)

    init {
        fm.beginTransaction().add(R.id.numPicker, numPicker).commit()

        bind.root.txtChannelName.addTextChangedListener {
            bind.root.btnCreateChannel.isEnabled = it!!.isNotEmpty()
        }
    }

    fun channelCreated(): Boolean {
        return !pusherChannelName.isNullOrEmpty()
    }

    fun getChannel(): String = pusherChannelName!!

    suspend fun deleteCreatedChannel() {
        if (!this::channelId.isInitialized)
            return
        retrofit.cluedo.notifyChannelRemovedBeforeJoin(pusherChannelName!!)
        retrofit.cluedo.deleteChannel(channelId)
    }

    fun createChannel() {
        val channelName = bind.txtChannelName.text.toString()
        val authKey =
            "${num1}${num2}${num3}${num4}"

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
            try {
                val res = retrofit.cluedo.createChannel(jsonBody)
                if (res != null) {
                    val pref = context.getSharedPreferences(
                        context.resources.getString(R.string.player_data_pref),
                        Context.MODE_PRIVATE
                    )
                    playerName =
                        pref.getString(context.resources.getString(R.string.player_name_key), "")!!
                    channelId = res.id

                    val joinRequest = JoinRequest(playerName, authKey)
                    val json = retrofit.moshi.adapter(JoinRequest::class.java).toJson(joinRequest)
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    retrofit.cluedo.joinChannel(res.id, json)

                    val editor = pref.edit()
                    editor.putString(context.resources.getString(R.string.channel_id_key), res.id)
                    editor.apply()

                    withContext(Dispatchers.Main) {
                        numPicker.disablePickers()
                        bind.btnCreateChannel.isEnabled = false
                        bind.txtChannelName.isEnabled = false

                        bind.ivLoading.visibility = ImageView.VISIBLE
                        bind.tvWaitForPlayers.visibility = TextView.VISIBLE
                    }

                    pusher.connect()

                    var playersToWait = playerCount - 1

                    pusherChannelName = "presence-${res.channelName}"
                    pusher.subscribePresence(pusherChannelName, object : PresenceChannelEventListener {
                        override fun onEvent(channelName: String?, eventName: String?, message: String?) {}
                        override fun onSubscriptionSucceeded(p0: String?) {}
                        override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                        override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}

                        override fun userSubscribed(channelName: String?, presenceData: User?) {
                            if (!isWaiting)
                                return
                            playersToWait--
                            if (!fragmentKilled)
                                Snackbar.make(
                                    bind.createChannelRoot,
                                    "Valaki megjött!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            if (playersToWait == 0) {
                                if (!fragmentKilled)
                                    Snackbar.make(
                                        bind.createChannelRoot,
                                        "Mindenki megjött!",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                lifecycleScope.launch(Dispatchers.IO) {
                                    delay(500)
                                    retrofit.cluedo.notifyGameReady(pusherChannelName!!)
                                }
                                isWaiting = false
                                listener.onFinish()
                            }
                        }

                        override fun userUnsubscribed(channelName: String?, presenceData: User?) {
                            if (!isWaiting)
                                return
                            playersToWait++
                            if (!fragmentKilled)
                                Snackbar.make(
                                    bind.createChannelRoot,
                                    "Valaki elment!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                        }
                    })
                }
            }
            catch (ex: HttpException) {
                val message = when (ex.code()) {
                    400 -> "Már létezik ilyen nevű szerver."
                    500 -> "Szerverhiba, próbálja újra!"
                    else -> "Ismeretlen hiba történt."
                }
                withContext(Dispatchers.Main) {
                    Snackbar.make(bind.createChannelRoot, message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    fun notifyFragmentKilled() {
        fragmentKilled = true
    }

    override fun onValueChanged(num1: Int, num2: Int, num3: Int, num4: Int) {
        this.num1 = num1
        this.num2 = num2
        this.num3 = num3
        this.num4 = num4
    }
}