package neptun.jxy1vz.cluedo.ui.activity.login

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.databinding.ActivityLoginBinding
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.PlayerRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class LoginViewModel(
    private val bind: ActivityLoginBinding,
    private val context: Context,
    private val listener: LoginActivityListener,
    private val lifecycle: LifecycleCoroutineScope
) : BaseObservable() {

    private val retrofit = RetrofitInstance.getInstance(context)

    fun login() {
        disableEditTexts()

        lifecycle.launch(Dispatchers.IO) {
            val playerName = bind.root.txtPlayerName.text.toString()
            val password = bind.root.txtPassword.text.toString()

            val playerRequest = PlayerRequest(playerName, password)
            val adapter = retrofit.moshi.adapter(PlayerRequest::class.java)

            val moshiJson = adapter.toJson(playerRequest)
            val jsonBody =
                moshiJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            if (bind.root.newOrExisting.isChecked) {
                register(jsonBody)
            } else {
                val res = retrofit.cluedo.loginPlayer(jsonBody)
                res?.let {
                    listener.goToMenu(playerName)
                }
                if (res == null) {
                    errorSnackbar()
                }
            }
        }
    }

    private suspend fun register(jsonBody: RequestBody) {
        val res = retrofit.cluedo.registerPlayer(jsonBody)
        res?.let {
            listener.goToMenu(res.name)
            return
        }
        withContext(Dispatchers.Main) {
            errorSnackbar()
        }
    }

    private fun errorSnackbar() {
        Snackbar.make(bind.root, "Művelet sikertelen. Kérlek próbáld újra!", Snackbar.LENGTH_LONG)
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
}