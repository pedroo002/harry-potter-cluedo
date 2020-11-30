package neptun.jxy1vz.hp_cluedo.ui.activity.login

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.ActivityLoginBinding
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.data.network.model.player.PlayerRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class LoginViewModel(
    private val bind: ActivityLoginBinding,
    private val context: Context,
    private val listener: LoginActivityListener,
    private val lifecycle: LifecycleCoroutineScope
) : BaseObservable() {

    private val retrofit = RetrofitInstance.getInstance(context)
    private val adapter = retrofit.moshi.adapter(PlayerRequest::class.java)

    fun login() {
        disableEditTexts()

        lifecycle.launch(Dispatchers.IO) {
            val playerName = bind.root.txtPlayerName.text.toString()
            val password = bind.root.txtPassword.text.toString()

            val playerRequest = PlayerRequest(playerName, password)

            val moshiJson = adapter.toJson(playerRequest)
            val jsonBody =
                moshiJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            if (bind.root.newOrExisting.isChecked) {
                register(moshiJson)
            } else {
                try {
                    val res = retrofit.cluedo.loginPlayer(jsonBody)
                    res?.let {
                        savePlayerData(playerName, password)
                        listener.goToMenu()
                    }
                }
                catch (ex: HttpException){
                    val message = when (ex.code()) {
                        400 -> "Már be van jelentkezve!"
                        401 -> "Helytelen jelszó!"
                        404 -> "Ilyen nevű játékos nem létezik."
                        500 -> "Szerverhiba, próbálja újra!"
                        else -> "Ismeretlen hiba történt."
                    }
                    withContext(Dispatchers.Main) {
                        errorSnackbar(message)
                    }
                }
            }
        }
    }

    private suspend fun register(json: String) {
        try {
            retrofit.cluedo.registerPlayer(json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))

            val playerRequest = adapter.fromJson(json)

            val res = retrofit.cluedo.loginPlayer(json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))
            res?.let {
                savePlayerData(playerRequest!!.playerName, playerRequest.password)
                listener.goToMenu()
            }
        }
        catch (ex: HttpException) {
            val message = when (ex.code()) {
                400 -> "Ilyen nevű játékos már létezik."
                500 -> "Szerverhiba, próbálja újra!"
                else -> "Ismeretlen hiba történt."
            }
            withContext(Dispatchers.Main) {
                errorSnackbar(message)
            }
        }
    }

    private fun errorSnackbar(message: String) {
        Snackbar.make(bind.root, message, Snackbar.LENGTH_LONG)
            .show()
        enableEditTexts()
    }

    private fun disableEditTexts() {
        bind.root.txtPlayerName.isEnabled = false
        bind.root.txtPassword.isEnabled = false
    }

    private fun enableEditTexts() {
        bind.root.txtPlayerName.isEnabled = true
        bind.root.txtPassword.isEnabled = true
    }

    private fun savePlayerData(playerName: String, password: String) {
        val pref = context.getSharedPreferences(context.resources.getString(R.string.player_data_pref), Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(context.resources.getString(R.string.player_name_key), playerName)
        editor.putString(context.resources.getString(R.string.password_key), password)
        editor.apply()
    }
}