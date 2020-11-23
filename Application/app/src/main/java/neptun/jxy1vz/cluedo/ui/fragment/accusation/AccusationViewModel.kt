package neptun.jxy1vz.cluedo.ui.fragment.accusation

import android.content.Context
import android.widget.ImageView
import androidx.core.view.children
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentAccusationBinding
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.domain.model.helper.*

class AccusationViewModel(private val playerId: Int, private val bind: FragmentAccusationBinding, private val context: Context, private val listener: FinalizationListener) : BaseObservable() {

    interface FinalizationListener {
        fun onFinalized(suspect: Suspect)
    }

    private val roomList: MutableList<ImageView> = ArrayList()
    private val toolList: MutableList<ImageView> = ArrayList()
    private val suspectList: MutableList<ImageView> = ArrayList()

    private var selectedRoom: String = ""
    private var selectedTool: String = ""
    private var selectedSuspect: String = ""

    init {
        bind.layoutRoomImages.children.asSequence().forEach { child ->
            roomList.add(child as ImageView)
        }
        bind.layoutToolImages.children.asSequence().forEach { child ->
            toolList.add(child as ImageView)
        }
        bind.layoutSuspectImages.children.asSequence().forEach { child ->
            suspectList.add(child as ImageView)
        }
    }

    fun finalize() {
        listener.onFinalized(Suspect(playerId, selectedRoom, selectedTool, selectedSuspect))
    }

    fun selectRoom(idx: Int) {
        for (i in roomList.indices) {
            if (i == idx) {
                roomList[i].setImageResource(roomTokens[i])
                selectedRoom = context.resources.getStringArray(R.array.rooms)[i]
            }
            else
                roomList[i].setImageResource(roomTokensBW[i])
        }

        notifyChange()
        if (selectedTool.isNotEmpty() && selectedSuspect.isNotEmpty())
            bind.btnValidate.isEnabled = true
    }

    fun selectTool(idx: Int) {
        for (i in toolList.indices) {
            if (i == idx) {
                toolList[i].setImageResource(toolTokens[i])
                selectedTool = context.resources.getStringArray(R.array.tools)[i]
            }
            else
                toolList[i].setImageResource(toolTokensBW[i])
        }

        notifyChange()
        if (selectedRoom.isNotEmpty() && selectedSuspect.isNotEmpty())
            bind.btnValidate.isEnabled = true
    }

    fun selectSuspect(idx: Int) {
        for (i in suspectList.indices) {
            if (i == idx) {
                suspectList[i].setImageResource(suspectTokens[i])
                selectedSuspect = context.resources.getStringArray(R.array.suspects)[i]
            }
            else
                suspectList[i].setImageResource(suspectTokensBW[i])
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