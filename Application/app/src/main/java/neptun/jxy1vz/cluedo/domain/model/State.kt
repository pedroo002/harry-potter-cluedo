package neptun.jxy1vz.cluedo.domain.model

data class State(
    val serialNum: Int,
    val roomId: Int,
    val doorId: Int,
    val doorState: DoorState,
    val darkMark: Boolean,
    val passageWay: Int? = null
)