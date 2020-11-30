package neptun.jxy1vz.hp_cluedo.domain.model.card

abstract class Card(
    open val id: Int,
    open val name: String,
    open val imageRes: String = "",
    open val verso: String = ""
)