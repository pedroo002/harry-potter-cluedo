package neptun.jxy1vz.cluedo.domain.handler

import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.domain.model.State
import neptun.jxy1vz.cluedo.domain.model.boolean
import neptun.jxy1vz.cluedo.ui.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.gryffindorState
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.hufflepuffState
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.player
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.ravenclawState
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.slytherinState

class StateMachineHandler(private val map: MapViewModel.Companion) {
    enum class HogwartsHouse {
        SLYTHERIN,
        RAVENCLAW,
        GRYFFINDOR,
        HUFFLEPUFF
    }

    fun setState(playerId: Int, house: HogwartsHouse) {
        map.cameraHandler.moveCameraToCorner(house)

        when (house) {
            HogwartsHouse.SLYTHERIN -> {
                setChanges(
                    playerId,
                    gameModels.slytherinStates,
                    gameModels.passageWayListSlytherin,
                    gameModels.passageWayVisibilitiesSlytherin,
                    slytherinState,
                    HogwartsHouse.SLYTHERIN
                )
                slytherinState++
                if (slytherinState == 16)
                    slytherinState = 0
            }
            HogwartsHouse.RAVENCLAW -> {
                setChanges(
                    playerId,
                    gameModels.ravenclawStates,
                    gameModels.passageWayListRavenclaw,
                    gameModels.passageWayVisibilitiesRavenclaw,
                    ravenclawState,
                    HogwartsHouse.RAVENCLAW
                )
                ravenclawState++
                if (ravenclawState == 16)
                    ravenclawState = 0
            }
            HogwartsHouse.GRYFFINDOR -> {
                setChanges(
                    playerId,
                    gameModels.gryffindorStates,
                    gameModels.passageWayListGryffindor,
                    gameModels.passageWayVisibilitiesGryffindor,
                    gryffindorState,
                    HogwartsHouse.GRYFFINDOR
                )
                gryffindorState++
                if (gryffindorState == 16)
                    gryffindorState = 0
            }
            HogwartsHouse.HUFFLEPUFF -> {
                setChanges(
                    playerId,
                    gameModels.hufflepuffStates,
                    gameModels.passageWayListHufflepuff,
                    gameModels.passageWayVisibilitiesHufflepuff,
                    hufflepuffState,
                    HogwartsHouse.HUFFLEPUFF
                )
                hufflepuffState++
                if (hufflepuffState == 16)
                    hufflepuffState = 0
            }
        }
    }

    fun setChanges(
        playerId: Int,
        stateList: List<State>,
        gateways: List<Int>,
        visibilities: List<List<Boolean>>,
        state: Int,
        house: HogwartsHouse
    ) {
        val gatewayNumbers: MutableList<Int> = ArrayList()
        for (i in 0..2) {
            stateList[state * 3 + i].passageWay?.let {
                gatewayNumbers.add(i)
            }
        }

        val visibleGatewaySerialNumbers: MutableList<Int> = ArrayList()
        for (i in visibilities[state].indices) {
            if (visibilities[state][i])
                visibleGatewaySerialNumbers.add(i)
        }

        for (i in gatewayNumbers.indices) {
            mapRoot.mapLayout.findViewById<ImageView>(gateways[visibleGatewaySerialNumbers[i]])
                .setOnClickListener {
                    if (it.visibility == View.VISIBLE && playerInTurn == player.id)
                        map.playerHandler.teleport(
                            playerInTurn,
                            stateList[state * 3 + gatewayNumbers[i]].roomId,
                            stateList[state * 3 + gatewayNumbers[i]].passageWay!!
                        )
                }
        }

        val gatewayAnimations: MutableList<Pair<ImageView, Boolean>> = ArrayList()
        for (p in gateways) {
            val visibility = visibilities[state][gateways.indexOf(p)]

            if (state > 0) {
                if (visibilities[state - 1][gateways.indexOf(p)] != visibility) {
                    gatewayAnimations.add(Pair(mapRoot.mapLayout.findViewById(p), visibility))
                    if (visibility)
                        map.uiHandler.setViewVisibility(mapRoot.mapLayout.findViewById(p), visibility)
                }
            } else
                map.uiHandler.setViewVisibility(mapRoot.mapLayout.findViewById(p), visibility)
        }

        for (s in stateList) {
            if (s.serialNum == state) {
                val oldState = gameModels.doorList[s.doorId].state
                val doorAnimation = oldState != s.doorState
                gameModels.doorList[s.doorId].state = s.doorState
                val ivDoor = when (s.doorId) {
                    0 -> mapRoot.mapLayout.ivDoor0
                    2 -> mapRoot.mapLayout.ivDoor2
                    4 -> mapRoot.mapLayout.ivDoor4
                    6 -> mapRoot.mapLayout.ivDoor6
                    7 -> mapRoot.mapLayout.ivDoor7
                    12 -> mapRoot.mapLayout.ivDoor12
                    13 -> mapRoot.mapLayout.ivDoor13
                    15 -> mapRoot.mapLayout.ivDoor15
                    17 -> mapRoot.mapLayout.ivDoor17
                    19 -> mapRoot.mapLayout.ivDoor19
                    20 -> mapRoot.mapLayout.ivDoor20
                    else -> mapRoot.mapLayout.ivDoor21
                }

                val darkMarkAnimation = when (stateList.indexOf(s) % 3) {
                    2 -> stateList.indexOf(s) >= 5 && stateList[stateList.indexOf(s) - 3].darkMark != s.darkMark
                    else -> false
                }
                val ivDarkMark = when (house) {
                    HogwartsHouse.SLYTHERIN -> {
                        mapRoot.mapLayout.ivDarkMarkSlytherin
                    }
                    HogwartsHouse.RAVENCLAW -> {
                        mapRoot.mapLayout.ivDarkMarkRavenclaw
                    }
                    HogwartsHouse.GRYFFINDOR -> {
                        mapRoot.mapLayout.ivDarkMarkGryffindor
                    }
                    HogwartsHouse.HUFFLEPUFF -> {
                        mapRoot.mapLayout.ivDarkMarkHufflepuff
                    }
                }

                if (s.serialNum == 0) {
                    map.uiHandler.setViewVisibility(ivDoor, s.doorState.boolean())
                    map.uiHandler.setViewVisibility(ivDarkMark, s.darkMark)
                } else
                    map.uiHandler.animateMapChanges(
                        playerId,
                        s,
                        stateList.indexOf(s),
                        doorAnimation,
                        ivDoor,
                        darkMarkAnimation,
                        ivDarkMark,
                        gatewayAnimations
                    )
            }
        }
    }
}