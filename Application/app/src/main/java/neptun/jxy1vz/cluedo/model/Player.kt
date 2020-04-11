package neptun.jxy1vz.cluedo.model

data class Player(
    val id: Int,
    val card: PlayerCard,
    var pos: Position,
    val tile: Int,
    var hp: Int = 70,
    var mysteryCards: MutableList<MysteryCard> = ArrayList(),
    var helperCards: MutableList<HelperCard>? = null
)