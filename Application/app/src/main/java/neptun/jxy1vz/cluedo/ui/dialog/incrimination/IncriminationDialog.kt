package neptun.jxy1vz.cluedo.ui.dialog.incrimination

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogIncriminationBinding
import neptun.jxy1vz.cluedo.model.Suspect
import neptun.jxy1vz.cluedo.model.helper.roomList

class IncriminationDialog(
    private val playerId: Int,
    private val roomId: Int,
    private val listener: MapInterface
) : DialogFragment(),
    IncriminationViewModel.IncriminationDialogInterface {

    interface MapInterface {
        fun getIncrimination(suspect: Suspect)
        fun onIncriminationSkip()
    }

    private var tool: String = ""
    private var suspect: String = ""

    private lateinit var dialogIncriminationBinding: DialogIncriminationBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogIncriminationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_incrimination,
            null,
            false
        )

        dialogIncriminationBinding.dialogViewModel = IncriminationViewModel(
            dialogIncriminationBinding,
            context!!,
            this
        )

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setView(dialogIncriminationBinding.root)
            .setTitle("${context!!.resources.getString(R.string.incrimination)} itt: ${roomList[roomId].name}")
            .create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (tool.isNotEmpty() && suspect.isNotEmpty())
            listener.getIncrimination(Suspect(playerId, roomList[roomId].name, tool, suspect))
        else
            listener.onIncriminationSkip()
        super.onDismiss(dialog)
    }

    override fun onIncriminationFinalization(tool: String, suspect: String) {
        this.tool = tool
        this.suspect = suspect
        dialog!!.dismiss()
    }

    override fun onSkip() {
        dialog!!.dismiss()
    }
}