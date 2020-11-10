package neptun.jxy1vz.cluedo.ui.fragment.cards.card_loss

import android.content.Context
import androidx.databinding.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.domain.model.card.HelperCard
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel

class CardLossViewModel(
    private val context: Context,
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
        if (MapViewModel.isGameModeMulti())
            sendCardThrowEvent(selectedCard.name)
        else
            MapViewModel.player.helperCards!!.remove(selectedCard)
        listener.onFinish()
    }

    private fun sendCardThrowEvent(cardName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            RetrofitInstance.getInstance(context).cluedo.sendCardThrowEvent(MapViewModel.channelName, MapViewModel.mPlayerId!!, cardName)
        }
    }
}