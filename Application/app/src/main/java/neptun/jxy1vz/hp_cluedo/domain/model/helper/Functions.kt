package neptun.jxy1vz.hp_cluedo.domain.model.helper

import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.domain.model.card.DarkCard

fun getHelperObjects(player: Player, darkCard: DarkCard): ArrayList<String> {
    val helperObjects = ArrayList<String>()
    if (!player.helperCards.isNullOrEmpty()) {
        player.helperCards!!.forEach { helperCard ->
            if (!darkCard.helperIds.isNullOrEmpty() && darkCard.helperIds!!.contains(helperCard.id))
                helperObjects.add(helperCard.name)
        }
    }
    return helperObjects
}