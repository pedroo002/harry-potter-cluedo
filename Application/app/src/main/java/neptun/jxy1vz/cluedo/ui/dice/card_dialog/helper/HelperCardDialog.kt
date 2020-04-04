package neptun.jxy1vz.cluedo.ui.dice.card_dialog.helper

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogHelperCardBinding

class HelperCardDialog(private val cardResource: Int) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val dialogHelperCardBinding = DataBindingUtil.inflate<DialogHelperCardBinding>(LayoutInflater.from(context), R.layout.dialog_helper_card, null, false)
        dialogHelperCardBinding.dialogViewModel = HelperCardViewModel()
        dialogHelperCardBinding.ivCard.setImageResource(cardResource)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_DialogWhenLarge).setTitle(resources.getString(R.string.got_helper_card)).setNeutralButton(resources.getString(R.string.ok)
        ) { dialog, _ ->
            dialog.dismiss()
        }.create()
    }
}