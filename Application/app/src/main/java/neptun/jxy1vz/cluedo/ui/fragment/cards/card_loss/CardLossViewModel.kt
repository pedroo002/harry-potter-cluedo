package neptun.jxy1vz.cluedo.ui.fragment.cards.card_loss

import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.domain.model.card.HelperCard
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel

class CardLossViewModel(
    title: String,
    private val listener: ViewModelListener
) : BaseObservable() {

    private var title = ""
    lateinit var selectedCard: HelperCard

    init {
        this.title = title
    }

    fun getTitle(): String {
        return title
    }

    fun throwCard() {
        MapViewModel.player.helperCards!!.remove(selectedCard)
        listener.onFinish()
    }
}