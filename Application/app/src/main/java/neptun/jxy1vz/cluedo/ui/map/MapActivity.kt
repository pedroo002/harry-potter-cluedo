package neptun.jxy1vz.cluedo.ui.map

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMapBinding
import neptun.jxy1vz.cluedo.model.Player
import neptun.jxy1vz.cluedo.model.helper.playerImageIdList
import neptun.jxy1vz.cluedo.model.helper.playerList

class MapActivity : AppCompatActivity(), MapActivityListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityMapBinding = setContentView<ActivityMapBinding>(this, R.layout.activity_map)
        val mapRoot = findViewById<ZoomLayout>(R.id.mapRoot)
        val playerImageList: MutableList<ImageView> = ArrayList()

        for (id in playerImageIdList) {
            var delete = true
            for (player in playerList) {
                if (player.tile == id)
                    delete = false
            }
            if (delete) {
                mapRoot.mapLayout.removeView(findViewById(id))
            }
            else {
                playerImageList.add(mapRoot.mapLayout.findViewById(id))
            }
        }

        val playerImagePairs: MutableList<Pair<Player, ImageView>> = ArrayList()
        for (i in 0 until playerList.size) {
            playerImagePairs.add(Pair(playerList[i], playerImageList[i]))
        }

        activityMapBinding.mapViewModel = MapViewModel(this, applicationContext, intent.getIntExtra("Player ID", 0), playerImagePairs, mapRoot, supportFragmentManager)
        activityMapBinding.executePendingBindings()
    }

    override fun exitToMenu() {
        finish()
    }
}
