package neptun.jxy1vz.hp_cluedo.ui.activity.menu

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.ActivityMenuBinding
import neptun.jxy1vz.hp_cluedo.domain.model.helper.DatabaseAccess
import neptun.jxy1vz.hp_cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.network.model.player.PlayerRequest
import neptun.jxy1vz.hp_cluedo.ui.fragment.channel.create.CreateChannelFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.channel.join.JoinChannelFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.character_selector.multi.MultiplayerCharacterSelectorFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class MenuActivity : AppCompatActivity(), MenuViewModel.MenuListener {

    private lateinit var activityMenuBinding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = applicationContext.getSharedPreferences(
            getString(R.string.database_name),
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        if (!pref.contains(getString(R.string.first_start_pref))) {
            editor.putBoolean(getString(R.string.first_start_pref), true)
            GlobalScope.launch(Dispatchers.IO) {
                val db = DatabaseAccess(applicationContext)
                db.uploadDatabase()
            }
        } else
            editor.putBoolean(getString(R.string.first_start_pref), false)
        editor.apply()

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
        val pref = applicationContext.getSharedPreferences(
            resources.getString(R.string.player_data_pref),
            Context.MODE_PRIVATE
        )
        val playerRequest = PlayerRequest(
            pref.getString(resources.getString(R.string.player_name_key), "")!!,
            pref.getString(resources.getString(R.string.password_key), "")!!
        )

        val retrofit = RetrofitInstance.getInstance(applicationContext)
        val body = retrofit.moshi.adapter(PlayerRequest::class.java).toJson(playerRequest).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        lifecycleScope.launch(Dispatchers.IO) {
            retrofit.cluedo.logoutPlayer(body)
        }

        finish()
    }
}