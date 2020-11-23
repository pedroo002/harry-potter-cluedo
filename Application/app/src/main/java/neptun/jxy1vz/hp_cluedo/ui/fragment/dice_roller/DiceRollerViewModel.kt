package neptun.jxy1vz.hp_cluedo.ui.fragment.dice_roller

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.databinding.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentDiceRollerBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.StateMachineHandler
import neptun.jxy1vz.hp_cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.network.model.message.dice.DiceDataMessage
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.random.Random

class DiceRollerViewModel(
    private val bind: FragmentDiceRollerBinding,
    private val context: Context,
    private val felixFelicis: Boolean,
    private val listener: DiceFragmentListener
) : BaseObservable(),
    Animation.AnimationListener {

    interface DiceFragmentListener {
        fun onFinish()
    }

    enum class CardType {
        HELPER,
        DARK
    }

    init {
        bind.btnSubmit.isEnabled = false
    }

    private var cardType: CardType? = null
    private var house: StateMachineHandler.HogwartsHouse? = null

    fun getCardType(): CardType? {
        return cardType
    }

    fun getHouse(): StateMachineHandler.HogwartsHouse? {
        return house
    }

    fun getSum(): Int {
        return num1 + num2
    }

    private val diceImageList = listOf(
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
        diceImageList.forEach { dice ->
            dice.startAnimation(anim)
        }
        disableButton()
    }

    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        diceValue1 = getNumericDiceResource(num1)
        diceValue2 = getNumericDiceResource(num2)
        diceValue3 = getGraphicalDiceResource(num3)

        diceImageList[0].setImageResource(diceValue1)
        diceImageList[1].setImageResource(diceValue2)
        diceImageList[2].setImageResource(diceValue3)

        setDice3(num3)

        bind.btnSubmit.isEnabled = true

        if (MapViewModel.isGameModeMulti()) {
            sendDiceEvent()
        }
    }

    override fun onAnimationStart(animation: Animation?) {
        num1 = Random.nextInt(1, 7)
        num2 = Random.nextInt(1, 7)
        num3 = Random.nextInt(1, 7)

        if (felixFelicis) {
            num1 = 6
            num2 = 6
        }
    }

    private fun setDice3(dice3: Int) {
        when (dice3) {
            1 -> cardType = CardType.HELPER
            2 -> house = StateMachineHandler.HogwartsHouse.GRYFFINDOR
            3 -> house = StateMachineHandler.HogwartsHouse.SLYTHERIN
            4 -> house = StateMachineHandler.HogwartsHouse.HUFFLEPUFF
            5 -> house = StateMachineHandler.HogwartsHouse.RAVENCLAW
            6 -> cardType = CardType.DARK
        }
    }

    private fun disableButton() {
        bind.btnRoll.isEnabled = false
    }

    private fun sendDiceEvent() {
        GlobalScope.launch(Dispatchers.IO) {
            val retrofit = RetrofitInstance.getInstance(context)
            val diceData = DiceDataMessage(MapViewModel.mPlayerId!!, num1, num2, num3)
            val moshiJson = retrofit.moshi.adapter(DiceDataMessage::class.java).toJson(diceData)
            val body = moshiJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            retrofit.cluedo.sendDiceEvent(MapViewModel.channelName, body)
        }
    }

    fun finish() {
        listener.onFinish()
    }
}