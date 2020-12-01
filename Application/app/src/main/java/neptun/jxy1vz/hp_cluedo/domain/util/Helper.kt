package neptun.jxy1vz.hp_cluedo.domain.util

import android.content.Context
import android.widget.ImageView
import android.widget.NumberPicker
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.playerImagePairs
import java.io.IOException

fun setNumPicker(numPicker: NumberPicker, min: Int, max: Int, color: Int) {
    numPicker.minValue = min
    numPicker.maxValue = max
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        numPicker.textColor = color
    }
}

fun removePlayer(player: Player) {
    val newPlayerList = ArrayList<Player>()
    gameModels.playerList.forEach { p ->
        if (p.id != player.id)
            newPlayerList.add(p)
    }
    gameModels.playerList = newPlayerList
    val pair =
        MapViewModel.playerHandler.getPairById(player.id)
    MapViewModel.mapRoot.mapLayout.removeView(pair.second)
    val newPlayerImagePairs =
        ArrayList<Pair<Player, ImageView>>()
    playerImagePairs.forEach { p ->
        if (p.first.id != player.id)
            newPlayerImagePairs.add(p)
    }
    playerImagePairs = newPlayerImagePairs
}

fun loadUrlImageIntoImageView(url: String, context: Context, iv: ImageView) {
    Glide.with(context).load(url).placeholder(R.drawable.placeholder_image).into(iv)
}

fun isOnline(): Boolean {
    val runtime = Runtime.getRuntime()
    try {
        val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
        val exitValue = ipProcess.waitFor()
        return exitValue == 0
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    return false
}