package neptun.jxy1vz.hp_cluedo.ui.fragment.channel.join

import android.content.Context
import android.widget.ArrayAdapter
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentJoinChannelBinding
import neptun.jxy1vz.hp_cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.network.model.channel.ChannelApiModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.hp_cluedo.ui.fragment.channel.num_picker.NumPickerFragment

class JoinChannelViewModel(
    private val bind: FragmentJoinChannelBinding,
    private val context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    fm: FragmentManager,
    private val listener: ViewModelListener
) : BaseObservable(), NumPickerFragment.NumPickerChangeListener {

    var authKey = ""
    var channel = ""
    var channelId = ""
    var joinPressed = false

    var channels: List<ChannelApiModel>? = null
    lateinit var channelNames: ArrayList<String>
    private val retrofit = RetrofitInstance.getInstance(context)

    private var num1 = 0
    private var num2 = 0
    private var num3 = 0
    private var num4 = 0

    private var numPicker: NumPickerFragment = NumPickerFragment.newInstance(this)

    init {
        fm.beginTransaction().add(R.id.numPicker, numPicker).commit()

        bind.swipeJoin.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                updateChannelList()
                withContext(Dispatchers.Main) {
                    bind.swipeJoin.isRefreshing = false
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            updateChannelList()
        }
    }

    private suspend fun updateChannelList() {
        val playerCount = context.getSharedPreferences(
            context.resources.getString(R.string.game_params_pref),
            Context.MODE_PRIVATE
        ).getInt(context.resources.getString(R.string.player_count_key), 3)

        channels = retrofit.cluedo.getChannelsByPlayerLimit(playerCount)
        channels?.let {
            channelNames = ArrayList()
            channels!!.forEach { channel ->
                channelNames.add(channel.channelName)
            }

            withContext(Dispatchers.Main) {
                val spinnerAdapter = ArrayAdapter(context, R.layout.spinner_item, channelNames)
                bind.spinnerAllChannels.adapter = spinnerAdapter
                if (channelNames.isNotEmpty())
                    bind.btnJoin.isEnabled = true
            }
        }
    }

    fun join() {
        numPicker.disablePickers()
        bind.swipeJoin.isEnabled = false

        authKey =
            "${num1}${num2}${num3}${num4}"
        channel = bind.spinnerAllChannels.selectedItem.toString()
        channelId = channels!![channelNames.indexOf(channel)].id
        joinPressed = true
        listener.onFinish()
    }

    override fun onValueChanged(num1: Int, num2: Int, num3: Int, num4: Int) {
        this.num1 = num1
        this.num2 = num2
        this.num3 = num3
        this.num4 = num4
    }
}