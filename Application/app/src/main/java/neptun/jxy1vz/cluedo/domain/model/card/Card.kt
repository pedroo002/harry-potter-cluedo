package neptun.jxy1vz.cluedo.domain.model.card

abstract class Card(
    open val id: Int,
    open val name: String,
    open val imageRes: Int,
    open val verso: Int
)