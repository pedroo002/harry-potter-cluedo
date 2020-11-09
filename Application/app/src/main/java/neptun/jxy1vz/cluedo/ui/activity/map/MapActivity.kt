package neptun.jxy1vz.cluedo.ui.activity.map

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.lifecycleScope
import com.otaliastudios.zoom.ZoomLayout
import com.pusher.client.channel.PresenceChannelEventListener
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMapBinding
import neptun.jxy1vz.cluedo.domain.handler.MapActivityListener
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance

class MapActivity : AppCompatActivity(), MapActivityListener {

    private lateinit var activityMapBinding: ActivityMapBinding

    private lateinit var gamePref: SharedPreferences
    private lateinit var playerPref: SharedPreferences
    private lateinit var playMode: String
    private lateinit var playModes: Array<String>

    private lateinit var retrofit: RetrofitInstance
    private lateinit var channel: String
    private lateinit var pusherChannel: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gamePref = applicationContext.getSharedPreferences(applicationContext.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE)
        playerPref = applicationContext.getSharedPreferences(applicationContext.resources.getString(R.string.player_data_pref), Context.MODE_PRIVATE)
        playMode = gamePref.getString(applicationContext.resources.getString(R.string.play_mode_key), "")!!
        playModes = applicationContext.resources.getStringArray(R.array.playmodes)

        activityMapBinding = setContentView(this, R.layout.activity_map)
        val mapRoot = findViewById<ZoomLayout>(R.id.mapRoot)
        val playerImageList: MutableList<ImageView> = ArrayList()

        GlobalScope.launch(Dispatchers.IO) {
            if (playMode == playModes[1]) {
                retrofit = RetrofitInstance.getInstance(applicationContext)
                channel = playerPref.getString(applicationContext.resources.getString(R.string.channel_id_key), "")!!
                pusherChannel = "presence-${retrofit.cluedo.getChannel(channel)!!.channelName}"
            }

            val gameModel = GameModels(applicationContext)
            val playerList = gameModel.keepCurrentPlayers()
            gameModel.playerList = playerList.sortedBy { p -> p.id }
            gameModel.eraseNotes()
            withContext(Dispatchers.Main) {
                for (id in gameModel.playerImageIdList) {
                    var delete = true
                    for (player in playerList) {
                        if (player.tile == id)
                            delete = false
                    }
                    if (delete) {
                        mapRoot.mapLayout.removeView(findViewById(id))
                    } else {
                        playerImageList.add(mapRoot.mapLayout.findViewById(id))
                    }
                }

                val playerImagePairs: MutableList<Pair<Player, ImageView>> = ArrayList()
                for (i in playerList.indices) {
                    playerImagePairs.add(Pair(playerList[i], playerImageList[i]))
                }

                activityMapBinding.mapViewModel = MapViewModel(
                    gameModel,
                    this@MapActivity,
                    applicationContext,
                    intent.getIntExtra(applicationContext.resources.getString(R.string.player_id), 0),
                    playerImagePairs,
                    mapRoot,
                    supportFragmentManager
                )
                activityMapBinding.executePendingBindings()
            }
        }
    }

    override fun exitToMenu(notify: Boolean) {
        if (playMode == playModes[1]) {
            retrofit.cluedo.apply {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (playerPref.getBoolean(applicationContext.resources.getString(R.string.is_host_key), false)) {
                        retrofit.cluedo.deleteChannel(channel)
                    }
                    PusherInstance.getInstance().apply {
                        unsubscribe(pusherChannel)
                        disconnect()
                    }
                    if (notify)
                        activityMapBinding.mapViewModel!!.quitDuringGame()
                }
            }
        }

        finish()
    }

    override fun onBackPressed() {
        activityMapBinding.mapViewModel!!.onBackPressed()
    }

    override fun onDestroy() {
        MapViewModel.onDestroy()
        super.onDestroy()
    }
}
