package neptun.jxy1vz.cluedo.model

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