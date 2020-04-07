package neptun.jxy1vz.cluedo.ui.map

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil.setContentView
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMapBinding
import neptun.jxy1vz.cluedo.model.helper.playerImageIdList
import neptun.jxy1vz.cluedo.model.helper.playerList

class MapActivity : AppCompatActivity() {

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

        activityMapBinding.mapViewModel = MapViewModel(intent.getIntExtra("Player ID", 0), playerImageList, mapLayout, supportFragmentManager)
        activityMapBinding.executePendingBindings()
    }
}
