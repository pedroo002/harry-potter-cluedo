package neptun.jxy1vz.cluedo.ui.fragment.channel.create

import android.content.Context
import androidx.databinding.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.ChannelRequest
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class CreateChannelViewModel(private val bind: FragmentCreateChannelBinding, private val context: Context, listener: ViewModelListener) : BaseObservable() {

    private val retrofit = RetrofitInstance.getInstance(context)

    fun createChannel() {
        val channelName = bind.txtChannelName.text.toString()
        val authKey = "${bind.numAuthKey1.value}${bind.numAuthKey2.value}${bind.numAuthKey3.value}${bind.numAuthKey4.value}"

        val adapter = retrofit.moshi.adapter(ChannelRequest::class.java)
        val channelRequest = ChannelRequest(channelName, authKey, context.getSharedPreferences(context.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE).getString(context.resources.getString(R.string.player_count_key), "5")!!)
        val moshiJson = adapter.toJson(channelRequest)
        val jsonBody = moshiJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        GlobalScope.launch(Dispatchers.IO) {
            retrofit.cluedo.createChannel(jsonBody)
        }
    }
}