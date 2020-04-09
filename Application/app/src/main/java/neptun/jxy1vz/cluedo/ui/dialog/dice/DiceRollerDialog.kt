package neptun.jxy1vz.cluedo.ui.dialog.dice

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogDiceRollerBinding

class DiceRollerDialog(private val listener: DiceResultInterface, private val playerId: Int) :
    DialogFragment() {

    interface DiceResultInterface {
        fun onDiceRoll(playerId: Int, sum: Int, other: Int)
        fun showCard(playerId: Int, type: DiceRollerViewModel.CardType?)
    }

    private lateinit var dialogDiceRollerBinding: DialogDiceRollerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogDiceRollerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_dice_roller,
            null,
            false
        )

        dialogDiceRollerBinding.dialogViewModel =
            DiceRollerViewModel(dialogDiceRollerBinding, context!!, listener, playerId)
        dialogDiceRollerBinding.executePendingBindings()

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_DialogWhenLarge)
            .setView(dialogDiceRollerBinding.root).setTitle(resources.getString(R.string.dobj))
            .setNeutralButton(
                resources.getString(R.string.ok)
            ) { dialog, _ ->
                listener.showCard(playerId, dialogDiceRollerBinding.dialogViewModel!!.getCardType())
                dialog.dismiss()
            }.create()
    }
}