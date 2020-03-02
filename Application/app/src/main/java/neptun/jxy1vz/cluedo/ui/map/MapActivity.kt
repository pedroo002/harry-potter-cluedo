package neptun.jxy1vz.cluedo.ui.map

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = setContentView<ActivityMapBinding>(this, R.layout.activity_map)
        val playerList = listOf<ImageView>(
            findViewById(R.id.ivGreenPlayer),
            findViewById(R.id.ivRedPlayer),
            findViewById(R.id.ivYellowPlayer),
            findViewById(R.id.ivBluePlayer),
            findViewById(R.id.ivPurplePlayer),
            findViewById(R.id.ivWhitePlayer)
        )
        activityMainBinding.player1 = MapViewModel(playerList)
        activityMainBinding.executePendingBindings()
    }
}
