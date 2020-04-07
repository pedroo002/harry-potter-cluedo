package neptun.jxy1vz.cluedo.model

import androidx.annotation.DrawableRes

data class Room(
    val id: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val left: Int,
    val area: Int,
    @DrawableRes val selection: Int
)