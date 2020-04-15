package neptun.jxy1vz.cluedo.ui.dialog.incrimination

import android.content.Context
import android.widget.ImageView
import androidx.core.view.children
import androidx.databinding.BaseObservable
import com.google.android.material.snackbar.Snackbar
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogIncriminationBinding
import neptun.jxy1vz.cluedo.model.helper.suspectTokens
import neptun.jxy1vz.cluedo.model.helper.suspectTokensBW
import neptun.jxy1vz.cluedo.model.helper.toolTokens
import neptun.jxy1vz.cluedo.model.helper.toolTokensBW

class IncriminationViewModel(
    private val bind: DialogIncriminationBinding,
    private val context: Context,
    private val listener: IncriminationDialogInterface
) : BaseObservable() {

    interface IncriminationDialogInterface {
        fun onIncriminationFinalization(tool: String, suspect: String)
        fun onSkip()
    }

    private val toolList: MutableList<ImageView> = ArrayList()
    private val suspectList: MutableList<ImageView> = ArrayList()

    private var tool = ""
    private var suspect = ""

    fun finalize() {
        if (tool.isEmpty() || suspect.isEmpty())
            Snackbar.make(bind.root, "Válassz mindkét sorból egyet!", Snackbar.LENGTH_LONG).show()
        else
            listener.onIncriminationFinalization(tool, suspect)
    }

    fun skip() {
        listener.onSkip()
    }

    init {
        for (child in bind.layoutToolImages.children.asSequence()) {
            toolList.add(child as ImageView)
        }
        for (child in bind.layoutSuspectImages.children.asSequence()) {
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
    }
}