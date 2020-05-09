package neptun.jxy1vz.cluedo.ui.map

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.otaliastudios.zoom.ZoomLayout
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

class MapActivity : AppCompatActivity(), MapActivityListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityMapBinding = setContentView<ActivityMapBinding>(this, R.layout.activity_map)
        val mapRoot = findViewById<ZoomLayout>(R.id.mapRoot)
        val playerImageList: MutableList<ImageView> = ArrayList()

        GlobalScope.launch(Dispatchers.IO) {
            val gameModel = GameModels(applicationContext)
            val playerList = gameModel.keepCurrentPlayers()
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

    override fun exitToMenu() {
        finish()
    }

    override fun onDestroy() {
        MapViewModel.onDestroy()
        super.onDestroy()
    }
}
