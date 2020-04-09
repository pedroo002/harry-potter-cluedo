package neptun.jxy1vz.cluedo.model

data class Door(
    val id: Int,
    val position: Position,
    val room: Room,
    val closedDoor: Int? = null,
    val state: DoorState = DoorState.OPENED
)

enum class DoorState {
    OPENED,
    CLOSED
}