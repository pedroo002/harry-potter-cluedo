package neptun.jxy1vz.hp_cluedo.ui.fragment.incrimination

import android.content.Context
import android.widget.ImageView
import androidx.core.view.children
import androidx.databinding.BaseObservable
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentIncriminationBinding
import neptun.jxy1vz.hp_cluedo.domain.model.helper.suspectTokens
import neptun.jxy1vz.hp_cluedo.domain.model.helper.suspectTokensBW
import neptun.jxy1vz.hp_cluedo.domain.model.helper.toolTokens
import neptun.jxy1vz.hp_cluedo.domain.model.helper.toolTokensBW

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

        bind.layoutToolImages.children.asSequence().forEach { child ->
            toolList.add(child as ImageView)
        }
        bind.layoutSuspectImages.children.asSequence().forEach { child ->
            suspectList.add(child as ImageView)
        }
    }

    fun selectTool(idx: Int) {
        for (i in toolList.indices) {
            if (i == idx) {
                toolList[i].setImageResource(toolTokens[i])
                tool = context.resources.getStringArray(R.array.tools)[i]
            }
            else
                toolList[i].setImageResource(toolTokensBW[i])
        }

        notifyChange()
        if (suspect.isNotEmpty())
            bind.btnValidate.isEnabled = true
    }

    fun selectSuspect(idx: Int) {
        for (i in suspectList.indices) {
            if (i == idx) {
                suspectList[i].setImageResource(suspectTokens[i])
                suspect = context.resources.getStringArray(R.array.suspects)[i]
            }
            else
                suspectList[i].setImageResource(suspectTokensBW[i])
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