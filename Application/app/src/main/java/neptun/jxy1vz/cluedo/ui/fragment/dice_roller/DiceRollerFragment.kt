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
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel

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
        fragmentDiceRollerBinding.diceRollerViewModel = DiceRollerViewModel(fragmentDiceRollerBinding, context!!, felixFelicis, this)
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