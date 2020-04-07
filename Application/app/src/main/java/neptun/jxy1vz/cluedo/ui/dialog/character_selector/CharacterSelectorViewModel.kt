package neptun.jxy1vz.cluedo.ui.dialog.character_selector

import android.content.Context
import android.content.Intent
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.ui.mystery_cards.MysteryCardActivity

class CharacterSelectorViewModel(private val context: Context) : BaseObservable() {

    private var playerId: Int = 0

    fun startGame() {
        val mysteryCardIntent = Intent(context, MysteryCardActivity::class.java)
        mysteryCardIntent.putExtra("Player ID", playerId)
        mysteryCardIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mysteryCardIntent)
    }

    fun setPlayer(id: Int) {
        playerId = id
        val pref = context.getSharedPreferences("Game params", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("player_id", id)
        editor.apply()
    }
}