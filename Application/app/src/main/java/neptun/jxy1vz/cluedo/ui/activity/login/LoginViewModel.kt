package neptun.jxy1vz.cluedo.ui.activity.login

import android.content.Context
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.databinding.ActivityLoginBinding
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.PlayerRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

class LoginViewModel(private val bind: ActivityLoginBinding, private val context: Context, private val listener: LoginActivityListener, private val lifecycle: LifecycleCoroutineScope) : BaseObservable() {

    private val retrofit = RetrofitInstance.getInstance(context)

    fun login() {
        disableEditTexts()

        GlobalScope.launch(Dispatchers.IO) {
            val playerName = bind.root.txtPlayerName.text.toString()
            val password = bind.root.txtPassword.text.toString()

            val playerRequest = PlayerRequest(playerName, password)
            val adapter = retrofit.moshi.adapter(PlayerRequest::class.java)

            val jsonObject = JSONObject()
            jsonObject.put("player_name", playerName)
            jsonObject.put("password", password)

            val moshiJson = adapter.toJson(playerRequest)

            val jsonBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), moshiJson)

            if (bind.root.newOrExisting.isChecked) {
                register(jsonBody)
            }
            else {
                /*RetrofitInstance.cluedo.loginPlayer(playerName, password).process { playerApiModel, throwable ->
                    println("Debug: ${playerApiModel?.name}")
                }*/

                val res = retrofit.cluedo.loginPlayer(jsonBody)
                res?.let {
                    if (res.name == playerName)
                        listener.goToMenu(playerName)
                }
                /*.enqueue(object : Callback<PlayerApiModel> {
                override fun onResponse(call: Call<PlayerApiModel>, response: Response<PlayerApiModel>) {
                    println("${response.code()}: ${response.message()}")
                    when (response.code()) {
                        200 -> {
                            listener.goToMenu(playerName)
                        }
                        400 -> {
                            enableEditTexts()
                            Snackbar.make(bind.root, response.message(), Snackbar.LENGTH_LONG).show()
                        }
                        500 -> {
                            enableEditTexts()
                            serverErrorSnackbar()
                        }
                    }
                }

                override fun onFailure(call: Call<PlayerApiModel>, t: Throwable) {
                    t.message?.let { Log.i("LoginViewModel::login()", it) }
                    enableEditTexts()
                }
            })*/
            }
        }
    }

    private suspend fun register(jsonBody: RequestBody) {
        /*RetrofitInstance.cluedo.registerPlayer(jsonBody).process { playerApiModel, throwable ->
            println(playerApiModel?.name)
        }*/
        val res = retrofit.cluedo.registerPlayer(jsonBody)
        res?.let {
            listener.goToMenu(res.name)
        }
            /*.enqueue(object : Callback<PlayerApiModel> {
            override fun onResponse(
                call: Call<PlayerApiModel>,
                response: Response<PlayerApiModel>
            ) {
                println("${response.code()}: ${response.message()}")
                when (response.code()) {
                    201 -> {
                        listener.goToMenu(bind.root.txtPlayerName.text.toString())
                    }
                    400 -> {
                        enableEditTexts()
                        Snackbar.make(bind.root, response.message(), Snackbar.LENGTH_LONG).show()
                    }
                    500 -> {
                        enableEditTexts()
                        serverErrorSnackbar()
                    }
                }
            }

            override fun onFailure(call: Call<PlayerApiModel>, t: Throwable) {
                t.message?.let { Log.i("LoginViewModel::register()", it) }
                enableEditTexts()
            }
        })*/
    }

    private fun serverErrorSnackbar() {
        Snackbar.make(bind.root, "Szerverhiba. Kérlek próbáld újra!", Snackbar.LENGTH_LONG).show()
    }

    private fun disableEditTexts() {
        bind.root.txtPlayerName.isEnabled = false
        bind.root.txtPassword.isEnabled = false
    }

    private fun enableEditTexts() {
        bind.root.txtPlayerName.isEnabled = true
        bind.root.txtPassword.isEnabled = true
    }
}