package neptun.jxy1vz.cluedo.model

data class Position (
    var row: Int,
    var col: Int
)

fun Position.toString(): String {
    return "($col; $row)"
}