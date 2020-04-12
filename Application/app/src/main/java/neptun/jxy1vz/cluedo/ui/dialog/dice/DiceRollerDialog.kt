package neptun.jxy1vz.cluedo.ui.dialog.dice

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogDiceRollerBinding
import neptun.jxy1vz.cluedo.ui.map.MapViewModel

class DiceRollerDialog(private val listener: DiceResultInterface, private val playerId: Int) :
    DialogFragment() {

    interface DiceResultInterface {
        fun onDiceRoll(playerId: Int, sum: Int, house: MapViewModel.HogwartsHouse?)
        fun getCard(playerId: Int, type: DiceRollerViewModel.CardType?)
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
            DiceRollerViewModel(dialogDiceRollerBinding, context!!)
        dialogDiceRollerBinding.executePendingBindings()

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_DialogWhenLarge)
            .setView(dialogDiceRollerBinding.root).setTitle(resources.getString(R.string.dobj))
            .setNeutralButton(
                resources.getString(R.string.ok)
            ) { dialog, _ ->
                listener.getCard(playerId, dialogDiceRollerBinding.dialogViewModel!!.getCardType())
                listener.onDiceRoll(playerId, dialogDiceRollerBinding.dialogViewModel!!.getSum(), dialogDiceRollerBinding.dialogViewModel!!.getHouse())
                dialog.dismiss()
            }.create()
    }
}