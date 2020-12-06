package neptun.jxy1vz.hp_cluedo.ui.fragment.cards.card_loss

import android.content.Context
import androidx.databinding.BaseObservable
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.domain.model.card.HelperCard
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.databinding.FragmentCardLossBinding
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import retrofit2.HttpException
import java.net.SocketTimeoutException

class CardLossViewModel(
    private val context: Context,
    private val bind: FragmentCardLossBinding,
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
        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).cardDao().throwCard(selectedCard.id.toLong())
            withContext(Dispatchers.Main) {
                MapViewModel.player.helperCards!!.remove(selectedCard)
                if (MapViewModel.isGameModeMulti())
                    sendCardThrowEvent(selectedCard.name)
                else
                    listener.onFinish()
            }
        }
    }

    private suspend fun sendCardThrowEvent(cardName: String) {
        withContext(Dispatchers.IO) {
            try {
                RetrofitInstance.getInstance(context).cluedo.sendCardThrowEvent(MapViewModel.channelName, MapViewModel.mPlayerId!!, cardName)
                withContext(Dispatchers.Main) {
                    listener.onFinish()
                }
            }
            catch (ex: HttpException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(bind.root, ex.message ?: "Hiba lépett fel a hálózatban.", Snackbar.LENGTH_LONG).show()
                }
            }
            catch (ex: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(bind.root, "A kapcsolat túllépte az időkorlátot!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}