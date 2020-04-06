package neptun.jxy1vz.cluedo.ui.mystery_cards

import android.content.Context
import android.content.Intent
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.model.Player
import neptun.jxy1vz.cluedo.ui.map.MapActivity

class MysteryCardViewModel(private val context: Context, private val player: Player) : BaseObservable() {
    fun startGame() {
        val mapIntent = Intent(context, MapActivity::class.java)
        mapIntent.putExtra("Player ID", player.id)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mapIntent)
    }
}