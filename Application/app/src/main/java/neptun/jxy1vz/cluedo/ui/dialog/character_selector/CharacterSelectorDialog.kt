package neptun.jxy1vz.cluedo.ui.dialog.character_selector

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogCharacterSelectorBinding

class CharacterSelectorDialog : DialogFragment(),
    CharacterSelectorViewModel.CharacterSelectorInterface {

    private lateinit var dialogCharacterSelectorBinding: DialogCharacterSelectorBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogCharacterSelectorBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_character_selector,
            null,
            false
        )

        dialogCharacterSelectorBinding.dialogViewModel = CharacterSelectorViewModel(dialogCharacterSelectorBinding, context!!, this)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setView(dialogCharacterSelectorBinding.root)
            .setTitle(resources.getString(R.string.dialog_character_title))
            .create()
    }

    override fun onGameStart() {
        dialog!!.dismiss()
    }
}