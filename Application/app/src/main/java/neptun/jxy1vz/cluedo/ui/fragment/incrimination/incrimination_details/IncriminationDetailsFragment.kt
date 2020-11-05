package neptun.jxy1vz.cluedo.ui.fragment.incrimination.incrimination_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentIncriminationDetailsBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.domain.util.toApiModel
import neptun.jxy1vz.cluedo.network.model.message.suspect.SuspectMessage
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class IncriminationDetailsFragment(private val suspect: Suspect, private val listener: DialogDismiss) : Fragment(), ViewModelListener,
    IncriminationDetailsViewModel.DetailsFragmentListener {

    private lateinit var fragmentIncriminationDetailsBinding: FragmentIncriminationDetailsBinding
    private var needToTakeNotes = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentIncriminationDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_incrimination_details, container, false)
        fragmentIncriminationDetailsBinding.incriminationDetailsViewModel = IncriminationDetailsViewModel(fragmentIncriminationDetailsBinding, context!!, suspect, this, this)
        return fragmentIncriminationDetailsBinding.root
    }

    override fun onResume() {
        super.onResume()
        if (MapViewModel.isGameModeMulti() && suspect.playerId == MapViewModel.mPlayerId) {
            GlobalScope.launch(Dispatchers.IO) {
                MapViewModel.retrofit.apply {
                    val suspectMessage = suspect.toApiModel()
                    val json = moshi.adapter(SuspectMessage::class.java).toJson(suspectMessage)
                    val body =
                        json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    cluedo.sendIncrimination(MapViewModel.channelName, body)
                }
            }
        }
    }

    override fun onFinish() {
        listener.onIncriminationDetailsDismiss(needToTakeNotes)
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }

    override fun deliverInformation(needToTakeNotes: Boolean) {
        this.needToTakeNotes = needToTakeNotes
    }
}