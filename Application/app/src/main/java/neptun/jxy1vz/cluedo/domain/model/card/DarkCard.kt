package neptun.jxy1vz.cluedo.domain.model.card

data class DarkCard(
    override val id: Int,
    override val name: String,
    override val imageRes: Int,
    override val verso: Int,
    val type: CardType,
    val lossType: LossType,
    val hpLoss: Int,
    var helperIds: List<Int>? = null
) : Card(id, name, imageRes, verso)