package neptun.jxy1vz.cluedo.domain.model.helper

import neptun.jxy1vz.cluedo.domain.model.card.DarkCard
import neptun.jxy1vz.cluedo.domain.model.card.HelperType
import neptun.jxy1vz.cluedo.domain.model.Player

fun getHelperObjects(player: Player, darkCard: DarkCard): ArrayList<String> {
    val helperObjects = ArrayList<String>()
    if (!player.helperCards.isNullOrEmpty()) {
        for (helperCard in player.helperCards!!) {
            if (!darkCard.helperIds.isNullOrEmpty() && darkCard.helperIds!!.contains(helperCard.id))
                helperObjects.add(helperCard.name)
        }
    }
    return helperObjects
}

fun getPixelsFromDp(dp: Int, density: Int): Int {
    return (density / 160) * dp
}