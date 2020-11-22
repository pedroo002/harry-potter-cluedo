package neptun.jxy1vz.cluedo.domain.util

import android.widget.ImageView
import android.widget.NumberPicker
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel

fun setNumPicker(numPicker: NumberPicker, min: Int, max: Int, color: Int) {
    numPicker.minValue = min
    numPicker.maxValue = max
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        numPicker.textColor = color
    }
}

fun removePlayer(player: Player) {
    val newPlayerList = ArrayList<Player>()
    for (p in MapViewModel.gameModels.playerList) {
        if (p.id != player.id)
            newPlayerList.add(p)
    }
    MapViewModel.gameModels.playerList = newPlayerList
    val pair =
        MapViewModel.playerHandler.getPairById(player.id)
    MapViewModel.mapRoot.mapLayout.removeView(pair.second)
    val newPlayerImagePairs =
        ArrayList<Pair<Player, ImageView>>()
    for (p in MapViewModel.playerImagePairs) {
        if (p.first.id != player.id)
            newPlayerImagePairs.add(p)
    }
    MapViewModel.playerImagePairs = newPlayerImagePairs
}