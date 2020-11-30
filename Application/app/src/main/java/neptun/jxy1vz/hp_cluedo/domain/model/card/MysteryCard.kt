package neptun.jxy1vz.hp_cluedo.domain.model.card

data class MysteryCard(
    override val id: Int,
    override val name: String,
    val type: CardType,
    override val imageRes: String = "",
    override val verso: String = ""
) : Card(id, name, imageRes, verso)