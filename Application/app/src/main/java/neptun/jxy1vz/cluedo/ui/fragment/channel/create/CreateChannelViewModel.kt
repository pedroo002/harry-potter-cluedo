package neptun.jxy1vz.cluedo.ui.fragment.channel.create

import android.content.Context
import android.util.Log
import androidx.databinding.BaseObservable
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.ChannelApiModel
import neptun.jxy1vz.cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateChannelViewModel(private val bind: FragmentCreateChannelBinding, private val context: Context, listener: ViewModelListener) : BaseObservable() {

    fun createChannel() {
        val channelName = bind.txtChannelName.text
        val authKey = "${bind.numAuthKey1.value}${bind.numAuthKey2.value}${bind.numAuthKey3.value}${bind.numAuthKey4.value}"

        val jsonObject = JSONObject()
        jsonObject.put("channel_name", channelName)
        jsonObject.put("auth_key", authKey)
        jsonObject.put("max_user", context.getSharedPreferences(context.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE).getString(context.resources.getString(R.string.player_count_key), "5"))
        val jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())

        GlobalScope.launch(Dispatchers.IO) {
            RetrofitInstance.retrofit.createChannel(jsonBody).enqueue(object : Callback<ChannelApiModel> {
                override fun onResponse(
                    call: Call<ChannelApiModel>,
                    response: Response<ChannelApiModel>
                ) {
                    when (response.code()) {
                        201 -> {

                        }
                        400 -> {
                            Snackbar.make(bind.root, response.message(), Snackbar.LENGTH_LONG).show()
                        }
                        else -> {

                        }
                    }
                }

                override fun onFailure(call: Call<ChannelApiModel>, t: Throwable) {
                    Log.i("CreateChannelViewModel", t.message)
                }

            })
        }
    }
}