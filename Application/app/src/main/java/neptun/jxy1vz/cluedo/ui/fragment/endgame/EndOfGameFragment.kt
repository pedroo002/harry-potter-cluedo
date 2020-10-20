package neptun.jxy1vz.cluedo.ui.fragment.endgame

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
import neptun.jxy1vz.cluedo.databinding.FragmentEndOfGameBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class EndOfGameFragment(private val suspect: Suspect, private val listener: DialogDismiss) : Fragment(), ViewModelListener {

    private lateinit var fragmentEndOfGameBinding: FragmentEndOfGameBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentEndOfGameBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_end_of_game, container, false)
        fragmentEndOfGameBinding.endOfGameViewModel = EndOfGameViewModel(fragmentEndOfGameBinding, context!!, suspect, this)
        return fragmentEndOfGameBinding.root
    }

    override fun onFinish() {
        val gameMode = context!!.getSharedPreferences(resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE).getString(resources.getString(R.string.play_mode_key), "")
        if (gameMode == resources.getStringArray(R.array.playmodes)[1]) {
            val playerData = context!!.getSharedPreferences(resources.getString(R.string.player_data_pref), Context.MODE_PRIVATE)
            val playerName = playerData.getString(resources.getString(R.string.player_name_key), "")
            val channelId = playerData.getString(resources.getString(R.string.channel_id_key), "")

            val retrofit = RetrofitInstance.getInstance(context!!)
            lifecycleScope.launch(Dispatchers.IO) {
                retrofit.cluedo.leaveChannel(channelId!!, playerName!!)
                val channelName = retrofit.cluedo.getChannel(channelId)
                val pusher = PusherInstance.getInstance()
                pusher.unsubscribe("presence-$channelName")
                pusher.disconnect()
            }
        }

        listener.onEndOfGameDismiss()
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}