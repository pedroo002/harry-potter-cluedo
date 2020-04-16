package neptun.jxy1vz.cluedo.domain.util

import neptun.jxy1vz.cluedo.database.model.*
import neptun.jxy1vz.cluedo.database.model.CardType.*
import neptun.jxy1vz.cluedo.database.model.LossType
import neptun.jxy1vz.cluedo.domain.model.*
import neptun.jxy1vz.cluedo.domain.model.CardType
import neptun.jxy1vz.cluedo.ui.dialog.dice.DiceRollerViewModel


fun CardDBmodel.toDomainModel(): Card {
    return when (cardType) {
        "HELPER_TOOL", "HELPER_SPELL", "HELPER_ALLY"-> HelperCard(
            id.toInt(),
            name,
            imageRes,
            versoRes,
            toCardType(cardType).toDomainModel()
        )
        "DARK_CORRIDOR", "DARK_PLAYER_IN_TURN", "DARK_ROOM_BAGOLYHAZ", "DARK_ROOM_BAJITALTAN", "DARK_ROOM_GYENGELKEDO",
        "DARK_ROOM_JOSLASTAN", "DARK_ROOM_KONYVTAR", "DARK_ROOM_NAGYTEREM", "DARK_ROOM_SERLEG_TEREM", "DARK_ROOM_SVK",
        "DARK_ROOM_SZUKSEG_SZOBAJA", "DARK_ALL_PLAYERS", "DARK_MEN", "DARK_WOMEN"
        -> DarkCard(
            id.toInt(),
            name,
            imageRes,
            versoRes,
            toCardType(cardType).toDomainModel(),
            toLossType(lossType!!).toDomainModel(),
            hpLoss!!
        )
        "MYSTERY_TOOL", "MYSTERY_SUSPECT", "MYSTERY_VENUE"-> MysteryCard(
            id.toInt(),
            name,
            imageRes,
            versoRes,
            toCardType(cardType).toDomainModel())
        else -> PlayerCard(id.toInt(), name, imageRes, versoRes)
    }
}

fun Card.toDatabaseModel(owner: Int?): CardDBmodel {
    return when (this) {
        is HelperCard -> CardDBmodel(this.id.toLong(), this.name, this.imageRes, this.verso, this.type.toDatabaseModel().string(), owner)
        is DarkCard -> CardDBmodel(this.id.toLong(), this.name, this.imageRes, this.verso, this.type.toDatabaseModel().string(), owner, this.lossType.toDatabaseModel().string(), this.hpLoss)
        is MysteryCard -> CardDBmodel(this.id.toLong(), this.name, this.imageRes, this.verso, this.type.toDatabaseModel().string())
        else -> CardDBmodel(this.id.toLong(), this.name, this.imageRes, this.verso, "PLAYER")
    }
}

fun neptun.jxy1vz.cluedo.database.model.CardType.toDomainModel(): CardType {
    return when (this) {
        HELPER_TOOL -> HelperType.TOOL
        HELPER_SPELL -> HelperType.SPELL
        HELPER_ALLY -> HelperType.ALLY
        DARK_ALL_PLAYERS -> DarkType.ALL_PLAYERS
        DARK_CORRIDOR -> DarkType.CORRIDOR
        DARK_MEN -> DarkType.GENDER_MEN
        DARK_WOMEN -> DarkType.GENDER_WOMEN
        DARK_PLAYER_IN_TURN -> DarkType.PLAYER_IN_TURN
        DARK_ROOM_BAGOLYHAZ -> DarkType.ROOM_BAGOLYHAZ
        DARK_ROOM_BAJITALTAN -> DarkType.ROOM_BAJITALTAN
        DARK_ROOM_GYENGELKEDO -> DarkType.ROOM_GYENGELKEDO
        DARK_ROOM_JOSLASTAN -> DarkType.ROOM_JOSLASTAN
        DARK_ROOM_KONYVTAR -> DarkType.ROOM_KONYVTAR
        DARK_ROOM_NAGYTEREM -> DarkType.ROOM_NAGYTEREM
        DARK_ROOM_SERLEG_TEREM -> DarkType.ROOM_SERLEG
        DARK_ROOM_SVK -> DarkType.ROOM_SVK
        DARK_ROOM_SZUKSEG_SZOBAJA -> DarkType.ROOM_SZUKSEG_SZOBAJA
        MYSTERY_TOOL -> MysteryType.TOOL
        MYSTERY_SUSPECT -> MysteryType.SUSPECT
        MYSTERY_VENUE -> MysteryType.VENUE
        else -> HelperType.TOOL
    }
}

