package neptun.jxy1vz.cluedo.ui.map

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val greenTile = findViewById<ImageView>(R.id.ivGreenTile)
        activityMainBinding.player1 = MapViewModel(resources.displayMetrics.densityDpi, greenTile)
        activityMainBinding.executePendingBindings()
    }
}
