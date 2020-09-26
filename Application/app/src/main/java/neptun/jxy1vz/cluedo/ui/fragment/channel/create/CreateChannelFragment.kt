package neptun.jxy1vz.cluedo.ui.fragment.channel.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class CreateChannelFragment : Fragment(), ViewModelListener {

    private lateinit var fragmentCreateChannelBinding : FragmentCreateChannelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCreateChannelBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_channel, container, false)
        fragmentCreateChannelBinding.createChannelViewModel = CreateChannelViewModel()
        return fragmentCreateChannelBinding.root
    }

    override fun onFinish() {
        TODO("Not yet implemented")
    }
}