package neptun.jxy1vz.cluedo.domain.model

import kotlin.random.Random

data class Player(
    val id: Int,
    val card: PlayerCard,
    var pos: Position,
    val tile: Int,
    val gender: Gender,
    var hp: Int = 70,
    var mysteryCards: MutableList<MysteryCard> = ArrayList(),
    var helperCards: MutableList<HelperCard>? = null,
    var conclusion: ArrayList<Pair<String, Int>>? = null,
    var suspicion: HashMap<String, MutableList<Int>>? = null
)

fun Player.getConclusion(mysteryName: String, cardHolderPlayerId: Int) {
    if (conclusion.isNullOrEmpty())
        conclusion = ArrayList()
    conclusion!!.add(Pair(mysteryName, cardHolderPlayerId))
    suspicion?.let {
        if (suspicion!!.containsKey(mysteryName))
            suspicion!!.remove(mysteryName)
    }
}

fun Player.getSuspicion(suspect: Suspect, playerWhoShowed: Int? = null) {
    if (suspicion.isNullOrEmpty()) {
        suspicion = HashMap()
    }
    for (suspectParam in suspect.let { listOf(it.room, it.suspect, it.tool) }) {
        if (!ownCard(suspectParam) && !hasConclusion(suspectParam)) {
            if (playerWhoShowed != null) {
                if (suspicion!!.containsKey(suspectParam)) {
                    suspicion!![suspectParam]!!.add(playerWhoShowed)
                } else {
                    suspicion!![suspectParam] = ArrayList()
                    suspicion!![suspectParam]!!.add(playerWhoShowed)
                }
            } else {
                suspicion!![suspectParam] = ArrayList()
                suspicion!![suspectParam]!!.add(suspect.playerId)
                suspicion!![suspectParam]!!.add(-1)
            }
        }
    }
}

fun Player.hasConclusion(name: String): Boolean {
    if (conclusion.isNullOrEmpty())
        return false
    for (pair in conclusion!!) {
        if (pair.first == name)
            return true
    }
    return false
}

fun Player.ownCard(name: String): Boolean {
    for (card in mysteryCards) {
        if (card.name == name)
            return true
    }
    return false
}

fun Player.getRandomSuspect(
    room: String,
    toolList: Array<String>,
    suspectList: Array<String>
): Suspect {
    var tool: String
    do {
        tool = toolList[Random.nextInt(0, 6)]
        var notOwnCard = true
        for (mc in mysteryCards) {
            if (mc.name == tool)
                notOwnCard = false
        }
    } while (!notOwnCard)
    var suspect: String
    do {
        suspect = suspectList[Random.nextInt(0, 6)]
        var notOwnCard = true
        for (mc in mysteryCards) {
            if (mc.name == suspect)
                notOwnCard = false
        }
    } while (!notOwnCard)

    return Suspect(id, room, tool, suspect)
}

enum class Gender {
    MAN,
    WOMAN
}