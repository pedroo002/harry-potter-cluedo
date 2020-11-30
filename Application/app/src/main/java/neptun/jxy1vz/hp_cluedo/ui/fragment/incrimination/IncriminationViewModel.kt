package neptun.jxy1vz.hp_cluedo.ui.fragment.incrimination

import android.content.Context
import android.widget.ImageView
import androidx.core.view.children
import androidx.databinding.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.FragmentIncriminationBinding
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView

class IncriminationViewModel(
    private val bind: FragmentIncriminationBinding,
    private val context: Context,
    roomName: String,
    private val listener: IncriminationDialogInterface
) : BaseObservable() {

    interface IncriminationDialogInterface {
        fun onIncriminationFinalization(tool: String, suspect: String)
        fun onSkip()
    }

    private lateinit var toolTokens: List<String>
    private lateinit var suspectTokens: List<String>

    private val toolList: ArrayList<ImageView> = ArrayList()
    private val suspectList: ArrayList<ImageView> = ArrayList()

    private var room = ""
    private var tool = ""
    private var suspect = ""

    private var finished = false

    fun finalize() {
        if (!finished) {
            listener.onIncriminationFinalization(tool, suspect)
            finished = true
        }
    }

    fun skip() {
        listener.onSkip()
    }

    init {
        room = roomName

        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).assetDao().apply {
                toolTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_TOOL_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                suspectTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_SUSPECT_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }

                withContext(Dispatchers.Main) {
                    bind.layoutToolImages.children.asSequence().forEach { child ->
                        val idx = bind.layoutToolImages.children.indexOf(child)
                        loadUrlImageIntoImageView(toolTokens[idx * 2 + 1], context, (child as ImageView))
                        toolList.add(child)
                    }
                    bind.layoutSuspectImages.children.asSequence().forEach { child ->
                        val idx = bind.layoutSuspectImages.children.indexOf(child)
                        loadUrlImageIntoImageView(suspectTokens[idx * 2 + 1], context, (child as ImageView))
                        suspectList.add(child)
                    }
                }
            }
        }
    }

    fun selectTool(idx: Int) {
        for (i in toolList.indices) {
            if (i == idx) {
                loadUrlImageIntoImageView(toolTokens[i * 2], context, toolList[i])
                tool = context.resources.getStringArray(R.array.tools)[i]
            }
            else
                loadUrlImageIntoImageView(toolTokens[i * 2 + 1], context, toolList[i])

        }

        notifyChange()
        if (suspect.isNotEmpty())
            bind.btnValidate.isEnabled = true
    }

    fun selectSuspect(idx: Int) {
        for (i in suspectList.indices) {
            if (i == idx) {
                loadUrlImageIntoImageView(suspectTokens[i * 2], context, suspectList[i])
                suspect = context.resources.getStringArray(R.array.suspects)[i]
            }
            else
                loadUrlImageIntoImageView(suspectTokens[i * 2 + 1], context, suspectList[i])

        }

        notifyChange()
        if (tool.isNotEmpty())
            bind.btnValidate.isEnabled = true
    }

    fun getRoom(): String {
        return "Gyanúsítás itt: $room"
    }

    fun getTool(): String {
        return "Eszköz:\n$tool"
    }

    fun getSuspect(): String {
        return "Gyanúsított:\n$suspect"
    }
}