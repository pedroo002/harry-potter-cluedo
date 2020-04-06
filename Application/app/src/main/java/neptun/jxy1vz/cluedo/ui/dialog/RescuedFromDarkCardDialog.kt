package neptun.jxy1vz.cluedo.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R

class RescuedFromDarkCardDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setTitle(R.string.rescued)
            .setMessage(R.string.your_card_helped_you)
            .setNeutralButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }
}