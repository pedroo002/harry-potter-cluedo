package neptun.jxy1vz.hp_cluedo.domain.model

import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.domain.model.card.HelperCard
import neptun.jxy1vz.hp_cluedo.domain.model.card.MysteryCard
import neptun.jxy1vz.hp_cluedo.domain.model.card.MysteryType
import neptun.jxy1vz.hp_cluedo.domain.model.card.PlayerCard
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mContext

class Player(
    val id: Int,
    val card: PlayerCard,
    var pos: Position,
    val tile: Int,
    val gender: Gender,
    var hp: Int = 70,
    var mysteryCards: MutableList<MysteryCard> = ArrayList(),
    var helperCards: MutableList<HelperCard>? = null,
    private var conclusions: HashMap<String, Int>? = null,
    private var suspicions: HashMap<String, Int>? = null,
    private var revealedMysteryCards: HashMap<Int, String>? = null,
    var solution: Suspect? = null
) {

    fun updateConclusions(allMysteryCards: List<MysteryCard>) {
        conclusions!!.filter { con -> con.value == -1 }.forEach { conclusion ->
            allMysteryCards.filter { c -> c.name == conclusion.key }.forEach {card ->
                fillSolution(card.type as MysteryType, card.name)
            }
        }
    }

    fun getConclusion(mysteryName: String, cardHolderPlayerId: Int) {
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

        solution?.let {
            when {
                solution!!.room == mysteryName -> solution!!.room = ""
                solution!!.tool == mysteryName -> solution!!.tool = ""
                solution!!.suspect == mysteryName -> solution!!.suspect = ""
            }
        }
    }

    fun getSuspicion(suspect: Suspect, playerWhoShowed: Int? = null) {
        if (suspicions.isNullOrEmpty()) {
            suspicions = HashMap()
        }
        val suspectParams = suspect.let { listOf(it.room, it.suspect, it.tool) }
        suspectParams.forEach { suspectParam ->
            if (!ownCard(suspectParam)) {
                val otherTwo = ArrayList<String>()
                suspectParams.forEach { param ->
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
                    } else if (conclusions!![otherTwo[0]] == playerWhoShowed || conclusions!![otherTwo[0]] == playerWhoShowed) {
                        suspicions!![suspectParam] = playerWhoShowed
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

    fun hasConclusion(name: String): Boolean {
        if (conclusions.isNullOrEmpty())
            return false
        return conclusions!!.containsKey(name)
    }

    fun hasSuspicion(name: String): Boolean {
        if (suspicions.isNullOrEmpty())
            return false
        return suspicions!!.containsKey(name)
    }

    fun ownCard(name: String): Boolean {
        mysteryCards.forEach { card ->
            if (card.name == name)
                return true
        }
        return false
    }

    private fun containsAny(list: Array<String>): Boolean {
        if (conclusions.isNullOrEmpty())
            return false
        list.forEach { item ->
            if (conclusions!!.containsKey(item))
                return true
        }
        return false
    }

    fun generateSuspect(
        room: String,
        toolList: Array<String>,
        suspectList: Array<String>
    ): Suspect {
        var tool: String
        if (solution != null && solution!!.tool.isNotEmpty())
            tool = solution!!.tool
        else if (containsAny(toolList))
            do {
                tool = toolList.random()
            } while (hasConclusion(tool))
        else
            tool = toolList.random()

        var suspect: String
        if (solution != null && solution!!.suspect.isNotEmpty())
            suspect = solution!!.suspect
        else if (containsAny(suspectList))
            do {
                suspect = suspectList.random()
            } while (hasConclusion(suspect))
        else
            suspect = suspectList.random()

        return Suspect(id, room, tool, suspect)
    }

    fun fillSolution(type: MysteryType, mysteryName: String) {
        if (solution == null)
            solution = Suspect(id, "", "", "")
        if (hasConclusion(mysteryName) && conclusions!![mysteryName] != -1)
            return
        when (type) {
            MysteryType.VENUE -> solution!!.room = mysteryName
            MysteryType.SUSPECT -> solution!!.suspect = mysteryName
            else -> solution!!.tool = mysteryName
        }
    }

    fun hasSolution(): Boolean {
        return solution != null && solution!!.room.isNotEmpty() && solution!!.suspect.isNotEmpty() && solution!!.tool.isNotEmpty()
    }

    fun revealCardToPlayer(playerId: Int, cardOptions: List<MysteryCard>): MysteryCard {
        if (revealedMysteryCards.isNullOrEmpty()) {
            revealedMysteryCards = HashMap()
            val cardToReveal = cardOptions.random()
            revealedMysteryCards!![playerId] = cardToReveal.name
            return cardToReveal
        }

        if (revealedMysteryCards!!.containsKey(playerId)) {
            cardOptions.forEach { card ->
                revealedMysteryCards!!.entries.find { entry -> entry.key == playerId && entry.value == card.name
                    return card
                }
            }
        }
        val cardToReveal = cardOptions.random()
        revealedMysteryCards!![playerId] = cardToReveal.name
        return cardToReveal
    }

    fun hasAlohomora(): Boolean {
        if (helperCards.isNullOrEmpty())
            return false
        if (helperCards!!.map { card -> card.name }.contains(mContext!!.resources.getStringArray(R.array.helper_cards)[26]))
            return true
        return false
    }

    fun hasFelixFelicis(): Boolean {
        if (helperCards.isNullOrEmpty())
            return false
        if (helperCards!!.map { card -> card.name }.contains(mContext!!.resources.getStringArray(R.array.helper_cards)[9]))
            return true
        return false
    }

    enum class Gender {
        MAN,
        WOMAN
    }
}