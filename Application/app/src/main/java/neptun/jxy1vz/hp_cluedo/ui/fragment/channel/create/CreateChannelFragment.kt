package neptun.jxy1vz.hp_cluedo.ui.fragment.channel.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.hp_cluedo.data.network.pusher.PusherInstance
import neptun.jxy1vz.hp_cluedo.ui.activity.menu.MenuActivity
import neptun.jxy1vz.hp_cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.hp_cluedo.ui.fragment.character_selector.multi.MultiplayerCharacterSelectorFragment
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException

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
            catch (ex: SocketException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(fragmentCreateChannelBinding.root, ex.message ?: "Hiba lépett fel a hálózatban.", Snackbar.LENGTH_LONG).show()
                }
            }
            catch (ex: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(fragmentCreateChannelBinding.root, "A kapcsolat túllépte az időkorlátot!", Snackbar.LENGTH_LONG).show()
                }
            }
            finally {
                onFragmentClose()
            }
        }
    }

    private fun vm(): CreateChannelViewModel = fragmentCreateChannelBinding.createChannelViewModel!!
}