package neptun.jxy1vz.cluedo.ui.fragment.channel.root

import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentChannelRootBinding
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class ChannelRootViewModel(private val bind: FragmentChannelRootBinding, private val listener: ViewModelListener) : BaseObservable() {

    private var action = ""

    fun createServer() {
        action = "create"
        bind.btnNext.isEnabled = true
        bind.ivCreate.setImageResource(R.drawable.host_server_selected)
        bind.ivJoin.setImageResource(R.drawable.join_server)
    }

    fun joinServer() {
        action = "join"
        bind.btnNext.isEnabled = true
        bind.ivCreate.setImageResource(R.drawable.host_server)
        bind.ivJoin.setImageResource(R.drawable.join_server_selected)
    }

    fun next() {
        listener.onFinish()
    }

    fun cancel() {
        listener.onFinish()
    }
}