package neptun.jxy1vz.cluedo.ui.dialog.dice

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogDiceRollerBinding
import kotlin.random.Random

class DiceRollerViewModel(
    private val bind: DialogDiceRollerBinding,
    private val context: Context,
    private val diceResultListener: DiceRollerDialog.DiceResultInterface,
    private val playerId: Int
) : BaseObservable(),
    Animation.AnimationListener {

    enum class CardType {
        HELPER,
        DARK
    }

    private var cardType: CardType? = null

    fun getCardType(): CardType? {
        return cardType
    }

    private val diceImageList = listOf<ImageView>(
        bind.ivDice1,
        bind.ivDice2,
        bind.ivDice3
    )

    @DrawableRes
    var diceValue1: Int = R.drawable.dice1
    @DrawableRes
    var diceValue2: Int = R.drawable.dice4
    @DrawableRes
    var diceValue3: Int = R.drawable.helper_card

    private var num1 = 0
    private var num2 = 0
    private var num3 = 0

    private fun getNumericDiceResource(number: Int): Int {
        return when (number) {
            1 -> R.drawable.dice1
            2 -> R.drawable.dice2
            3 -> R.drawable.dice3
            4 -> R.drawable.dice4
            5 -> R.drawable.dice5
            else -> R.drawable.dice6
        }
    }

    private fun getGraphicalDiceResource(number: Int): Int {
        return when (number) {
            1 -> R.drawable.helper_card
            2 -> R.drawable.gryffindor
            3 -> R.drawable.slytherin
            4 -> R.drawable.hufflepuff
            5 -> R.drawable.ravenclaw
            else -> R.drawable.dark_mark
        }
    }

    fun rollDice() {
        val anim = AnimationUtils.loadAnimation(context, R.anim.shake)
        anim.setAnimationListener(this)
        for (dice in diceImageList) {
            dice.startAnimation(anim)
        }
        disableButton()
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {
        diceValue1 = getNumericDiceResource(num1)
        diceValue2 = getNumericDiceResource(num2)
        diceValue3 = getGraphicalDiceResource(num3)

        diceImageList[0].setImageResource(diceValue1)
        diceImageList[1].setImageResource(diceValue2)
        diceImageList[2].setImageResource(diceValue3)

        onDiceResult(num1, num2, num3)
    }

    override fun onAnimationStart(animation: Animation?) {
        num1 = Random.nextInt(1, 7)
        num2 = Random.nextInt(1, 7)
        num3 = Random.nextInt(1, 7)
    }

    private fun onDiceResult(dice1: Int, dice2: Int, dice3: Int) {
        diceResultListener.onDiceRoll(playerId, dice1 + dice2, dice3)
        when (dice3) {
            1 -> cardType = CardType.HELPER
            6 -> cardType = CardType.DARK
        }
    }

    private fun disableButton() {
        bind.btnRoll.isEnabled = false
    }
}