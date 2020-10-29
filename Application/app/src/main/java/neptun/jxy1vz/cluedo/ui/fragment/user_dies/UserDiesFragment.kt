package neptun.jxy1vz.cluedo.ui.fragment.user_dies

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentUserDiesBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class UserDiesFragment(private val player: Player, private val listener: DialogDismiss) : Fragment(), ViewModelListener {

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
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}