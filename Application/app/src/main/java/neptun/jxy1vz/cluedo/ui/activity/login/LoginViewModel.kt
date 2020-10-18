package neptun.jxy1vz.cluedo.ui.activity.login

import android.content.Context
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.PlayerApiModel
import neptun.jxy1vz.cluedo.databinding.ActivityLoginBinding
import neptun.jxy1vz.cluedo.network.model.nyt_model.ArticleData
import neptun.jxy1vz.cluedo.network.model.nyt_model.ArticleProperties
import neptun.jxy1vz.cluedo.network.model.nyt_model.toDomainModel
import neptun.jxy1vz.cluedo.network.model.weather.WeatherData
import okhttp3.Dispatcher
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class LoginViewModel(private val bind: ActivityLoginBinding, private val context: Context, private val listener: LoginActivityListener, private val lifecycle: LifecycleCoroutineScope) : BaseObservable() {

    fun login() {
        disableEditTexts()

        GlobalScope.launch(Dispatchers.IO) {
            RetrofitInstance.weather.getWeather("London", "metric", RetrofitInstance.SERVICE_KEY).enqueue(object : Callback<WeatherData> {
                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                    println("jovagy")
                }

                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    println("Fail")
                }
            })
        }
        /*GlobalScope.launch(Dispatchers.IO) {
            RetrofitInstance.nytimes.getNews().enqueue(object : Callback<ArticleData> {
                override fun onResponse(call: Call<ArticleData>, response: Response<ArticleData>) {
                    val results = response.body()?.results?.map(ArticleProperties::toDomainModel)
                    println("Size of results: ${results?.size}")
                }

                override fun onFailure(call: Call<ArticleData>, t: Throwable) {
                    println("Unsuccessful query")
                }

            })

            /*RetrofitInstance.retrofit.test().enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    println(response.message())
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    println("Fail.")
                }

            })*/

            val playerName = bind.root.txtPlayerName.text.toString()
            val password = bind.root.txtPassword.text.toString()

            val jsonObject = JSONObject()
            jsonObject.put("player_name", playerName)
            jsonObject.put("password", password)
            val jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())

            if (bind.root.newOrExisting.isChecked) {
                register(jsonBody)
            }
            else {
                RetrofitInstance.cluedo.loginPlayer(playerName, password).process { playerApiModel, throwable ->
                    println("Debug: ${playerApiModel?.name}")
                }
                /*try {
                    RetrofitInstance.retrofit.loginPlayer(playerName, password)
                        .enqueue(object : Callback<PlayerApiModel> {
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
                            Log.i("LoginViewModel::login()", t.message)
                            enableEditTexts()
                        }
                    })
                } catch (ex: Exception) {
                    Log.i("LoginViewModel::login()", "Hiba:\n${ex.localizedMessage}")
                }*/
            }
        }*/
    }

    private suspend fun register(jsonBody: RequestBody) {
        /*RetrofitInstance.retrofit.registerPlayer(jsonBody).process { playerApiModel, throwable ->
            println(playerApiModel?.name)
        }*/
        RetrofitInstance.cluedo.registerPlayer(jsonBody).enqueue(object : Callback<PlayerApiModel> {
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

        })
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