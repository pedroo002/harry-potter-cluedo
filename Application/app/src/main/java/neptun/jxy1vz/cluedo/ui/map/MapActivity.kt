package neptun.jxy1vz.cluedo.ui.map

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil.setContentView
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMapBinding
import neptun.jxy1vz.cluedo.model.Player
import neptun.jxy1vz.cluedo.model.helper.playerImageIdList
import neptun.jxy1vz.cluedo.model.helper.playerList

class MapActivity : AppCompatActivity(), MapActivityListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityMapBinding = setContentView<ActivityMapBinding>(this, R.layout.activity_map)
        val mapLayout = findViewById<ConstraintLayout>(R.id.mapLayout)
        val playerImageList: MutableList<ImageView> = ArrayList()

        for (id in playerImageIdList) {
            var delete = true
            for (player in playerList) {
                if (player.tile == id)
                    delete = false
            }
            if (delete) {
                mapLayout.removeView(findViewById(id))
            }
            else {
                playerImageList.add(mapLayout.findViewById(id))
            }
        }

        val playerImagePairs: MutableList<Pair<Player, ImageView>> = ArrayList()
        for (i in 0 until playerList.size) {
            playerImagePairs.add(Pair(playerList[i], playerImageList[i]))
        }

        activityMapBinding.mapViewModel = MapViewModel(this, applicationContext, intent.getIntExtra("Player ID", 0), playerImagePairs, mapLayout, supportFragmentManager)
        activityMapBinding.executePendingBindings()
    }

    override fun exitToMenu() {
        finish()
    }
}