fun CardType.toDatabaseModel(): neptun.jxy1vz.cluedo.database.model.CardType {
    return when (this) {
        HelperType.TOOL -> HELPER_TOOL
        HelperType.SPELL -> HELPER_SPELL
        HelperType.ALLY -> HELPER_ALLY
        DarkType.ALL_PLAYERS -> DARK_ALL_PLAYERS
        DarkType.CORRIDOR -> DARK_CORRIDOR
        DarkType.GENDER_MEN -> DARK_MEN
        DarkType.GENDER_WOMEN -> DARK_WOMEN
        DarkType.PLAYER_IN_TURN -> DARK_PLAYER_IN_TURN
        DarkType.ROOM_BAGOLYHAZ -> DARK_ROOM_BAGOLYHAZ
        DarkType.ROOM_BAJITALTAN -> DARK_ROOM_BAJITALTAN
        DarkType.ROOM_GYENGELKEDO -> DARK_ROOM_GYENGELKEDO
        DarkType.ROOM_JOSLASTAN -> DARK_ROOM_JOSLASTAN
        DarkType.ROOM_KONYVTAR -> DARK_ROOM_KONYVTAR
        DarkType.ROOM_NAGYTEREM -> DARK_ROOM_NAGYTEREM
        DarkType.ROOM_SERLEG -> DARK_ROOM_SERLEG_TEREM
        DarkType.ROOM_SVK -> DARK_ROOM_SVK
        DarkType.ROOM_SZUKSEG_SZOBAJA -> DARK_ROOM_SZUKSEG_SZOBAJA
        MysteryType.TOOL -> MYSTERY_TOOL
        MysteryType.SUSPECT -> MYSTERY_SUSPECT
        MysteryType.VENUE -> MYSTERY_VENUE
        else -> PLAYER
    }
}

private val darkTypes = listOf(DARK_ALL_PLAYERS, DARK_PLAYER_IN_TURN, DARK_CORRIDOR, DARK_MEN, DARK_WOMEN, DARK_ROOM_BAGOLYHAZ, DARK_ROOM_BAJITALTAN, DARK_ROOM_GYENGELKEDO, DARK_ROOM_JOSLASTAN, DARK_ROOM_KONYVTAR, DARK_ROOM_NAGYTEREM, DARK_ROOM_SERLEG_TEREM, DARK_ROOM_SVK, DARK_ROOM_SZUKSEG_SZOBAJA)
private val helperTypes = listOf(HELPER_TOOL, HELPER_SPELL, HELPER_ALLY)

fun DiceRollerViewModel.CardType.toDatabaseModel(): neptun.jxy1vz.cluedo.database.model.CardType {
    return when (this) {
        DiceRollerViewModel.CardType.HELPER -> helperTypes.random()
        else -> darkTypes.random()
    }
}

fun DiceRollerViewModel.CardType.toDomainModel(): CardType {
    return when (this) {
        DiceRollerViewModel.CardType.HELPER -> helperTypes.random().toDomainModel()
        else -> darkTypes.random().toDomainModel()
    }
}

fun LossType.toDomainModel(): neptun.jxy1vz.cluedo.domain.model.LossType {
    return when (this) {
        LossType.HP -> neptun.jxy1vz.cluedo.domain.model.LossType.HP
        LossType.TOOL_CARD -> neptun.jxy1vz.cluedo.domain.model.LossType.TOOL
        LossType.SPELL_CARD -> neptun.jxy1vz.cluedo.domain.model.LossType.SPELL
        else -> neptun.jxy1vz.cluedo.domain.model.LossType.ALLY
    }
}

fun neptun.jxy1vz.cluedo.domain.model.LossType.toDatabaseModel(): LossType {
    return when (this) {
        neptun.jxy1vz.cluedo.domain.model.LossType.HP -> LossType.HP
        neptun.jxy1vz.cluedo.domain.model.LossType.TOOL -> LossType.TOOL_CARD
        neptun.jxy1vz.cluedo.domain.model.LossType.SPELL -> LossType.SPELL_CARD
        else -> LossType.ALLY_CARD
    }
}