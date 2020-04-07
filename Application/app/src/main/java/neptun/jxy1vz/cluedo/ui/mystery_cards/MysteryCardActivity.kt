package neptun.jxy1vz.cluedo.ui.mystery_cards

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.cluedo.model.MysteryType
import neptun.jxy1vz.cluedo.model.helper.playerList

class MysteryCardActivity : AppCompatActivity() {

    private lateinit var activityMysteryCardBinding: ActivityMysteryCardBinding
    private var playerId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playerId = intent.getIntExtra("Player ID", 0)

        activityMysteryCardBinding = DataBindingUtil.setContentView(this, R.layout.activity_mystery_card)
        activityMysteryCardBinding.mysteryCardViewModel = MysteryCardViewModel(applicationContext, playerList[playerId])
        activityMysteryCardBinding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()

        activityMysteryCardBinding.mysteryCardViewModel!!.getMysteryCard(MysteryType.TOOL, activityMysteryCardBinding.ivMysteryCardTool)
        activityMysteryCardBinding.mysteryCardViewModel!!.getMysteryCard(MysteryType.SUSPECT, activityMysteryCardBinding.ivMysteryCardSuspect)
        activityMysteryCardBinding.mysteryCardViewModel!!.getMysteryCard(MysteryType.VENUE, activityMysteryCardBinding.ivMysteryCardVenue)

        activityMysteryCardBinding.mysteryCardViewModel!!.setSolution()
    }
}