package neptun.jxy1vz.hp_cluedo.domain.handler

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.constraintlayout.widget.Guideline
import androidx.core.animation.doOnEnd
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.*
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.domain.model.*
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.diceList
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.finishedCardCheck
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.isGameModeMulti
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mContext
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mPlayerId
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mapRoot
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.selectionList
import neptun.jxy1vz.hp_cluedo.ui.fragment.dice_roller.DiceRollerViewModel
import kotlin.math.abs
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
                    if (s.darkMark) {
                        map.interactionHandler.getCard(playerId, DiceRollerViewModel.CardType.DARK)
                        finishedCardCheck = false
                    } else
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
            gatewayAnimations.forEach { pair ->
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

    fun drawSelection(selRes: String, row: Int, col: Int, playerId: Int) {
        val targetPosition = Position(row, col)

        val selection = ImageView(mapRoot.mapLayout.context)
        selectionList.add(selection)
        selection.layoutParams = ConstraintLayout.LayoutParams(
            MATCH_CONSTRAINT,
            MATCH_CONSTRAINT
        )
        loadUrlImageIntoImageView(selRes, mContext!!, selection)
        selection.visibility = ImageView.VISIBLE
        setLayoutConstraintStart(selection, gameModels.cols[col])
        setLayoutConstraintTop(selection, gameModels.rows[row])
        val roomId =map.mapHandler.stepInRoom(Position(row, col))
        if (roomId != -1) {
            setLayoutConstraintEnd(selection, gameModels.cols[gameModels.roomList[roomId].right + 1])
            setLayoutConstraintBottom(selection, gameModels.rows[gameModels.roomList[roomId].bottom + 1])
        }
        else {
            setLayoutConstraintEnd(selection, gameModels.cols[col + 1])
            setLayoutConstraintBottom(selection, gameModels.rows[row + 1])
        }
        selection.setOnClickListener {
            if (playerId != mPlayerId)
                return@setOnClickListener

            if (finishedCardCheck) {
                map.playerHandler.stepPlayer(playerId, targetPosition)
                emptySelectionList()
            }
        }
        mapRoot.mapLayout.addView(selection)
    }

    suspend fun animatePlayerWalking(playerId: Int, start: Position, destination: Position) {
        if (map.mapHandler.stepInRoom(start) != -1 && map.mapHandler.stepInRoom(destination) != -1) {
            finishPlayerStep(playerId, destination)
            return
        }
        mapRoot.engine.setAnimationDuration(250)
        map.cameraHandler.moveCameraToPlayer(playerId)
        delay(500)
        val distancesFromOrigin = when {
            map.mapHandler.stepInRoom(start) == -1 -> map.mapHandler.dijkstra(start)
            else -> mergeRoutesFromRoom(playerId, start)
        }
        var origin = start
        val distancesFromDestination = when {
            map.mapHandler.stepInRoom(destination) == -1 -> map.mapHandler.dijkstra(destination)
            else -> mergeRoutesFromRoom(playerId, destination)
        }
        var currentStep = 1
        val stepCount =
            if (map.mapHandler.stepInRoom(destination) != -1) distancesFromOrigin[destination]!! - 1 else distancesFromOrigin[destination]!!

        val path = ArrayList<Position>()
        for (i in 1..stepCount) {
            for (entry in distancesFromOrigin.entries) {
                if (entry.value == currentStep) {
                    for (otherEntry in distancesFromDestination.entries) {
                        if (map.mapHandler.stepInRoom(entry.key) == -1 && otherEntry.key == entry.key && otherEntry.value == stepCount - currentStep && (path.size == 0 || path.size > 0 && (entry.key.col == path[path.lastIndex].col || entry.key.row == path[path.lastIndex].row))) {
                            path.add(entry.key)
                            if (currentStep == 1 && map.mapHandler.stepInRoom(origin) != -1)
                                origin = entry.key
                            currentStep++
                            break
                        }
                    }
                }
                if (currentStep > i)
                    continue
            }
        }

        var footprints: List<String>
        withContext(Dispatchers.IO) {
            footprints = CluedoDatabase.getInstance(mContext!!).assetDao().getAssetsByPrefix(AssetPrefixes.FOOTPRINT.string())!!.map { assetDBmodel -> assetDBmodel.url }
        }

        val forwardFeet = listOf(
            footprints[2],
            footprints[4],
            footprints[10]
        )
        val backwardFeet = listOf(
            footprints[0],
            footprints[1],
            footprints[9]
        )
        val leftFeet = listOf(
            footprints[5],
            footprints[6],
            footprints[11]
        )
        val rightFeet = listOf(
            footprints[7],
            footprints[8],
            footprints[12]
        )

        if (map.mapHandler.stepInRoom(start) != -1) {
            for (entry in distancesFromOrigin.entries) {
                val pos = entry.key
                if (map.mapHandler.isDoor(pos) && ((pos.row == origin.row && abs(pos.col - origin.col) == 1) || (pos.col == origin.col && abs(
                        pos.row - origin.row
                    ) == 1))
                ) {
                    origin = pos
                    break
                }
            }
            path.add(0, origin)
        }
        else if (path.size > 0) {
            val img = when (getDirection(start, path[0])) {
                "W" -> forwardFeet[2]
                "A" -> leftFeet[2]
                "S" -> backwardFeet[2]
                else -> rightFeet[2]
            }
            drawFoot(img, start.row, start.row + 1, start.col, start.col + 1)
        }

        var currentPosition = origin
        for (position in path) {
            val i = path.indexOf(position)
            var direction = getDirection(currentPosition, position)
            var colConstraintLeft: Int
            var rowConstraintTop: Int
            var colConstraintRight: Int
            var rowConstraintBottom: Int
            var imgRes = ""

            if (path.indexOf(position) == 0) {
                if (map.mapHandler.stepInRoom(start) != -1) {
                    map.gameModels.roomList.forEach { room ->
                        if (room.left <= start.col && room.right >= start.col) {
                            val roomPos = Position(room.top, room.left)
                            direction = when {
                                origin.col < roomPos.col -> "A"
                                origin.col >= roomPos.col && origin.col <= roomPos.col + room.right - room.left && origin.row < roomPos.row -> "W"
                                origin.col >= roomPos.col && origin.col <= roomPos.col + room.right - room.left && origin.row > roomPos.row -> "S"
                                origin.col > roomPos.col + room.right - room.left -> "D"
                                else -> "S"
                            }
                            imgRes = when (direction) {
                                "W" -> forwardFeet[2]
                                "A" -> leftFeet[2]
                                "S" -> backwardFeet[2]
                                else -> rightFeet[2]
                            }
                        }
                    }
                } else {
                    direction = getDirection(start, position)
                    imgRes = when (direction) {
                        "W" -> forwardFeet[i % 2]
                        "A" -> leftFeet[i % 2]
                        "S" -> backwardFeet[i % 2]
                        else -> rightFeet[i % 2]
                    }
                }
            } else {
                imgRes = when (direction) {
                    "W" -> forwardFeet[i % 2]
                    "A" -> leftFeet[i % 2]
                    "S" -> backwardFeet[i % 2]
                    else -> rightFeet[i % 2]
                }
            }

            when (direction) {
                "W" -> {
                    colConstraintLeft = currentPosition.col
                    colConstraintRight = currentPosition.col + 1
                    rowConstraintTop = currentPosition.row - 1
                    rowConstraintBottom = currentPosition.row + 1
                }
                "A" -> {
                    colConstraintLeft = currentPosition.col - 1
                    colConstraintRight = currentPosition.col + 1
                    rowConstraintTop = currentPosition.row
                    rowConstraintBottom = currentPosition.row + 1
                }
                "S" -> {
                    colConstraintLeft = currentPosition.col
                    colConstraintRight = currentPosition.col + 1
                    rowConstraintTop = currentPosition.row
                    rowConstraintBottom = currentPosition.row + 2
                }
                "D" -> {
                    colConstraintLeft = currentPosition.col
                    colConstraintRight = currentPosition.col + 2
                    rowConstraintTop = currentPosition.row
                    rowConstraintBottom = currentPosition.row + 1
                }
                else -> {
                    colConstraintLeft = currentPosition.col
                    colConstraintRight = currentPosition.col + 1
                    rowConstraintTop = currentPosition.row
                    rowConstraintBottom = currentPosition.row + 1
                }
            }
            drawFoot(imgRes, rowConstraintTop, rowConstraintBottom, colConstraintLeft, colConstraintRight)
            currentPosition = position
        }

        if (currentPosition == destination || map.mapHandler.isDoor(currentPosition)) {
            val pos1 = if (path.size > 1) path[path.lastIndex - 1] else start
            val pos2 = when {
                path.size == 0 -> destination
                path.size == 1 -> currentPosition
                map.mapHandler.isDoor(currentPosition) -> destination
                else -> currentPosition
            }
            val dir = when {
                map.mapHandler.isDoor(currentPosition) -> {
                    var roomPos = Position(-1, -1)
                    var roomWidth = 0
                    map.gameModels.doorList.forEach { door ->
                        if (door.position == currentPosition) {
                            roomPos = Position(door.room.top, door.room.left)
                            roomWidth = door.room.right - door.room.left
                        }
                    }
                    when {
                        currentPosition.col < roomPos.col -> "D"
                        currentPosition.col >= roomPos.col && currentPosition.col <= roomPos.col + roomWidth && currentPosition.row < roomPos.row -> "S"
                        currentPosition.col >= roomPos.col && currentPosition.col <= roomPos.col + roomWidth && currentPosition.row > roomPos.row -> "W"
                        currentPosition.col > roomPos.col + roomWidth -> "A"
                        else -> "S"
                    }
                }
                else -> getDirection(pos1, pos2)
            }
            val imgRes = when (dir) {
                "W" -> forwardFeet[2]
                "A" -> leftFeet[2]
                "S" -> backwardFeet[2]
                else -> rightFeet[2]
            }
            drawFoot(imgRes, currentPosition.row, currentPosition. row + 1, currentPosition.col, currentPosition.col + 1)
        }

        delay(250)
        mapRoot.engine.setAnimationDuration(1000)
        finishPlayerStep(playerId, destination)
    }

    private suspend fun drawFoot(imgRes: String, top: Int, bottom: Int, left: Int, right: Int) {
        val footImage = ImageView(mapRoot.mapLayout.context)
        loadUrlImageIntoImageView(imgRes, mContext!!, footImage)
        val layoutParams =
            ConstraintLayout.LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT)
        layoutParams.topToTop = map.gameModels.rows[top]
        layoutParams.bottomToBottom = map.gameModels.rows[bottom]
        layoutParams.startToStart = map.gameModels.cols[left]
        layoutParams.endToEnd = map.gameModels.cols[right]
        footImage.layoutParams = layoutParams
        mapRoot.mapLayout.addView(footImage)
        map.cameraHandler.moveCameraToPosition(
            mapRoot.mapLayout.findViewById<Guideline>(map.gameModels.rows[top]).top.toFloat(),
            mapRoot.mapLayout.findViewById<Guideline>(map.gameModels.cols[left]).left.toFloat()
        )
        delay(250)
        mapRoot.mapLayout.removeView(footImage)
    }

    private fun mergeRoutesFromRoom(playerId: Int, roomPos: Position): HashMap<Position, Int> {
        val roomId = map.mapHandler.stepInRoom(roomPos)
        var distances: HashMap<Position, Int>? = null
        map.gameModels.doorList.forEach { door ->
            if (door.room.id == roomId && (door.state == DoorState.OPENED || map.playerHandler.getPlayerById(
                    playerId
                ).hasAlohomora())
            )
                distances =
                    map.mapHandler.mergeDistances(map.mapHandler.dijkstra(door.position), distances)
        }
        return distances!!
    }

    private suspend fun finishPlayerStep(playerId: Int, destination: Position) {
        map.playerHandler.getPlayerById(playerId).pos = destination

        val pair = map.playerHandler.getPairById(playerId)
        map.uiHandler.setLayoutConstraintStart(
            pair.second,
            gameModels.cols[map.playerHandler.getPlayerById(playerId).pos.col]
        )
        map.uiHandler.setLayoutConstraintTop(
            pair.second,
            gameModels.rows[map.playerHandler.getPlayerById(playerId).pos.row]
        )
        map.uiHandler.setLayoutConstraintEnd(
            pair.second,
            gameModels.cols[map.playerHandler.getPlayerById(playerId).pos.col + 1]
        )
        map.uiHandler.setLayoutConstraintBottom(
            pair.second,
            gameModels.rows[map.playerHandler.getPlayerById(playerId).pos.row + 1]
        )

        delay(500)
        mapRoot.engine.setAnimationDuration(100)
        map.cameraHandler.moveCameraToPlayer(playerId)
        delay(500)
        mapRoot.engine.setAnimationDuration(1000)

        val starStep = map.mapHandler.stepOnStar(destination)

        when {
            map.mapHandler.stepInRoom(map.playerHandler.getPlayerById(playerId).pos) != -1 -> {
                if (playerId == mPlayerId) {
                    MapViewModel.userHasToIncriminate = true
                    MapViewModel.userHasToStepOrIncriminate = false
                    MapViewModel.userCanStep = false
                }
                if (!isGameModeMulti() || playerId == mPlayerId) {
                    map.interactionHandler.incrimination(
                        playerId,
                        map.mapHandler.stepInRoom(map.playerHandler.getPlayerById(playerId).pos)
                    )
                }
            }
            map.mapHandler.stepInRoom(map.playerHandler.getPlayerById(playerId).pos) == -1 -> {
                if (playerId != mPlayerId) {
                    if (starStep) {
                        MapViewModel.otherPlayerStepsOnStar = true
                        map.interactionHandler.getCard(
                            playerId,
                            DiceRollerViewModel.CardType.HELPER
                        )
                    } else {
                        map.gameSequenceHandler.moveToNextPlayer()
                    }
                } else {
                    MapViewModel.userFinishedHisTurn = true
                    if (!starStep) {
                        map.gameSequenceHandler.moveToNextPlayer()
                    } else
                        map.interactionHandler.getCard(
                            playerId,
                            DiceRollerViewModel.CardType.HELPER
                        )
                }
            }
        }
    }

    private fun getDirection(pos1: Position, pos2: Position): String {
        return when {
            pos1.col == pos2.col && pos1.row < pos2.row -> "S"
            pos1.col == pos2.col && pos1.row > pos2.row -> "W"
            pos1.col > pos2.col && pos1.row == pos2.row -> "A"
            pos1.col < pos2.col && pos1.row == pos2.row -> "D"
            else -> return ""
        }
    }

    fun emptySelectionList() {
        selectionList.forEach { sel ->
            mapRoot.mapLayout.removeView(sel)
        }
        selectionList = ArrayList()
    }

    fun setLayoutConstraintTop(view: View, row: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = row
        view.layoutParams = layoutParams
    }

    fun setLayoutConstraintStart(view: View, col: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.startToStart = col
        view.layoutParams = layoutParams
    }

    private fun setLayoutConstraintBottom(view: View, row: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomToBottom = row
        view.layoutParams = layoutParams
    }

    private fun setLayoutConstraintEnd(view: View, col: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.endToEnd = col
        view.layoutParams = layoutParams
    }

    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        var dice1Value: Int
        var dice2Value: Int
        var hogwartsDice: Int

        if (isGameModeMulti()) {
            dice1Value = MapViewModel.diceData.dice1
            dice2Value = MapViewModel.diceData.dice2
            hogwartsDice = MapViewModel.diceData.hogwartsDice
        }
        else {
            if (map.playerHandler.getPlayerById(playerInTurn).hasFelixFelicis()) {
                dice1Value = 6
                dice2Value = 6
            }
            else {
                dice1Value = Random.nextInt(1, 7)
                dice2Value = Random.nextInt(1, 7)
            }
            hogwartsDice = Random.nextInt(1, 7)
        }

        processDice(dice1Value, dice2Value, hogwartsDice)
    }

    private fun processDice(dice1Value: Int, dice2Value: Int, hogwartsDice: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val diceUrlList = CluedoDatabase.getInstance(mContext!!).assetDao().getAssetsByPrefix(AssetPrefixes.DICE.string())!!.map { assetDBmodel -> assetDBmodel.url }
            withContext(Dispatchers.Main) {
                loadUrlImageIntoImageView(when (dice1Value) {
                    1 -> diceUrlList[0]
                    2 -> diceUrlList[1]
                    3 -> diceUrlList[2]
                    4 -> diceUrlList[3]
                    5 -> diceUrlList[4]
                    else -> diceUrlList[5]
                }, mContext!!, diceList[0])

                loadUrlImageIntoImageView(when (dice2Value) {
                    1 -> diceUrlList[0]
                    2 -> diceUrlList[1]
                    3 -> diceUrlList[2]
                    4 -> diceUrlList[3]
                    5 -> diceUrlList[4]
                    else -> diceUrlList[5]
                }, mContext!!, diceList[2])

                loadUrlImageIntoImageView(when (hogwartsDice) {
                    1 -> diceUrlList[6]
                    2 -> diceUrlList[7]
                    3 -> diceUrlList[8]
                    4 -> diceUrlList[9]
                    5 -> diceUrlList[10]
                    else -> diceUrlList[11]
                }, mContext!!, diceList[1])

                var cardType: DiceRollerViewModel.CardType? = null
                var house: StateMachineHandler.HogwartsHouse? = null
                when (hogwartsDice) {
                    1 -> cardType = DiceRollerViewModel.CardType.DARK
                    2 -> cardType = DiceRollerViewModel.CardType.HELPER
                    3 -> house = StateMachineHandler.HogwartsHouse.GRYFFINDOR
                    4 -> house = StateMachineHandler.HogwartsHouse.HUFFLEPUFF
                    5 -> house = StateMachineHandler.HogwartsHouse.RAVENCLAW
                    6 -> house = StateMachineHandler.HogwartsHouse.SLYTHERIN
                }

                diceList.forEach { dice ->
                    (AnimatorInflater.loadAnimator(mContext!!, R.animator.disappear) as AnimatorSet).apply {
                        setTarget(dice)
                        start()
                        doOnEnd {
                            dice.visibility = ImageView.GONE
                            if (diceList.indexOf(dice) == 2) {
                                map.gameSequenceHandler.pause(playerInTurn, dice1Value + dice2Value, house)
                                cardType?.let {
                                    if (!isGameModeMulti())
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
        }
    }

    override fun onAnimationStart(animation: Animation?) {}
}