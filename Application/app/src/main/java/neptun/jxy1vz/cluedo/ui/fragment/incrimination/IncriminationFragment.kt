package neptun.jxy1vz.cluedo.ui.fragment.incrimination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentIncriminationBinding
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel

class IncriminationFragment : Fragment(), IncriminationViewModel.IncriminationDialogInterface {

    private lateinit var gameModels: GameModels
    private var playerId: Int = 0
    private var roomId: Int = 0
    private lateinit var listener: MapInterface

    fun setArgs(gm: GameModels, pId: Int, rId: Int, l: MapInterface) {
        gameModels = gm
        playerId = pId
        roomId = rId
        listener = l
    }

    companion object {
        fun newInstance(gameModels: GameModels, playerId: Int, roomId: Int, listener: MapInterface) : IncriminationFragment {
            val fragment = IncriminationFragment()
            fragment.setArgs(gameModels, playerId, roomId, listener)
            return fragment
        }
    }

    interface MapInterface {
        fun getIncrimination(suspect: Suspect)
    }

    private lateinit var fragmentIncriminationBinding: FragmentIncriminationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentIncriminationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_incrimination, container, false)
        val roomName = gameModels.roomList[roomId].name
        fragmentIncriminationBinding.incriminationViewModel = IncriminationViewModel(fragmentIncriminationBinding, context!!, roomName, this)
        fragmentIncriminationBinding.btnSkip.isEnabled = !MapViewModel.userHasToIncriminate
        return fragmentIncriminationBinding.root
    }

    override fun onIncriminationFinalization(tool: String, suspect: String) {
        listener.getIncrimination(Suspect(playerId, gameModels.roomList[roomId].name, tool, suspect))
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }

    override fun onSkip() {
        MapViewModel.enableScrolling()
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}