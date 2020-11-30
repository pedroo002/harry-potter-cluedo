package neptun.jxy1vz.hp_cluedo.ui.activity.map

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.lifecycleScope
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.ActivityMapBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.MapActivityListener
import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.data.network.pusher.PusherInstance

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

        gamePref = applicationContext.getSharedPreferences(
            applicationContext.resources.getString(R.string.game_params_pref),
            Context.MODE_PRIVATE
        )
        playerPref = applicationContext.getSharedPreferences(
            applicationContext.resources.getString(R.string.player_data_pref),
            Context.MODE_PRIVATE
        )
        playMode =
            gamePref.getString(applicationContext.resources.getString(R.string.play_mode_key), "")!!
        playModes = applicationContext.resources.getStringArray(R.array.playmodes)

        activityMapBinding = setContentView(this, R.layout.activity_map)
        val mapRoot = findViewById<ZoomLayout>(R.id.mapRoot)

        lifecycleScope.launch(Dispatchers.IO) {
            if (playMode == playModes[1]) {
                retrofit = RetrofitInstance.getInstance(applicationContext)
                channel = playerPref.getString(
                    applicationContext.resources.getString(R.string.channel_id_key),
                    ""
                )!!
                pusherChannel = "presence-${retrofit.cluedo.getChannel(channel)!!.channelName}"
            }

            val gameModel = GameModels(applicationContext)
            val playerList = gameModel.keepCurrentPlayers()
            gameModel.playerList = playerList.sortedBy { p -> p.id }
            gameModel.eraseNotes()

            CluedoDatabase.getInstance(applicationContext).assetDao().apply {
                val mapBackground = getAssetByTag("resources/map/other/map.png")!!.url
                val doorAssets = getAssetsByPrefix(AssetPrefixes.DOOR.string())!!.map { assetDBmodel -> assetDBmodel.url }

                val star = getAssetByTag("resources/map/tile/star.png")!!.url
                gameModel.fieldSelection = getAssetByTag("resources/map/selection/selection11_field.png")!!.url
                gameModel.starSelection = getAssetByTag("resources/map/tile/star_selection.png")!!.url

                val starIvList = listOf(activityMapBinding.ivStar1, activityMapBinding.ivStar2, activityMapBinding.ivStar3, activityMapBinding.ivStar4, activityMapBinding.ivStar5, activityMapBinding.ivStar6, activityMapBinding.ivStar7, activityMapBinding.ivStar8)
                val leftDoorIvList = listOf(activityMapBinding.ivDoor0, activityMapBinding.ivDoor4, activityMapBinding.ivDoor7, activityMapBinding.ivDoor15, activityMapBinding.ivDoor17, activityMapBinding.ivDoor20)
                val rightDoorIvList = listOf(activityMapBinding.ivDoor2, activityMapBinding.ivDoor19)
                val topDoorIvList = listOf(activityMapBinding.ivDoor6, activityMapBinding.ivDoor13)
                val bottomDoorIvList = listOf(activityMapBinding.ivDoor12, activityMapBinding.ivDoor21)

                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(mapBackground, applicationContext, activityMapBinding.ivMap)
                    starIvList.forEach {
                        loadUrlImageIntoImageView(star, applicationContext, it)
                    }
                    leftDoorIvList.forEach {
                        loadUrlImageIntoImageView(doorAssets[1], applicationContext, it)
                    }
                    rightDoorIvList.forEach {
                        loadUrlImageIntoImageView(doorAssets[2], applicationContext, it)
                    }
                    topDoorIvList.forEach {
                        loadUrlImageIntoImageView(doorAssets[3], applicationContext, it)
                    }
                    bottomDoorIvList.forEach {
                        loadUrlImageIntoImageView(doorAssets[0], applicationContext, it)
                    }

                    val tiles = listOf(
                        R.id.ivBluePlayer,
                        R.id.ivPurplePlayer,
                        R.id.ivRedPlayer,
                        R.id.ivYellowPlayer,
                        R.id.ivWhitePlayer,
                        R.id.ivGreenPlayer
                    )

                    val playerImagePairs: MutableList<Pair<Player, ImageView>> = ArrayList()
                    tiles.forEach { tile ->
                        var delete = true
                        val player = playerList.find { player -> player.tile == tile }
                        player?.let {
                            delete = false
                            playerImagePairs.add(Pair(player, mapRoot.mapLayout.findViewById(tile)))
                        }
                        if (delete) {
                            mapRoot.mapLayout.removeView(mapRoot.mapLayout.findViewById(tile))
                        }
                    }

                    activityMapBinding.mapViewModel = MapViewModel(
                        gameModel,
                        this@MapActivity,
                        activityMapBinding,
                        applicationContext,
                        intent.getIntExtra(
                            applicationContext.resources.getString(R.string.player_id),
                            0
                        ),
                        playerImagePairs,
                        mapRoot,
                        supportFragmentManager
                    )
                    activityMapBinding.executePendingBindings()
                }
            }
        }
    }

    override fun exitToMenu(notify: Boolean) {
        if (playMode == playModes[1]) {
            retrofit.cluedo.apply {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (playerPref.getBoolean(
                            applicationContext.resources.getString(R.string.is_host_key),
                            false
                        )
                    ) {
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
        } else
            MapViewModel.onDestroy()

        finish()
    }

    override fun onBackPressed() {
        activityMapBinding.mapViewModel!!.onBackPressed()
    }

    override fun onDestroy() {
        exitToMenu(true)
        super.onDestroy()
    }
}
