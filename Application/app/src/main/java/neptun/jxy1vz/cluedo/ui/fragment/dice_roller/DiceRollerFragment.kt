package neptun.jxy1vz.cluedo.ui.fragment.dice_roller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentDiceRollerBinding
import neptun.jxy1vz.cluedo.domain.handler.StateMachineHandler

class DiceRollerFragment(private val listener: DiceResultInterface, private val playerId: Int, private val felixFelicis: Boolean) : Fragment(), DiceRollerViewModel.DiceFragmentListener {

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
        fragmentDiceRollerBinding.diceRollerViewModel = DiceRollerViewModel(fragmentDiceRollerBinding, context!!, felixFelicis, this)
        return fragmentDiceRollerBinding.root
    }

    override fun onFinish() {
        val cardType = fragmentDiceRollerBinding.diceRollerViewModel!!.getCardType()
        val house = fragmentDiceRollerBinding.diceRollerViewModel!!.getHouse()
        val sum = fragmentDiceRollerBinding.diceRollerViewModel!!.getSum()

        listener.getCard(playerId, cardType)
        if (sum > 0)
            listener.onDiceRoll(playerId, sum, house)
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}