package neptun.jxy1vz.cluedo.ui.dice.card_dialog.dark_mark

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogDarkCardBinding

class DarkCardDialog(private val cardResource: Int) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogDarkCardBinding = DataBindingUtil.inflate<DialogDarkCardBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_dark_card,
            null,
            false
        )
        dialogDarkCardBinding.darkCardDialogViewModel = DarkCardViewModel()

        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(dialogDarkCardBinding.ivDarkCard)
            start()
            doOnEnd {
                dialogDarkCardBinding.ivDarkCard.setImageResource(cardResource)
            }
        }

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_DialogWhenLarge)
            .setView(dialogDarkCardBinding.root)
            .setTitle(resources.getString(R.string.sotet_jegy)).setNeutralButton(
            resources.getString(R.string.ok)
        ) { dialog, _ ->
            dialog.dismiss()
        }.create()
    }
}