package neptun.jxy1vz.hp_cluedo.ui.fragment.incrimination.incrimination_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.FragmentIncriminationDetailsBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.domain.model.Suspect
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.domain.util.toApiModel
import neptun.jxy1vz.hp_cluedo.network.model.message.SuspectMessage
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class IncriminationDetailsFragment : Fragment(), ViewModelListener {

    private lateinit var suspect: Suspect
    private lateinit var listener: DialogDismiss

    fun setArgs(sus: Suspect, l: DialogDismiss) {
        suspect = sus
        listener = l
    }

    companion object {
        fun newInstance(suspect: Suspect, listener: DialogDismiss): IncriminationDetailsFragment {
            val fragment = IncriminationDetailsFragment()
            fragment.setArgs(suspect, listener)
            return fragment
        }
    }

    private lateinit var fragmentIncriminationDetailsBinding: FragmentIncriminationDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentIncriminationDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_incrimination_details, container, false)
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                val cross = getAssetByTag("resources/map/note/cross.png")!!.url
                val skip = getAssetByTag("resources/map/other/skip.png")!!.url
                val cardVerso = getAssetByTag("resources/cards/mystery/rejtely22_hatlap.jpg")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(cross, context!!, fragmentIncriminationDetailsBinding.ivCross)
                    loadUrlImageIntoImageView(skip, context!!, fragmentIncriminationDetailsBinding.ivSkipBubble)
                    loadUrlImageIntoImageView(cardVerso, context!!, fragmentIncriminationDetailsBinding.ivFloatingCard)
                    fragmentIncriminationDetailsBinding.incriminationDetailsViewModel = IncriminationDetailsViewModel(fragmentIncriminationDetailsBinding, context!!, suspect, this@IncriminationDetailsFragment)
                }
            }
        }
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
        listener.onIncriminationDetailsDismiss()
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}