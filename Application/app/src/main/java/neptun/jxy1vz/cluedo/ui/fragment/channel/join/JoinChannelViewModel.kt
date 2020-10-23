package neptun.jxy1vz.cluedo.ui.fragment.channel.join

import android.content.Context
import android.graphics.Color
import android.widget.ArrayAdapter
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.android.synthetic.main.fragment_create_channel.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentJoinChannelBinding
import neptun.jxy1vz.cluedo.domain.util.setNumPicker
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.channel.ChannelApiModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class JoinChannelViewModel(private val bind: FragmentJoinChannelBinding, private val context: Context, private val lifecycleScope: LifecycleCoroutineScope, private val listener: ViewModelListener) : BaseObservable() {

    var authKey = ""
    var channel = ""
    var channelId = ""
    var joinPressed = false

    var channels: List<ChannelApiModel>? = null
    lateinit var channelNames: ArrayList<String>
    private val retrofit = RetrofitInstance.getInstance(context)

    init {
        setNumPicker(bind.root.numAuthKey1, 0, 9, Color.WHITE)
        setNumPicker(bind.root.numAuthKey2, 0, 9, Color.WHITE)
        setNumPicker(bind.root.numAuthKey3, 0, 9, Color.WHITE)
        setNumPicker(bind.root.numAuthKey4, 0, 9, Color.WHITE)

        lifecycleScope.launch(Dispatchers.IO) {
            val playerCount = context.getSharedPreferences(context.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE).getInt(context.resources.getString(R.string.player_count_key), 3)

            channels = retrofit.cluedo.getChannelsByPlayerLimit(playerCount)
            channels?.let {
                channelNames = ArrayList()
                for (channel in channels!!) {
                    channelNames.add(channel.channelName)
                }

                withContext(Dispatchers.Main) {
                    val spinnerAdapter = ArrayAdapter(context, R.layout.spinner_item, channelNames)
                    bind.spinnerAllChannels.adapter = spinnerAdapter
                }
            }
        }
    }

    fun join() {
        authKey = "${bind.numAuthKey1.value}${bind.numAuthKey2.value}${bind.numAuthKey3.value}${bind.numAuthKey4.value}"
        channel = bind.spinnerAllChannels.selectedItem.toString()
        channelId = channels!![channelNames.indexOf(channel)].id
        joinPressed = true
        listener.onFinish()
    }
}