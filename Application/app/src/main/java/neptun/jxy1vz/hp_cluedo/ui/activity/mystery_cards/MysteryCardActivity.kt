package neptun.jxy1vz.hp_cluedo.ui.activity.mystery_cards

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.hp_cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView

class MysteryCardActivity : AppCompatActivity() {

    private lateinit var activityMysteryCardBinding: ActivityMysteryCardBinding
    private var playerId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameModel = GameModels(applicationContext)
        playerId = intent.getIntExtra(applicationContext.resources.getString(R.string.player_id), 0)

        lifecycleScope.launch(Dispatchers.IO) {
            val loadingScreen = CluedoDatabase.getInstance(applicationContext).assetDao().getAssetByTag("resources/menu/other/loading_screen.png")!!.url
            withContext(Dispatchers.Main) {
                activityMysteryCardBinding = DataBindingUtil.setContentView(this@MysteryCardActivity, R.layout.activity_mystery_card)
                loadUrlImageIntoImageView(loadingScreen, applicationContext, activityMysteryCardBinding.ivLoadingScreen)
                activityMysteryCardBinding.mysteryCardViewModel = MysteryCardViewModel(gameModel, applicationContext, playerId, activityMysteryCardBinding, supportFragmentManager)
                activityMysteryCardBinding.executePendingBindings()
            }
        }
    }
}