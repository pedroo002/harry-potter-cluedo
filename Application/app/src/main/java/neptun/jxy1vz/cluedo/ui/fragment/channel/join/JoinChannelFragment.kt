package neptun.jxy1vz.cluedo.ui.fragment.channel.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentJoinChannelBinding
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class JoinChannelFragment : Fragment(), ViewModelListener {

    private lateinit var fragmentJoinChannelBinding: FragmentJoinChannelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentJoinChannelBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_channel, container, false)
        fragmentJoinChannelBinding.joinChannelViewModel = JoinChannelViewModel()
        return fragmentJoinChannelBinding.root
    }

    override fun onFinish() {
        TODO("Not yet implemented")
    }
}