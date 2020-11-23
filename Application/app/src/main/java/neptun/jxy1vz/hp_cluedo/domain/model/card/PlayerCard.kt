package neptun.jxy1vz.hp_cluedo.domain.model.card

data class PlayerCard(
    override val id: Int,
    override val name: String,
    override val imageRes: Int,
    override val verso: Int
) : Card(id, name, imageRes, verso)