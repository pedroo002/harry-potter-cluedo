package neptun.jxy1vz.hp_cluedo.ui.fragment.user_dies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentUserDiesBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.domain.model.BasePlayer
import neptun.jxy1vz.hp_cluedo.domain.model.ThinkingPlayer
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class UserDiesFragment : Fragment(), ViewModelListener {

    private lateinit var player: BasePlayer
    private lateinit var listener: DialogDismiss

    fun setArgs(p: BasePlayer, l: DialogDismiss) {
        player = p
        listener = l
    }

    companion object {
        fun newInstance(player: BasePlayer, listener: DialogDismiss) : UserDiesFragment {
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