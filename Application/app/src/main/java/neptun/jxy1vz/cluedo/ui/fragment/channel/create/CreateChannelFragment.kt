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
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.cluedo.domain.util.debugPrint
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuActivity
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.MultiplayerCharacterSelectorFragment
import retrofit2.HttpException

class CreateChannelFragment : Fragment(), ViewModelListener, MenuListener {

    companion object {
        const val TAG = "FRAGMENT-create"
    }

    private lateinit var fragmentCreateChannelBinding : FragmentCreateChannelBinding
    private lateinit var parentActivity: MenuActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCreateChannelBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_channel, container, false)
        parentActivity = activity!! as MenuActivity

        fragmentCreateChannelBinding.createChannelViewModel = CreateChannelViewModel(fragmentCreateChannelBinding, context!!, lifecycleScope, parentActivity.supportFragmentManager,  this)
        return fragmentCreateChannelBinding.root
    }

    override fun onFinish() {
        lifecycleScope.launch(Dispatchers.Main) {
            parentActivity.supportFragmentManager.beginTransaction().add(R.id.menuFrame, MultiplayerCharacterSelectorFragment.newInstance(true,
                isLate = false,
                listener = this@CreateChannelFragment
            ), MultiplayerCharacterSelectorFragment.TAG).addToBackStack(MultiplayerCharacterSelectorFragment.TAG).commit()
            fragmentCreateChannelBinding.createChannelViewModel!!.notifyFragmentKilled()
            onFragmentClose()
        }
    }

    override fun onFragmentClose() {
        parentActivity.supportFragmentManager.beginTransaction().remove(this).commit()
    }

    suspend fun onBackPressed() {
        if (vm().channelCreated()) {
            try {
                PusherInstance.getInstance().apply {
                    unsubscribe(vm().getChannel())
                    disconnect()
                }
                vm().deleteCreatedChannel()
            }
            catch (ex: HttpException) {
                if (ex.code() == 404) {
                    debugPrint("Delete channel: 404")
                }
            }
            finally {
                onFragmentClose()
            }
        }
    }

    private fun vm(): CreateChannelViewModel = fragmentCreateChannelBinding.createChannelViewModel!!
}