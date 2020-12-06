package neptun.jxy1vz.hp_cluedo.ui.fragment.dice_roller

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.BaseObservable
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.FragmentDiceRollerBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.StateMachineHandler
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.data.network.model.message.DiceDataMessage
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException
import kotlin.random.Random

class DiceRollerViewModel(
    private val bind: FragmentDiceRollerBinding,
    private val context: Context,
    private val felixFelicis: Boolean,
    private val listener: DiceFragmentListener
) : BaseObservable(),
    Animation.AnimationListener {

    private lateinit var diceList: List<String>

    interface DiceFragmentListener {
        fun onFinish()
    }

    enum class CardType {
        HELPER,
        DARK
    }

    init {
        GlobalScope.launch(Dispatchers.IO) {
            diceList = CluedoDatabase.getInstance(context).assetDao().getAssetsByPrefix(AssetPrefixes.DICE.string())!!.map { assetDBmodel -> assetDBmodel.url }
            withContext(Dispatchers.Main) {
                bind.btnSubmit.isEnabled = false
                bind.btnRoll.isEnabled = true
            }
        }
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

    private lateinit var diceValue1: String
    private lateinit var diceValue2: String
    private lateinit var diceValue3: String

    private var num1 = 0
    private var num2 = 0
    private var num3 = 0

    private fun getNumericDiceResource(number: Int): String {
        return when (number) {
            1 -> diceList[0]
            2 -> diceList[1]
            3 -> diceList[2]
            4 -> diceList[3]
            5 -> diceList[4]
            else -> diceList[5]
        }
    }

    private fun getGraphicalDiceResource(number: Int): String {
        return when (number) {
            1 -> diceList[6]
            2 -> diceList[7]
            3 -> diceList[8]
            4 -> diceList[9]
            5 -> diceList[10]
            else -> diceList[11]
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

        loadUrlImageIntoImageView(diceValue1, context, diceImageList[0])
        loadUrlImageIntoImageView(diceValue2, context, diceImageList[1])
        loadUrlImageIntoImageView(diceValue3, context, diceImageList[2])

        setDice3(num3)

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
            1 -> cardType = CardType.DARK
            2 -> cardType = CardType.HELPER
            3 -> house = StateMachineHandler.HogwartsHouse.GRYFFINDOR
            4 -> house = StateMachineHandler.HogwartsHouse.HUFFLEPUFF
            5 -> house = StateMachineHandler.HogwartsHouse.RAVENCLAW
            6 -> house = StateMachineHandler.HogwartsHouse.SLYTHERIN
        }
    }

    private fun disableButton() {
        bind.btnRoll.isEnabled = false
    }

    private fun sendDiceEvent() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val retrofit = RetrofitInstance.getInstance(context)
                val diceData = DiceDataMessage(MapViewModel.mPlayerId!!, num1, num2, num3)
                val moshiJson = retrofit.moshi.adapter(DiceDataMessage::class.java).toJson(diceData)
                val body = moshiJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                retrofit.cluedo.sendDiceEvent(MapViewModel.channelName, body)

                withContext(Dispatchers.Main) {
                    bind.btnSubmit.isEnabled = true
                }
            }
            catch (ex: HttpException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(bind.root, ex.message ?: "Hiba lépett fel a hálózatban.", Snackbar.LENGTH_LONG).show()
                }
            }
            catch (ex: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(bind.root, "A kapcsolat túllépte az időkorlátot!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    fun finish() {
        listener.onFinish()
    }
}