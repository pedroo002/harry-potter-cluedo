package neptun.jxy1vz.cluedo.ui.dice

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import kotlin.random.Random

class DiceRollerViewModel(private val listener: DiceViewModelInterface, private val context: Context, private val imageList: List<ImageView>) : BaseObservable(),
    Animation.AnimationListener {

    interface DiceViewModelInterface {
        fun onDiceResult(dice1: Int, dice2: Int, dice3: Int)
        fun disableButton()
    }

    @DrawableRes var diceValue1: Int = R.drawable.dice1
    @DrawableRes var diceValue2: Int = R.drawable.dice4
    @DrawableRes var diceValue3: Int = R.drawable.helper_card

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
            else -> R.drawable.dark_sign_black_gold
        }
    }

    fun rollDice() {
        val anim = AnimationUtils.loadAnimation(context, R.anim.shake)
        anim.setAnimationListener(this)
        for (dice in imageList) {
            dice.startAnimation(anim)
        }
        listener.disableButton()
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {
        diceValue1 = getNumericDiceResource(num1)
        diceValue2 = getNumericDiceResource(num2)
        diceValue3 = getGraphicalDiceResource(num3)

        imageList[0].setImageResource(diceValue1)
        imageList[1].setImageResource(diceValue2)
        imageList[2].setImageResource(diceValue3)

        listener.onDiceResult(num1, num2, num3)
    }

    override fun onAnimationStart(animation: Animation?) {
        num1 = Random.nextInt(1, 7)
        num2 = Random.nextInt(1, 7)
        num3 = Random.nextInt(1, 7)
    }
}