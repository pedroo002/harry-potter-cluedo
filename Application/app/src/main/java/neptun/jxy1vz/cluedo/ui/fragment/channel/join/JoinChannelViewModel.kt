package neptun.jxy1vz.cluedo.ui.fragment.channel.join

import android.content.Context
import android.widget.ArrayAdapter
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentJoinChannelBinding
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.ChannelApiModel
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
        lifecycleScope.launch(Dispatchers.IO) {
            channels = retrofit.cluedo.getChannels()
            channels?.let {
                channelNames = ArrayList()
                for (channel in channels!!) {
                    channelNames.add(channel.channelName)
                }

                withContext(Dispatchers.Main) {
                    val spinnerAdapter = ArrayAdapter<String>(context, R.layout.spinner_item, channelNames)
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