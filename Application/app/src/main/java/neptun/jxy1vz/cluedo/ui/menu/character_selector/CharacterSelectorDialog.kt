package neptun.jxy1vz.cluedo.ui.menu.character_selector

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogCharacterSelectorBinding

class CharacterSelectorDialog : DialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var dialogCharacterSelectorBinding: DialogCharacterSelectorBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogCharacterSelectorBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_character_selector,
            null,
            false
        )

        dialogCharacterSelectorBinding.spinnerCharacter.adapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.characters)
        )
        dialogCharacterSelectorBinding.spinnerCharacter.onItemSelectedListener = this

        val scale = resources.displayMetrics.density
        dialogCharacterSelectorBinding.ivCharacterCard.cameraDistance = 8000 * scale

        dialogCharacterSelectorBinding.dialogViewModel = CharacterSelectorViewModel(context!!)
        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Dialog)
            .setView(dialogCharacterSelectorBinding.root).setTitle(resources.getString(R.string.dialog_character_title))
            .create()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        dialogCharacterSelectorBinding.ivCharacterCard.setImageResource(R.drawable.szereplo_hatlap)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        dialogCharacterSelectorBinding.ivCharacterCard.setImageResource(R.drawable.szereplo_hatlap)
        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(dialogCharacterSelectorBinding.ivCharacterCard)
            start()
            doOnEnd {
                dialogCharacterSelectorBinding.dialogViewModel.setPlayer(position)

                @DrawableRes val img = when (position) {
                    0 -> R.drawable.szereplo_ginny
                    1 -> R.drawable.szereplo_harry
                    2 -> R.drawable.szereplo_hermione
                    3 -> R.drawable.szereplo_ron
                    4 -> R.drawable.szereplo_luna
                    else -> R.drawable.szereplo_neville
                }
                dialogCharacterSelectorBinding.ivCharacterCard.setImageResource(img)
            }
        }
    }
}