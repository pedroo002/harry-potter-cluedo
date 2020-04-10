package neptun.jxy1vz.cluedo.model

data class State(
    val serialNum: Int,
    val roomId: Int,
    val doorId: Int,
    val doorState: DoorState,
    val darkMark: Boolean,
    val passageWay: Int? = null
)