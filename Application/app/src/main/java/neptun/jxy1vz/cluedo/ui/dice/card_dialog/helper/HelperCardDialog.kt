package neptun.jxy1vz.cluedo.ui.dice.card_dialog.helper

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
import neptun.jxy1vz.cluedo.databinding.DialogHelperCardBinding

class HelperCardDialog(private val cardResource: Int) : DialogFragment() {

    private lateinit var dialogHelperCardBinding: DialogHelperCardBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogHelperCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_helper_card,
            null,
            false
        )
        dialogHelperCardBinding.helperCardDialogViewModel = HelperCardViewModel()

        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(dialogHelperCardBinding.ivHelperCard)
            start()
            doOnEnd {
                dialogHelperCardBinding.ivHelperCard.setImageResource(cardResource)
            }
        }

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_DialogWhenLarge)
            .setView(dialogHelperCardBinding.root)
            .setTitle(resources.getString(R.string.got_helper_card)).setNeutralButton(
            resources.getString(R.string.ok)
        ) { dialog, _ ->
            dialog.dismiss()
        }.create()
    }
}