package neptun.jxy1vz.hp_cluedo.ui.fragment.dice_roller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.FragmentDiceRollerBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.StateMachineHandler
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel

class DiceRollerFragment : Fragment(), DiceRollerViewModel.DiceFragmentListener {

    private lateinit var listener: DiceResultInterface
    private var playerId: Int = 0
    private var felixFelicis: Boolean = false

    fun setArgs(l: DiceResultInterface, id: Int, felix: Boolean) {
        listener = l
        playerId = id
        felixFelicis = felix
    }

    companion object {
        fun newInstance(listener: DiceResultInterface, playerId: Int, felixFelicis: Boolean): DiceRollerFragment {
            val fragment = DiceRollerFragment()
            fragment.setArgs(listener, playerId, felixFelicis)
            return fragment
        }
    }

    interface DiceResultInterface {
        fun onDiceRoll(playerId: Int, sum: Int, house: StateMachineHandler.HogwartsHouse?)
        fun getCard(playerId: Int, type: DiceRollerViewModel.CardType?)
    }

    private lateinit var fragmentDiceRollerBinding: FragmentDiceRollerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDiceRollerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dice_roller, container, false)
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                val dice1 = getAssetByTag("resources/map/dice/dice01.png")!!.url
                val dice2 = getAssetByTag("resources/map/dice/dice02.png")!!.url
                val helper = getAssetByTag("resources/map/dice/dice08_helper_card.png")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(dice1, context!!, fragmentDiceRollerBinding.ivDice1)
                    loadUrlImageIntoImageView(dice2, context!!, fragmentDiceRollerBinding.ivDice2)
                    loadUrlImageIntoImageView(helper, context!!, fragmentDiceRollerBinding.ivDice3)
                    fragmentDiceRollerBinding.btnRoll.isEnabled = false
                    fragmentDiceRollerBinding.diceRollerViewModel = DiceRollerViewModel(fragmentDiceRollerBinding, context!!, felixFelicis, this@DiceRollerFragment)
                }
            }
        }
        return fragmentDiceRollerBinding.root
    }

    override fun onFinish() {
        val cardType = fragmentDiceRollerBinding.diceRollerViewModel!!.getCardType()
        val house = fragmentDiceRollerBinding.diceRollerViewModel!!.getHouse()
        val sum = fragmentDiceRollerBinding.diceRollerViewModel!!.getSum()

        listener.getCard(playerId, cardType)
        cardType?.let {
            MapViewModel.finishedCardCheck = false
        }
        if (sum > 0)
            listener.onDiceRoll(playerId, sum, house)
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}