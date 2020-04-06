package neptun.jxy1vz.cluedo.model

data class Player(
    var id: Int,
    var pos: Position,
    var hp: Int = 70,
    var mysteryCards: MutableList<MysteryCard> = ArrayList(),
    var helperCards: MutableList<HelperCard>? = null
)