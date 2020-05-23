package neptun.jxy1vz.cluedo.domain.model.card

data class MysteryCard(
    override val id: Int,
    override val name: String,
    override val imageRes: Int,
    override val verso: Int,
    val type: CardType
) : Card(id, name, imageRes, verso)