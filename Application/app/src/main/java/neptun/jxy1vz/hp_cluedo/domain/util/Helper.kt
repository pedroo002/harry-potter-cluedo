package neptun.jxy1vz.hp_cluedo.domain.util

import android.content.Context
import android.widget.ImageView
import android.widget.NumberPicker
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.domain.model.BasePlayer
import neptun.jxy1vz.hp_cluedo.domain.model.ThinkingPlayer
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.playerImagePairs
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

fun setNumPicker(numPicker: NumberPicker, min: Int, max: Int, color: Int) {
    numPicker.minValue = min
    numPicker.maxValue = max
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        numPicker.textColor = color
    }
}

fun removePlayer(player: BasePlayer) {
    val newPlayerList = ArrayList<BasePlayer>()
    gameModels.playerList.forEach { p ->
        if (p.id != player.id)
            newPlayerList.add(p)
    }
    gameModels.playerList = newPlayerList
    val pair =
        MapViewModel.playerHandler.getPairById(player.id)
    MapViewModel.mapRoot.mapLayout.removeView(pair.second)
    val newPlayerImagePairs =
        ArrayList<Pair<BasePlayer, ImageView>>()
    playerImagePairs.forEach { p ->
        if (p.first.id != player.id)
            newPlayerImagePairs.add(p)
    }
    playerImagePairs = newPlayerImagePairs
}

fun loadUrlImageIntoImageView(url: String, context: Context, iv: ImageView) {
    Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder_image).into(iv)
}

fun isServerReachable(ipAddress: String): Boolean {
    return try {
        val socketAddress = InetSocketAddress(ipAddress, 80)
        val socket = Socket()
        socket.connect(socketAddress, 3000)
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}