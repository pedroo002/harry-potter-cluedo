package neptun.jxy1vz.cluedo.ui.fragment.channel.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentChannelRootBinding
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuActivity
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.channel.create.CreateChannelFragment
import neptun.jxy1vz.cluedo.ui.fragment.channel.join.JoinChannelFragment

class ChannelRootFragment : Fragment(), ViewModelListener {

    private lateinit var listener: MenuListener

    fun setListener(l: MenuListener) {
        listener = l
    }

    companion object {
        fun newInstance(listener: MenuListener): ChannelRootFragment {
            val fragment = ChannelRootFragment()
            fragment.setListener(listener)
            return fragment
        }
    }

    private lateinit var fragmentChannelRootBinding: FragmentChannelRootBinding
    private lateinit var parentActivity: MenuActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChannelRootBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_channel_root, container, false)
        parentActivity = activity!! as MenuActivity
        fragmentChannelRootBinding.channelRootViewModel = ChannelRootViewModel(fragmentChannelRootBinding, this)
        return fragmentChannelRootBinding.root
    }

    override fun onFinish() {
        val action = fragmentChannelRootBinding.channelRootViewModel!!.action
        val fragment: Fragment = when (action) {
            "create" -> CreateChannelFragment()
            else -> JoinChannelFragment()
        }
        parentActivity.supportFragmentManager.beginTransaction().add(R.id.menuFrame, fragment, "FRAGMENT-$action").addToBackStack("FRAGMENT-$action").commit()
        listener.onFragmentClose()
    }
}