package neptun.jxy1vz.cluedo.domain.handler

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.model.DoorState
import neptun.jxy1vz.cluedo.domain.model.Position
import neptun.jxy1vz.cluedo.domain.model.State
import neptun.jxy1vz.cluedo.domain.model.boolean
import neptun.jxy1vz.cluedo.ui.fragment.dice_roller.DiceRollerViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.diceList
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mContext
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.selectionList
import kotlin.random.Random

class UIHandler(private val map: MapViewModel.Companion) : Animation.AnimationListener {
    fun animateMapChanges(
        playerId: Int,
        s: State,
        idx: Int,
        doorAnimation: Boolean,
        ivDoor: ImageView,
        darkMarkAnimation: Boolean,
        ivDarkMark: ImageView,
        gatewayAnimations: List<Pair<ImageView, Boolean>>
    ) {
        if (s.doorState == DoorState.CLOSED)
            setViewVisibility(ivDoor, s.doorState.boolean())
        if (s.darkMark)
            setViewVisibility(ivDarkMark, s.darkMark)

        if (doorAnimation) {
            val doorAnimId = when (s.doorState) {
                DoorState.CLOSED -> R.animator.appear
                else -> R.animator.disappear
            }
            (AnimatorInflater.loadAnimator(mContext, doorAnimId) as AnimatorSet).apply {
                setTarget(ivDoor)
                start()
                doOnEnd {
                    if (s.doorState == DoorState.OPENED)
                        setViewVisibility(ivDoor, s.doorState.boolean())
                    if (!darkMarkAnimation && gatewayAnimations.isEmpty() && idx % 3 == 2) {
                        if (!map.pause)
                            map.cameraHandler.moveCameraToPlayer(playerInTurn)
                        map.gameSequenceHandler.continueGame()
                    }
                }
            }
        }
        if (darkMarkAnimation) {
            val darkMarkAnimId = when (s.darkMark) {
                true -> R.animator.appear
                else -> R.animator.disappear
            }
            (AnimatorInflater.loadAnimator(mContext!!, darkMarkAnimId) as AnimatorSet).apply {
                setTarget(ivDarkMark)
                start()
                doOnEnd {
                    if (s.darkMark)
                        map.interactionHandler.getCard(playerId, DiceRollerViewModel.CardType.DARK)
                    else
                        setViewVisibility(ivDarkMark, s.darkMark)

                    if (gatewayAnimations.isEmpty()) {
                        if (!s.darkMark && idx % 3 == 2) {
                            if (!map.pause)
                                map.cameraHandler.moveCameraToPlayer(playerInTurn)
                            map.gameSequenceHandler.continueGame()
                        }
                    }
                }
            }
        }
        if (gatewayAnimations.isNotEmpty()) {
            for (pair in gatewayAnimations) {
                val gatewayAnimId = when (pair.second) {
                    true -> R.animator.appear
                    else -> R.animator.disappear
                }
                (AnimatorInflater.loadAnimator(mContext!!, gatewayAnimId) as AnimatorSet).apply {
                    setTarget(pair.first)
                    start()
                    doOnEnd {
                        if (gatewayAnimations.indexOf(pair) == gatewayAnimations.lastIndex) {
                            if (!map.pause)
                                map.cameraHandler.moveCameraToPlayer(playerInTurn)
                            if (!s.darkMark && idx % 3 == 2)
                                map.gameSequenceHandler.continueGame()
                        }
                        if (!pair.second)
                            setViewVisibility(pair.first, pair.second)
                    }
                }
            }
        }
    }

    fun setViewVisibility(imageView: ImageView, visible: Boolean) {
        if (visible)
            imageView.visibility = View.VISIBLE
        else
            imageView.visibility = View.GONE
    }

    fun drawSelection(@DrawableRes selRes: Int, row: Int, col: Int, playerId: Int) {
        val targetPosition = Position(row, col)

        val selection = ImageView(mapRoot.mapLayout.context)
        selectionList.add(selection)
        selection.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        selection.setImageResource(selRes)
        selection.visibility = ImageView.VISIBLE
        setLayoutConstraintStart(selection, gameModels.cols[col])
        setLayoutConstraintTop(selection, gameModels.rows[row])
        selection.setOnClickListener {
            map.playerHandler.stepPlayer(playerId, targetPosition)
            emptySelectionList()
        }
        mapRoot.mapLayout.addView(selection)
    }

    fun emptySelectionList() {
        for (sel in selectionList)
            mapRoot.mapLayout.removeView(sel)
        selectionList = ArrayList()
    }

    @BindingAdapter("app:layout_constraintTop_toTopOf")
    fun setLayoutConstraintTop(view: View, row: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = row
        view.layoutParams = layoutParams
    }

    @BindingAdapter("app:layout_constraintStart_toStartOf")
    fun setLayoutConstraintStart(view: View, col: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.startToStart = col
        view.layoutParams = layoutParams
    }

    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        var dice1Value = Random.nextInt(1, 7)
        var dice2Value = Random.nextInt(1, 7)
        val hogwartsDice = Random.nextInt(1, 7)

        if (map.playerHandler.getPlayerById(playerInTurn).hasFelixFelicis()) {
            dice1Value = 6
            dice2Value = 6
        }

        diceList[0].setImageResource(
            when (dice1Value) {
                1 -> R.drawable.dice1
                2 -> R.drawable.dice2
                3 -> R.drawable.dice3
                4 -> R.drawable.dice4
                5 -> R.drawable.dice5
                else -> R.drawable.dice6
            }
        )

        diceList[2].setImageResource(
            when (dice2Value) {
                1 -> R.drawable.dice1
                2 -> R.drawable.dice2
                3 -> R.drawable.dice3
                4 -> R.drawable.dice4
                5 -> R.drawable.dice5
                else -> R.drawable.dice6
            }
        )

        diceList[1].setImageResource(
            when (hogwartsDice) {
                1 -> R.drawable.helper_card
                2 -> R.drawable.gryffindor
                3 -> R.drawable.slytherin
                4 -> R.drawable.hufflepuff
                5 -> R.drawable.ravenclaw
                else -> R.drawable.dark_mark
            }
        )

        var cardType: DiceRollerViewModel.CardType? = null
        var house: StateMachineHandler.HogwartsHouse? = null
        when (hogwartsDice) {
            1 -> cardType = DiceRollerViewModel.CardType.HELPER
            2 -> house = StateMachineHandler.HogwartsHouse.GRYFFINDOR
            3 -> house = StateMachineHandler.HogwartsHouse.SLYTHERIN
            4 -> house = StateMachineHandler.HogwartsHouse.HUFFLEPUFF
            5 -> house = StateMachineHandler.HogwartsHouse.RAVENCLAW
            6 -> cardType = DiceRollerViewModel.CardType.DARK
        }

        for (dice in diceList) {
            (AnimatorInflater.loadAnimator(mContext!!, R.animator.disappear) as AnimatorSet).apply {
                setTarget(dice)
                start()
                doOnEnd {
                    dice.visibility = ImageView.GONE
                    if (diceList.indexOf(dice) == 2) {
                        map.gameSequenceHandler.pause(playerInTurn, dice1Value + dice2Value, house)
                        cardType?.let {
                            map.interactionHandler.getCard(playerInTurn, cardType)
                        }
                        house?.let {
                            map.stateMachineHandler.setState(playerInTurn, house)
                        }
                    }
                }
            }
        }
    }

    override fun onAnimationStart(animation: Animation?) {}
}