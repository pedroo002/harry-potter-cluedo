package neptun.jxy1vz.hp_cluedo.ui.activity.menu

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.ActivityMenuBinding
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.data.network.model.player.PlayerRequest
import neptun.jxy1vz.hp_cluedo.ui.fragment.channel.create.CreateChannelFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.channel.join.JoinChannelFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.character_selector.multi.MultiplayerCharacterSelectorFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class MenuActivity : AppCompatActivity(), MenuViewModel.MenuListener {

    private lateinit var activityMenuBinding: ActivityMenuBinding
    private lateinit var retrofit: RetrofitInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retrofit = RetrofitInstance.getInstance(applicationContext)

        activityMenuBinding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
        activityMenuBinding.menuViewModel = MenuViewModel(activityMenuBinding, supportFragmentManager, this)
    }

    override fun onBackPressed() {
        val create = (supportFragmentManager.findFragmentByTag(CreateChannelFragment.TAG) as CreateChannelFragment?)
        val join = (supportFragmentManager.findFragmentByTag(JoinChannelFragment.TAG) as JoinChannelFragment?)
        val multi = (supportFragmentManager.findFragmentByTag(MultiplayerCharacterSelectorFragment.TAG) as MultiplayerCharacterSelectorFragment?)

        lifecycleScope.launch(Dispatchers.IO) {
            if (multi != null) {
                multi.onBackPressed()
                for (i in 1..supportFragmentManager.backStackEntryCount)
                    supportFragmentManager.popBackStack()
            }
            else {
                join?.onBackPressed()
                create?.onBackPressed()
            }
        }

        activityMenuBinding.menuViewModel!!.onFragmentClose()
        super.onBackPressed()
    }

    override fun exitGame() {
        lifecycleScope.launch(Dispatchers.IO) {
            logout()
        }
        finish()
    }

    override fun onDestroy() {
        lifecycleScope.launch(Dispatchers.IO) {
            logout()
        }
        super.onDestroy()
    }

    private suspend fun logout() {
        val pref = applicationContext.getSharedPreferences(
            resources.getString(R.string.player_data_pref),
            Context.MODE_PRIVATE
        )
        val playerRequest = PlayerRequest(
            pref.getString(resources.getString(R.string.player_name_key), "")!!,
            pref.getString(resources.getString(R.string.password_key), "")!!
        )
        val body = retrofit.moshi.adapter(PlayerRequest::class.java).toJson(playerRequest).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        retrofit.cluedo.logoutPlayer(body)
    }
}