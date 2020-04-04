package neptun.jxy1vz.cluedo.ui.menu.character_selector

import android.content.Context
import android.content.Intent
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.model.Player
import neptun.jxy1vz.cluedo.model.helper.playerList
import neptun.jxy1vz.cluedo.ui.map.MapActivity

class CharacterSelectorViewModel(private val context: Context) : BaseObservable() {

    private lateinit var player: Player

    fun startGame() {
        val mapIntent = Intent(context, MapActivity::class.java)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mapIntent)
    }

    fun setPlayer(id: Int) {
        player = playerList[id]
    }
}