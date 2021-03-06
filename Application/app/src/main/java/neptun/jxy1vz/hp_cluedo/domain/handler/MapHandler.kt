package neptun.jxy1vz.hp_cluedo.domain.handler

import neptun.jxy1vz.hp_cluedo.domain.model.*
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.gryffindorState
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.hufflepuffState
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.isGameModeMulti
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mPlayerId
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mapGraph
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.ravenclawState
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.slytherinState
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set
import kotlin.math.min
import kotlin.random.Random

class MapHandler(private val map: MapViewModel.Companion) {
    fun stepInRoom(pos: Position): Int {
        gameModels.roomList.forEach { room ->
            if (pos.row >= room.top && pos.row <= room.bottom && pos.col >= room.left && pos.col <= room.right)
                return room.id
        }
        return -1
    }

    fun stepOnStar(pos: Position): Boolean {
        gameModels.starList.forEach {star ->
            if (star == pos)
                return true
        }
        return false
    }

    fun isFieldOccupied(pos: Position): Boolean {
        gameModels.playerList.forEach { player ->
            if (player.pos == pos)
                return true
        }
        return false
    }

    fun isDoor(pos: Position): Boolean {
        gameModels.doorList.forEach {door ->
            if (door.position == pos)
                return true
        }
        return false
    }

    fun dijkstra(current: Position): HashMap<Position, Int> {
        val distances = HashMap<Position, Int>()
        val unvisited = HashSet<Position>()

        mapGraph.adjacencyMap.keys.forEach {field ->
            unvisited.add(field)
            if (field == current)
                distances[field] = 0
            else
                distances[field] = Int.MAX_VALUE
        }

        while (unvisited.isNotEmpty()) {
            var field: Position = unvisited.elementAt(0)
            distances.forEach {pair ->
                if (unvisited.contains(pair.key) && pair.value < distances[field]!!)
                    field = pair.key
            }

            mapGraph.adjacencyMap[field]!!.forEach {neighbour ->
                if (unvisited.contains(neighbour)) {
                    if (stepInRoom(field) == -1 || !isDoor(neighbour))
                        distances[neighbour] = min(distances[neighbour]!!, distances[field]!! + 1)
                }
            }
            unvisited.remove(field)
        }

        return distances
    }

    fun mergeDistances(
        map1: HashMap<Position, Int>,
        map2: HashMap<Position, Int>? = null
    ): HashMap<Position, Int> {
        val intersection: HashMap<Position, Int> = HashMap()
        map1.keys.forEach {pos ->
            intersection[pos] = map1[pos]!!
        }
        map2?.keys?.forEach { pos ->
            if (intersection.containsKey(pos))
                intersection[pos] = min(map1[pos]!!, map2[pos]!!)
            else
                intersection[pos] = map2[pos]!!
        }
        return intersection
    }

