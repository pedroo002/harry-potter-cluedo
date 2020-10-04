package neptun.jxy1vz.cluedo.ui.fragment.channel.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentChannelRootBinding
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.channel.create.CreateChannelFragment

class ChannelRootFragment(private val listener: MenuListener) : Fragment(), ViewModelListener {

    private lateinit var fragmentChannelRootBinding: FragmentChannelRootBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChannelRootBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_channel_root, container, false)
        fragmentChannelRootBinding.channelRootViewModel = ChannelRootViewModel(fragmentChannelRootBinding, this)
        return fragmentChannelRootBinding.root
    }

    override fun onFinish() {
        val createChannelFragment = CreateChannelFragment()
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.menuFrame, createChannelFragment).commit()
        listener.onFragmentClose()
    }
}