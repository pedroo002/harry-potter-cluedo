package neptun.jxy1vz.hp_cluedo.ui.fragment.accusation

import android.content.Context
import android.widget.ImageView
import androidx.core.view.children
import androidx.databinding.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.FragmentAccusationBinding
import neptun.jxy1vz.hp_cluedo.domain.model.Suspect
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView

class AccusationViewModel(private val playerId: Int, private val bind: FragmentAccusationBinding, private val context: Context, private val listener: FinalizationListener) : BaseObservable() {

    interface FinalizationListener {
        fun onFinalized(suspect: Suspect)
    }

    private lateinit var roomTokens: List<String>
    private lateinit var toolTokens: List<String>
    private lateinit var suspectTokens: List<String>

    private val roomList: MutableList<ImageView> = ArrayList()
    private val toolList: MutableList<ImageView> = ArrayList()
    private val suspectList: MutableList<ImageView> = ArrayList()

    private var selectedRoom: String = ""
    private var selectedTool: String = ""
    private var selectedSuspect: String = ""

    init {
        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).assetDao().apply {
                roomTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_ROOM_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                toolTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_TOOL_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                suspectTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_SUSPECT_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }

                withContext(Dispatchers.Main) {
                    bind.layoutRoomImages.children.asSequence().forEach { child ->
                        val idx = bind.layoutRoomImages.children.indexOf(child)
                        loadUrlImageIntoImageView(roomTokens[idx * 2 + 1], context, child as ImageView)
                        roomList.add(child)
                    }
                    bind.layoutToolImages.children.asSequence().forEach { child ->
                        val idx = bind.layoutToolImages.children.indexOf(child)
                        loadUrlImageIntoImageView(toolTokens[idx * 2 + 1], context, child as ImageView)
                        toolList.add(child)
                    }
                    bind.layoutSuspectImages.children.asSequence().forEach { child ->
                        val idx = bind.layoutSuspectImages.children.indexOf(child)
                        loadUrlImageIntoImageView(suspectTokens[idx * 2 + 1], context, child as ImageView)
                        suspectList.add(child)
                    }
                }
            }
        }
    }

    fun finalize() {
        listener.onFinalized(Suspect(playerId, selectedRoom, selectedTool, selectedSuspect))
    }

    fun selectRoom(idx: Int) {
        for (i in roomList.indices) {
            if (i == idx) {
                loadUrlImageIntoImageView(roomTokens[i * 2], context, roomList[i])
                selectedRoom = context.resources.getStringArray(R.array.rooms)[i]
            }
            else
                loadUrlImageIntoImageView(roomTokens[i * 2 + 1], context, roomList[i])

        }

        notifyChange()
        if (selectedTool.isNotEmpty() && selectedSuspect.isNotEmpty())
            bind.btnValidate.isEnabled = true
    }

    fun selectTool(idx: Int) {
        for (i in toolList.indices) {
            if (i == idx) {
                loadUrlImageIntoImageView(toolTokens[i * 2], context, toolList[i])
                selectedTool = context.resources.getStringArray(R.array.tools)[i]
            }
            else
                loadUrlImageIntoImageView(toolTokens[i * 2 + 1], context, toolList[i])

        }

        notifyChange()
        if (selectedRoom.isNotEmpty() && selectedSuspect.isNotEmpty())
            bind.btnValidate.isEnabled = true
    }

    fun selectSuspect(idx: Int) {
        for (i in suspectList.indices) {
            if (i == idx) {
                loadUrlImageIntoImageView(suspectTokens[i * 2], context, suspectList[i])
                selectedSuspect = context.resources.getStringArray(R.array.suspects)[i]
            }
            else
                loadUrlImageIntoImageView(suspectTokens[i * 2 + 1], context, suspectList[i])

        }

        notifyChange()
        if (selectedTool.isNotEmpty() && selectedRoom.isNotEmpty())
            bind.btnValidate.isEnabled = true
    }

    fun getRoom(): String {
        return "Helyiség:\n$selectedRoom"
    }

    fun getTool(): String {
        return "Eszköz:\n$selectedTool"
    }

    fun getSuspect(): String {
        return "Gyanúsított:\n$selectedSuspect"
    }
}