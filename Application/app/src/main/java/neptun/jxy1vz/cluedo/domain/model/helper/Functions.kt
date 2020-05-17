package neptun.jxy1vz.cluedo.domain.model.helper

import neptun.jxy1vz.cluedo.domain.model.DarkCard
import neptun.jxy1vz.cluedo.domain.model.HelperType
import neptun.jxy1vz.cluedo.domain.model.Player

fun getHelperObjects(player: Player, darkCard: DarkCard, tools: ArrayList<String>, spells: ArrayList<String>, allys: ArrayList<String>) {
    tools.add("")
    spells.add("")
    allys.add("")

    if (!player.helperCards.isNullOrEmpty()) {
        for (helperCard in player.helperCards!!) {
            if (!darkCard.helperIds.isNullOrEmpty() && darkCard.helperIds!!.contains(helperCard.id))
                when (helperCard.type) {
                    HelperType.TOOL -> addHelperToArray(helperCard.name, tools)
                    HelperType.SPELL -> addHelperToArray(helperCard.name, spells)
                    HelperType.ALLY -> addHelperToArray(helperCard.name, allys)
                }
        }
    }
}

private fun addHelperToArray(name: String, array: ArrayList<String>) {
    array.add(name)
}

fun getPixelsFromDp(dp: Int, density: Int): Int {
    return (density / 160) * dp
}