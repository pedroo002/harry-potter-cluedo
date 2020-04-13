package neptun.jxy1vz.cluedo.model

data class Player(
    val id: Int,
    val card: PlayerCard,
    var pos: Position,
    val tile: Int,
    var hp: Int = 70,
    var mysteryCards: MutableList<MysteryCard> = ArrayList(),
    var helperCards: MutableList<HelperCard>? = null,
    var conclusion: HashMap<String, MutableList<Int>>? = null
)

fun Player.getConclusion(mysteryName: String, cardHolderPlayerId: Int) {

}

fun Player.getSuspicion(suspect: Suspect, playerWhoShowed: Int? = null) {

}