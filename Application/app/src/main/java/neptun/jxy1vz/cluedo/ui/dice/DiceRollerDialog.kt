package neptun.jxy1vz.cluedo.ui.dice

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogDiceRollerBinding

class DiceRollerDialog(private val listener: DiceResultInterface, private val playerId: Int) :
    DialogFragment(), DiceRollerViewModel.DiceViewModelInterface {

    interface DiceResultInterface {
        fun onDiceRoll(player: Int, sum: Int, other: Int)
        fun showCard(type: CardType)
    }

    enum class CardType {
        HELPER,
        DARK
    }

    private var cardType: CardType? = null

    private lateinit var dialogDiceRollerBinding: DialogDiceRollerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogDiceRollerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_dice_roller,
            null,
            false
        )

        val diceImageList = listOf<ImageView>(
            dialogDiceRollerBinding.ivDice1,
            dialogDiceRollerBinding.ivDice2,
            dialogDiceRollerBinding.ivDice3
        )
        dialogDiceRollerBinding.dialogViewModel =
            DiceRollerViewModel(this, context!!, diceImageList)
        dialogDiceRollerBinding.executePendingBindings()

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_DialogWhenLarge)
            .setView(dialogDiceRollerBinding.root).setTitle(resources.getString(R.string.dobj))
            .setNeutralButton(
                resources.getString(R.string.ok)
            ) { dialog, _ ->
                cardType?.let {
                    listener.showCard(it)
                }
                dialog.dismiss()
            }.create()
    }

    override fun onDiceResult(dice1: Int, dice2: Int, dice3: Int) {
        listener.onDiceRoll(playerId, dice1 + dice2, dice3)
        when (dice3) {
            1 -> cardType = CardType.HELPER
            6 -> cardType = CardType.DARK
        }
    }

    override fun disableButton() {
        dialogDiceRollerBinding.btnRoll.isEnabled = false
    }
}