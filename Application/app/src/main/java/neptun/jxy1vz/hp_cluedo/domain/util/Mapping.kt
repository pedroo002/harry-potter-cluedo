package neptun.jxy1vz.hp_cluedo.domain.util

import neptun.jxy1vz.hp_cluedo.database.model.*
import neptun.jxy1vz.hp_cluedo.database.model.CardType.*
import neptun.jxy1vz.hp_cluedo.database.model.LossType
import neptun.jxy1vz.hp_cluedo.domain.model.*
import neptun.jxy1vz.hp_cluedo.domain.model.card.*
import neptun.jxy1vz.hp_cluedo.domain.model.card.CardType
import neptun.jxy1vz.hp_cluedo.network.model.message.suspect.SuspectMessage

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
            toCardType(cardType).toDomainModel()
        )
        else -> PlayerCard(
            id.toInt(),
            name,
            imageRes,
            versoRes
        )
    }
}

fun neptun.jxy1vz.hp_cluedo.database.model.CardType.toDomainModel(): CardType {
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

fun CardType.toDatabaseModel(): neptun.jxy1vz.hp_cluedo.database.model.CardType {
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

fun LossType.toDomainModel(): neptun.jxy1vz.hp_cluedo.domain.model.card.LossType {
    return when (this) {
        LossType.HP -> neptun.jxy1vz.hp_cluedo.domain.model.card.LossType.HP
        LossType.TOOL_CARD -> neptun.jxy1vz.hp_cluedo.domain.model.card.LossType.TOOL
        LossType.SPELL_CARD -> neptun.jxy1vz.hp_cluedo.domain.model.card.LossType.SPELL
        else -> neptun.jxy1vz.hp_cluedo.domain.model.card.LossType.ALLY
    }
}

fun neptun.jxy1vz.hp_cluedo.domain.model.card.LossType.toDatabaseModel(): LossType {
    return when (this) {
        neptun.jxy1vz.hp_cluedo.domain.model.card.LossType.HP -> LossType.HP
        neptun.jxy1vz.hp_cluedo.domain.model.card.LossType.TOOL -> LossType.TOOL_CARD
        neptun.jxy1vz.hp_cluedo.domain.model.card.LossType.SPELL -> LossType.SPELL_CARD
        else -> LossType.ALLY_CARD
    }
}

fun Note.toDatabaseModel(): NoteDBmodel {
    return NoteDBmodel(0, this.row, this.col, this.nameRes)
}

fun SuspectMessage.toDomainModel(): Suspect {
    return Suspect(playerId, room, tool, suspect)
}

fun Suspect.toApiModel(): SuspectMessage {
    return SuspectMessage(playerId, room, tool, suspect)
}