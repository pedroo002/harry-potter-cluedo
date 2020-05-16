package neptun.jxy1vz.cluedo.domain.handler

import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.mPlayerId
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.savedDiceValue
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.savedHouse
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.savedPlayerId
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.userCanStep
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.userFinishedHisTurn
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.userHasToIncriminate
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel.Companion.userHasToStepOrIncriminate

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

    fun moveToNextPlayer() {
        var idx = gameModels.playerList.indexOf(map.playerHandler.getPlayerById(playerInTurn))
        idx--
        if (idx < 0)
            idx = gameModels.playerList.lastIndex
        playerInTurn = gameModels.playerList[idx].id

        letPlayerTurn()
    }

    private fun letPlayerTurn() {
        if (isGameRunning) {
            map.cameraHandler.moveCameraToPlayer(playerInTurn)

            if (playerInTurn != mPlayerId)
                map.interactionHandler.rollWithDice(playerInTurn)
            else {
                userFinishedHisTurn = false
                userHasToIncriminate = false
                userHasToStepOrIncriminate = false
                userCanStep = true
                map.interactionHandler.showOptions(mPlayerId!!)
            }
        }
    }
}