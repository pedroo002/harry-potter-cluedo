package neptun.jxy1vz.hp_cluedo.domain.handler

import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.isGameModeMulti
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mPlayerId
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.savedDiceValue
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.savedHouse
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.savedPlayerId
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.userCanStep
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.userFinishedHisTurn
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.userHasToIncriminate
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.userHasToStepOrIncriminate

class GameSequenceHandler(private val map: MapViewModel.Companion) {
    fun pause(playerId: Int, diceSum: Int, house: StateMachineHandler.HogwartsHouse?) {
        map.pause = true
        savedPlayerId = playerId
        savedDiceValue = diceSum
        savedHouse = house
    }

    fun continueGame() {
        if (map.pause) {
            map.pause = false
            map.cameraHandler.moveCameraToPlayer(savedPlayerId)
            map.interactionHandler.onDiceRoll(savedPlayerId, savedDiceValue, savedHouse)
            savedPlayerId = -1
            savedDiceValue = 0
            savedHouse = null
        }
    }

    fun startGame() {
        isGameRunning = true

        letPlayerTurn()
    }

    fun moveToNextPlayer() {
        letPlayerTurn()
    }

    fun letPlayerTurn() {
        if (isGameRunning) {
            map.cameraHandler.moveCameraToPlayer(playerInTurn)

            if (playerInTurn == mPlayerId)
                blinkTile(playerInTurn)

            if (isGameModeMulti() && playerInTurn != mPlayerId) {
                return
            }

            if (playerInTurn != mPlayerId)
                map.interactionHandler.rollWithDice(playerInTurn)
            else {
                userFinishedHisTurn = false
                userHasToIncriminate = false
                userHasToStepOrIncriminate = false
                userCanStep = true
            }
        }
    }

    private fun blinkTile(playerId: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val tileToBlink = map.playerHandler.getPairById(playerId).second
            for (i in 1..3) {
                tileToBlink.visibility = ImageView.GONE
                delay(200)
                tileToBlink.visibility = ImageView.VISIBLE
                delay(200)
            }
        }
    }
}