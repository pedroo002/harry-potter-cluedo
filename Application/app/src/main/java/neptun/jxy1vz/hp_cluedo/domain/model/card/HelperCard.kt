package neptun.jxy1vz.hp_cluedo.domain.model.card

data class HelperCard(
    override val id: Int,
    override val name: String,
    val type: CardType,
    var count: Int = 1,
    var numberOfHelpingCases: Int = 0,
    override val imageRes: String = "",
    override val verso: String = ""
) : Card(id, name, imageRes, verso)