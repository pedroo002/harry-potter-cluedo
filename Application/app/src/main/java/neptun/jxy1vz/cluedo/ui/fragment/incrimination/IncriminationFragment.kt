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

class IncriminationFragment(private val gameModels: GameModels,
                            private val playerId: Int,
                            private val roomId: Int,
                            private val listener: MapInterface
) : Fragment(), IncriminationViewModel.IncriminationDialogInterface {

    interface MapInterface {
        fun getIncrimination(suspect: Suspect)
        fun onIncriminationSkip()
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
        return fragmentIncriminationBinding.root
    }

    override fun onIncriminationFinalization(tool: String, suspect: String) {
        listener.getIncrimination(Suspect(playerId, gameModels.roomList[roomId].name, tool, suspect))
        activity?.supportFragmentManager!!.beginTransaction().remove(this).commit()
    }

    override fun onSkip() {
        listener.onIncriminationSkip()
        activity?.supportFragmentManager!!.beginTransaction().remove(this).commit()
    }
}