    fun calculateMovingOptions(playerId: Int, stepCount: Int) {
        map.uiHandler.emptySelectionList()

        var limit = stepCount

        var distances: HashMap<Position, Int>? = null
        if (stepInRoom(map.playerHandler.getPlayerById(playerId).pos) == -1)
            distances = dijkstra(map.playerHandler.getPlayerById(playerId).pos)
        else {
            limit--
            val roomId = stepInRoom(map.playerHandler.getPlayerById(playerId).pos)
            gameModels.doorList.forEach {door ->
                if (door.room.id == roomId && (door.state == DoorState.OPENED || map.playerHandler.getPlayerById(playerId).hasAlohomora())) {
                    distances = mergeDistances(dijkstra(door.position), distances)
                }
            }
        }

        if (playerId == mPlayerId) {
            if (!distances.isNullOrEmpty()) {
                for (x in 0..MapViewModel.COLS) {
                    for (y in 0..MapViewModel.ROWS) {
                        val current = Position(y, x)
                        if (stepInRoom(current) == -1 && !isFieldOccupied(current) && distances!![current]!! <= limit) {
                            val sel = if (stepOnStar(current)) map.gameModels.starSelection else map.gameModels.fieldSelection
                            map.uiHandler.drawSelection(sel, y, x, playerId)
                        }
                    }
                }
            }

            if (stepInRoom(map.playerHandler.getPlayerById(playerId).pos) == -1) {
                gameModels.doorList.forEach { door ->
                    if (distances!![door.position]!! <= limit - 1 && (door.state == DoorState.OPENED || map.playerHandler.getPlayerById(playerId).hasAlohomora())) {
                        map.uiHandler.drawSelection(door.room.selection, door.room.top, door.room.left, playerId)
                    }
                }
            }
        } else if (!isGameModeMulti()) {
            if (distances != null) {
                val validKeys = ArrayList<Position>()
                val playerPos = map.playerHandler.getPlayerById(playerId).pos
                distances!!.keys.forEach { pos ->
                    if (stepInRoom(pos) != -1 && (stepInRoom(playerPos) == -1 || stepInRoom(playerPos) == stepInRoom(pos))) {
                        val roomId = stepInRoom(pos)
                        gameModels.doorList.forEach { door ->
                            if (door.room.id == roomId) {
                                if (!validKeys.contains(pos) && distances!![door.position]!! < limit && (door.state == DoorState.OPENED || map.playerHandler.getPlayerById(playerId).hasAlohomora() || stepInRoom(playerPos) == stepInRoom(pos)))
                                    validKeys.add(pos)
                            }
                        }
                    }
                    else if (stepInRoom(pos) == -1 && !(
                                ((isFieldOccupied(pos) && stepInRoom(playerPos) == -1)
                                        || (isFieldOccupied(pos) && stepInRoom(playerPos) != -1 && playerPos != pos))
                                        || distances!![pos]!! > limit)
                    )
                        validKeys.add(pos)
                }

                val desiredRooms = ArrayList<Room>()
                for (room in gameModels.roomList) {
                    if ((map.playerHandler.getPlayerById(playerId) as ThinkingPlayer).hasSolution() && room.id == 4) {
                        desiredRooms.add(room)
                        break
                    }
                    else if (!(map.playerHandler.getPlayerById(playerId) as ThinkingPlayer).hasConclusion(room.name) && room.id != 4) {
                        desiredRooms.add(room)
                    }
                    else if (!map.playerHandler.hasKnowledgeOfUnusedCards(playerId) && room.id == 4)
                        desiredRooms.add(room)
                }

                val roomDistances = HashMap<Room, Int>()
                desiredRooms.forEach { room ->
                    gameModels.doorList.forEach { door ->
                        if (door.room.id == room.id && (door.state == DoorState.OPENED || map.playerHandler.getPlayerById(playerId).hasAlohomora())) {
                            if (roomDistances.containsKey(room))
                                roomDistances[room] = min(roomDistances[room]!!, distances!![door.position]!! + 1)
                            else
                                roomDistances[room] = distances!![door.position]!! + 1
                        }
                    }
                }
                val sortedDistances = roomDistances.toList().sortedBy { (_, distance) -> distance }.toMap()

                var stepped = false
                for (roomDistance in sortedDistances) {
                    for (i in 0..4) {
                        val roomPos = Position(roomDistance.key.top, roomDistance.key.left + i)
                        if (validKeys.contains(roomPos)) {
                            map.playerHandler.stepPlayer(playerId, roomPos)
                            stepped = true
                            break
                        }
                    }
                    if (stepped)
                        break
                }

                if (!stepped) {
                    val roomOfPlayer = stepInRoom(playerPos)
                    val listOfStateLists = listOf(gameModels.slytherinStates, gameModels.ravenclawStates, gameModels.gryffindorStates, gameModels.hufflepuffStates)
                    val currentStates = listOf(slytherinState, ravenclawState, gryffindorState, hufflepuffState)
                    for (list in listOfStateLists) {
                        for (state in list) {
                            if (state.serialNum == currentStates[listOfStateLists.indexOf(list)] && state.roomId == roomOfPlayer) {
                                state.passageWay?.let {
                                    for (room in desiredRooms) {
                                        if (room.id == it) {
                                            map.playerHandler.stepPlayer(playerId, Position(room.top, room.left))
                                            stepped = true
                                            break
                                        }
                                    }
                                }
                            }
                            if (stepped)
                                break
                        }
                        if (stepped)
                            break
                    }
                }

                if (!stepped && sortedDistances.isNotEmpty()) {
                    for (i in sortedDistances.keys.toList().indices) {
                        var distancesFromRoom = HashMap<Position, Int>()
                        val roomId = sortedDistances.keys.toList()[i].id
                        gameModels.doorList.forEach {door ->
                            if (door.room.id == roomId && (door.state == DoorState.OPENED || map.playerHandler.getPlayerById(playerId).hasAlohomora())) {
                                distancesFromRoom =
                                    mergeDistances(dijkstra(door.position), distancesFromRoom)
                            }
                        }
                        val sortedMap = distancesFromRoom.toList().sortedBy { (_, distance) -> distance }.toMap()
                        for (distance in sortedMap) {
                            if (validKeys.contains(distance.key)) {
                                map.playerHandler.stepPlayer(playerId, distance.key)
                                stepped = true
                                break
                            }
                        }
                        if (stepped)
                            break
                    }
                }

                if (!stepped) {
                    for (pos in validKeys) {
                        if (stepOnStar(pos)) {
                            map.playerHandler.stepPlayer(playerId, pos)
                            stepped = true
                            break
                        }
                    }
                }

                if (!stepped)
                    map.playerHandler.stepPlayer(playerId, validKeys[Random.nextInt(0, validKeys.size)])
            } else {
                map.gameSequenceHandler.moveToNextPlayer()
            }
        }
    }
}