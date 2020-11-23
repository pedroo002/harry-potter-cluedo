package neptun.jxy1vz.hp_cluedo.ui.activity.mystery_cards

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.hp_cluedo.domain.model.helper.GameModels

class MysteryCardActivity : AppCompatActivity() {

    private lateinit var activityMysteryCardBinding: ActivityMysteryCardBinding
    private var playerId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameModel = GameModels(applicationContext)
        playerId = intent.getIntExtra(applicationContext.resources.getString(R.string.player_id), 0)

        activityMysteryCardBinding = DataBindingUtil.setContentView(this, R.layout.activity_mystery_card)
        activityMysteryCardBinding.mysteryCardViewModel = MysteryCardViewModel(gameModel, applicationContext, playerId, activityMysteryCardBinding, supportFragmentManager)
        activityMysteryCardBinding.executePendingBindings()
    }
}