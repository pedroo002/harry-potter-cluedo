package neptun.jxy1vz.cluedo.ui.fragment.channel.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.MultiplayerCharacterSelectorFragment

class CreateChannelFragment : Fragment(), ViewModelListener, MenuListener {

    companion object {
        const val TAG = "FRAGMENT-create"
    }

    private lateinit var fragmentCreateChannelBinding : FragmentCreateChannelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCreateChannelBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_channel, container, false)

        fragmentCreateChannelBinding.createChannelViewModel = CreateChannelViewModel(fragmentCreateChannelBinding, context!!, lifecycleScope,  this)
        return fragmentCreateChannelBinding.root
    }

    override fun onFinish() {
        lifecycleScope.launch(Dispatchers.Main) {
            activity!!.supportFragmentManager.beginTransaction().add(R.id.menuFrame, MultiplayerCharacterSelectorFragment(this@CreateChannelFragment)).addToBackStack("CharacterSelectorMulti").commit()
        }
    }

    override fun onFragmentClose() {
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }

    suspend fun onBackPressed() {
        fragmentCreateChannelBinding.createChannelViewModel!!.getChannel()?.let {
            fragmentCreateChannelBinding.createChannelViewModel!!.deleteCreatedChannel()
        }
        PusherInstance.getInstance().disconnect()
    }
}