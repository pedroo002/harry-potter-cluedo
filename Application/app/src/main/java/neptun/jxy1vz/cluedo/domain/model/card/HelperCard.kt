package neptun.jxy1vz.cluedo.domain.model.card

data class HelperCard(
    override val id: Int,
    override val name: String,
    override val imageRes: Int,
    override val verso: Int,
    val type: CardType,
    var count: Int = 1,
    var numberOfHelpingCases: Int = 0
) : Card(id, name, imageRes, verso)