package neptun.jxy1vz.hp_cluedo.domain.model.card

data class PlayerCard(
    override val id: Int,
    override val name: String,
    override val imageRes: String = "",
    override val verso: String = ""
) : Card(id, name, imageRes, verso)