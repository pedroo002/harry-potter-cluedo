package neptun.jxy1vz.cluedo.ui.fragment.user_dies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentUserDiesBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class UserDiesFragment : Fragment(), ViewModelListener {

    private lateinit var player: Player
    private lateinit var listener: DialogDismiss

    fun setArgs(p: Player, l: DialogDismiss) {
        player = p
        listener = l
    }

    companion object {
        fun newInstance(player: Player, listener: DialogDismiss) : UserDiesFragment {
            val fragment = UserDiesFragment()
            fragment.setArgs(player, listener)
            return fragment
        }
    }

    private lateinit var fragmentUserDiesBinding: FragmentUserDiesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserDiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_dies, container, false)
        fragmentUserDiesBinding.userDiesViewModel = UserDiesViewModel(fragmentUserDiesBinding, context!!, player, this)
        return fragmentUserDiesBinding.root
    }

    override fun onFinish() {
        listener.onPlayerDiesDismiss(null)
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}