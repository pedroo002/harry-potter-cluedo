package neptun.jxy1vz.cluedo.ui.mystery_cards

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.cluedo.domain.model.helper.GameModels

class MysteryCardActivity : AppCompatActivity() {

    private lateinit var activityMysteryCardBinding: ActivityMysteryCardBinding
    private var playerId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameModel = GameModels(applicationContext)
        playerId = intent.getIntExtra("Player ID", 0)

        activityMysteryCardBinding = DataBindingUtil.setContentView(this, R.layout.activity_mystery_card)
        activityMysteryCardBinding.mysteryCardViewModel = MysteryCardViewModel(gameModel, applicationContext, playerId, activityMysteryCardBinding)
        activityMysteryCardBinding.executePendingBindings()
    }
}