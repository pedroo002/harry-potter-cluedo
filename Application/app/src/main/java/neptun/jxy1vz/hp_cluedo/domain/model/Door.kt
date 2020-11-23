package neptun.jxy1vz.hp_cluedo.domain.model

data class Door(
    val id: Int,
    val position: Position,
    val room: Room,
    var state: DoorState = DoorState.OPENED
)

enum class DoorState {
    OPENED,
    CLOSED
}

fun DoorState.boolean(): Boolean {
    return when (this) {
        DoorState.OPENED -> false
        else -> true
    }
}