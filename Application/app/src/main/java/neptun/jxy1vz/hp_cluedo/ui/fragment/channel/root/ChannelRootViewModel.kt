package neptun.jxy1vz.hp_cluedo.ui.fragment.channel.root

import android.content.Context
import androidx.databinding.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.FragmentChannelRootBinding
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class ChannelRootViewModel(private val bind: FragmentChannelRootBinding, private val context: Context, private val listener: ViewModelListener) : BaseObservable() {

    var action = ""

    private lateinit var hostSelected: String
    private lateinit var host: String
    private lateinit var joinSelected: String
    private lateinit var join: String

    init {
        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).assetDao().apply {
                hostSelected = getAssetByTag("resources/menu/other/host_server_selected.png")!!.url
                host = getAssetByTag("resources/menu/other/host_server.png")!!.url
                joinSelected = getAssetByTag("resources/menu/other/join_server_selected.png")!!.url
                join = getAssetByTag("resources/menu/other/join_server.png")!!.url
            }
        }
    }

    fun createServer() {
        if (!this::join.isInitialized)
            return
        action = "create"
        bind.btnNext.isEnabled = true
        loadUrlImageIntoImageView(hostSelected, context, bind.ivCreate)
        loadUrlImageIntoImageView(join, context, bind.ivJoin)
    }

    fun joinServer() {
        if (!this::join.isInitialized)
            return
        action = "join"
        bind.btnNext.isEnabled = true
        loadUrlImageIntoImageView(host, context, bind.ivCreate)
        loadUrlImageIntoImageView(joinSelected, context, bind.ivJoin)
    }

    fun next() {
        listener.onFinish()
    }

    fun cancel() {
        action = "cancel"
        listener.onFinish()
    }
}