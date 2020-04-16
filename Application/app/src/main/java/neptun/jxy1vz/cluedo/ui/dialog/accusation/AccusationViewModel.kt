package neptun.jxy1vz.cluedo.ui.dialog.accusation

import android.content.Context
import android.widget.ImageView
import androidx.core.view.children
import androidx.databinding.BaseObservable
import com.google.android.material.snackbar.Snackbar
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogAccusationBinding
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.domain.model.helper.*

class AccusationViewModel(private val playerId: Int, private val bind: DialogAccusationBinding, private val context: Context, private val listener: FinalizationListener) : BaseObservable() {

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
        for (child in bind.layoutRoomImages.children.asSequence()) {
            roomList.add(child as ImageView)
        }
        for (child in bind.layoutToolImages.children.asSequence()) {
            toolList.add(child as ImageView)
        }
        for (child in bind.layoutSuspectImages.children.asSequence()) {
            suspectList.add(child as ImageView)
        }
    }

    fun finalize() {
        if (selectedRoom.isEmpty() || selectedTool.isEmpty() || selectedSuspect.isEmpty()) {
            Snackbar.make(bind.root, "Válassz minden sorból egyet!", Snackbar.LENGTH_LONG).show()
        }
        else
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
    }
}