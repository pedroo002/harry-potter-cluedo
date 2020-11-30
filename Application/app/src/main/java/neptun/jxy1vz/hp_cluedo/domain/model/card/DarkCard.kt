package neptun.jxy1vz.hp_cluedo.domain.model.card

data class DarkCard(
    override val id: Int,
    override val name: String,
    val type: CardType,
    val lossType: LossType,
    val hpLoss: Int,
    var helperIds: List<Int>? = null,
    override val imageRes: String = "",
    override val verso: String = ""
) : Card(id, name, imageRes, verso)