package neptun.jxy1vz.cluedo.domain.model

import androidx.annotation.DrawableRes

data class Room(
    val id: Int,
    val name: String,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val left: Int,
    val selection: Int
)