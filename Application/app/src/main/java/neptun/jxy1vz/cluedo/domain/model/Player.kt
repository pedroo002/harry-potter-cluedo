package neptun.jxy1vz.cluedo.domain.model

data class Player(
    val id: Int,
    val card: PlayerCard,
    var pos: Position,
    val tile: Int,
    val gender: Gender,
    var hp: Int = 70,
    var mysteryCards: MutableList<MysteryCard> = ArrayList(),
    var helperCards: MutableList<HelperCard>? = null,
    var conclusions: HashMap<String, Int>? = null,
    var suspicions: HashMap<String, Int>? = null,
    var solution: Suspect? = null
)

fun Player.fillSolution(type: MysteryType, mysteryName: String) {
    if (solution == null)
        solution = Suspect(id, "", "", "")
    when (type) {
        MysteryType.VENUE -> solution!!.room = mysteryName
        MysteryType.SUSPECT -> solution!!.suspect = mysteryName
        else -> solution!!.tool = mysteryName
    }
}

fun Player.hasSolution(): Boolean {
    return solution != null && solution!!.room.isNotEmpty() && solution!!.suspect.isNotEmpty() && solution!!.tool.isNotEmpty()
}

fun Player.updateConclusions(allMysteryCards: List<MysteryCard>) {
    for (conclusion in conclusions!!) {
        if (conclusion.value == -1) {
            for (card in allMysteryCards) {
                if (card.name == conclusion.key) {
                    fillSolution(card.type as MysteryType, card.name)
                    break
                }
            }
        }
    }
}

fun Player.getConclusion(mysteryName: String, cardHolderPlayerId: Int) {
    if (conclusions.isNullOrEmpty())
        conclusions = HashMap()

    if (cardHolderPlayerId == -2) {
        for (conclusion in conclusions!!) {
            if (conclusion.key == mysteryName)
                conclusion.setValue(cardHolderPlayerId)
        }
    }
    conclusions!![mysteryName] = cardHolderPlayerId
    suspicions?.let {
        if (suspicions!!.containsKey(mysteryName))
            suspicions!!.remove(mysteryName)
    }
}

fun Player.getSuspicion(suspect: Suspect, playerWhoShowed: Int? = null) {
    if (suspicions.isNullOrEmpty()) {
        suspicions = HashMap()
    }
    val suspectParams = suspect.let { listOf(it.room, it.suspect, it.tool) }
    for (suspectParam in suspectParams) {
        if (!ownCard(suspectParam)) {
            val otherTwo = ArrayList<String>()
            for (param in suspectParams) {
                if (param != suspectParam)
                    otherTwo.add(param)
            }
            if (hasConclusion(otherTwo[0]) && hasConclusion(otherTwo[1])) {
                if (playerWhoShowed == null) {
                    val type = when (suspectParam) {
                        suspect.room -> MysteryType.VENUE
                        suspect.suspect -> MysteryType.SUSPECT
                        else -> MysteryType.TOOL
                    }
                    fillSolution(type, suspectParam)
                }
                else
                    getConclusion(suspectParam, playerWhoShowed)
            }
        }
        if (!ownCard(suspectParam) && !hasConclusion(suspectParam)) {
            if (playerWhoShowed != null) {
                suspicions!![suspectParam] = playerWhoShowed
            } else {
                suspicions!![suspectParam] = -1
                suspicions!![suspectParam] = suspect.playerId
            }
        }
    }
}

fun Player.hasConclusion(name: String): Boolean {
    if (conclusions.isNullOrEmpty())
        return false
    return conclusions!!.containsKey(name)
}

fun Player.hasSuspicion(name: String): Boolean {
    if (suspicions.isNullOrEmpty())
        return false
    return suspicions!!.containsKey(name)
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
    if (solution != null && solution!!.tool.isNotEmpty())
        tool = solution!!.tool
    else
        do {
            tool = toolList.random()
        } while (hasConclusion(tool))

    var suspect: String
    if (solution != null && solution!!.suspect.isNotEmpty())
        suspect = solution!!.suspect
    else
        do {
            suspect = suspectList.random()
        } while (hasConclusion(suspect))

    return Suspect(id, room, tool, suspect)
}

enum class Gender {
    MAN,
    WOMAN
}