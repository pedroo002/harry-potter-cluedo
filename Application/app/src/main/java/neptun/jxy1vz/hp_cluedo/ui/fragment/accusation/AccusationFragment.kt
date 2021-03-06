package neptun.jxy1vz.hp_cluedo.ui.fragment.accusation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentAccusationBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.domain.model.Suspect
import neptun.jxy1vz.hp_cluedo.domain.util.toApiModel
import neptun.jxy1vz.hp_cluedo.data.network.model.message.SuspectMessage
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException

class AccusationFragment : Fragment(),
    AccusationViewModel.FinalizationListener {

    private var playerId: Int = 0
    private lateinit var listener: DialogDismiss

    fun setArgs(id: Int, l: DialogDismiss) {
        playerId = id
        listener = l
    }

    companion object {
        fun newInstance(playerId: Int, listener: DialogDismiss): AccusationFragment {
            val fragment = AccusationFragment()
            fragment.setArgs(playerId, listener)
            return fragment
        }
    }

    private lateinit var fragmentAccusationBinding: FragmentAccusationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAccusationBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_accusation, container, false)
        fragmentAccusationBinding.accusationViewModel =
            AccusationViewModel(playerId, fragmentAccusationBinding, context!!, this)
        return fragmentAccusationBinding.root
    }

    override fun onFinalized(suspect: Suspect) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (MapViewModel.isGameModeMulti()) {
                    val suspectMessage = suspect.toApiModel()
                    val json = MapViewModel.retrofit.moshi.adapter(SuspectMessage::class.java)
                        .toJson(suspectMessage)
                    val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    MapViewModel.retrofit.cluedo.sendAccusation(MapViewModel.channelName, body)
                }
                withContext(Dispatchers.Main) {
                    listener.onAccusationDismiss(suspect)
                    MapViewModel.fm.beginTransaction().remove(this@AccusationFragment).commit()
                }
            }
            catch (ex: HttpException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(fragmentAccusationBinding.root, ex.message ?: "Hiba lépett fel a hálózatban.", Snackbar.LENGTH_LONG).show()
                }
            }
            catch (ex: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(fragmentAccusationBinding.root, "A kapcsolat túllépte az időkorlátot!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}