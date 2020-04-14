package neptun.jxy1vz.cluedo.ui.map

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Matrix
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.otaliastudios.zoom.ZoomEngine
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.*
import neptun.jxy1vz.cluedo.model.helper.*
import neptun.jxy1vz.cluedo.ui.dialog.RescuedFromDarkCardDialog
import neptun.jxy1vz.cluedo.ui.dialog.accusation.AccusationDialog
import neptun.jxy1vz.cluedo.ui.dialog.card_dialog.dark_mark.DarkCardDialog
import neptun.jxy1vz.cluedo.ui.dialog.card_dialog.helper.HelperCardDialog
import neptun.jxy1vz.cluedo.ui.dialog.card_dialog.reveal_mystery_card.CardRevealDialog
import neptun.jxy1vz.cluedo.ui.dialog.dice.DiceRollerDialog
import neptun.jxy1vz.cluedo.ui.dialog.dice.DiceRollerViewModel.CardType
import neptun.jxy1vz.cluedo.ui.dialog.endgame.EndOfGameDialog
import neptun.jxy1vz.cluedo.ui.dialog.incrimination.IncriminationDialog
import neptun.jxy1vz.cluedo.ui.dialog.information.InformationDialog
import neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.card_loss.CardLossDialog
import neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.hp_loss.HpLossDialog
import neptun.jxy1vz.cluedo.ui.dialog.show_card.ShowCardDialog
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.elementAt
import kotlin.collections.indices
import kotlin.collections.isNotEmpty
import kotlin.collections.isNullOrEmpty
import kotlin.collections.iterator
import kotlin.collections.lastIndex
import kotlin.collections.set
import kotlin.math.min
import kotlin.random.Random

class MapViewModel(
    private val activityListener: MapActivityListener,
    private val context: Context,
    playerId: Int,
    private var playerImagePairs: List<Pair<Player, ImageView>>,
    private var mapRoot: ZoomLayout,
    private val fm: FragmentManager
) : BaseObservable(),
    DiceRollerDialog.DiceResultInterface, DarkCardDialog.DarkCardDialogListener,
    CardLossDialog.CardLossDialogListener, IncriminationDialog.MapInterface,
    DialogDismiss, ZoomEngine.Listener {

    private var player = getPlayerById(playerId)
    private var mapGraph: Graph<Position>
    private var selectionList: ArrayList<ImageView> = ArrayList()

    private var slytherinState = 0
    private var ravenclawState = 0
    private var gryffindorState = 0
    private var hufflepuffState = 0

    private var isGameRunning = true
    private var playerInTurn = playerId
    private var userFinishedHisTurn = false
    private var userHasToIncriminate = false
    private var userHasToStep = false

    enum class HogwartsHouse {
        SLYTHERIN,
        RAVENCLAW,
        GRYFFINDOR,
        HUFFLEPUFF
    }

    companion object {
        const val ROWS = 24
        const val COLS = 24
    }

    private fun moveToNextPlayer() {
        var idx = playerList.indexOf(getPlayerById(playerInTurn))
        idx--
        if (idx < 0)
            idx = playerList.lastIndex
        playerInTurn = playerList[idx].id

        letPlayerTurn()
    }

    private fun letPlayerTurn() {
        if (isGameRunning) {
            moveCameraToPlayer(playerInTurn)

            if (playerInTurn != player.id)
                rollWithDice(playerInTurn)
            else {
                userFinishedHisTurn = false
                userHasToIncriminate = false
                userHasToStep = false
                if (stepInRoom(player.pos) != -1)
                    incrimination(player.id, stepInRoom(player.pos))
                else {
                    showOptions(player.id)
                }
            }
        }
    }

    init {
        mapRoot.engine.addListener(this)

        mapRoot.mapLayout.setOnClickListener {
            moveCameraToPlayer(playerInTurn)
        }

        val initHp = when (playerList.size) {
            3 -> 60
            4 -> 70
            else -> 80
        }
        for (player in playerList) {
            player.hp = initHp
            getCard(player.id, CardType.HELPER)
        }

        setState(playerId, HogwartsHouse.SLYTHERIN)
        setState(playerId, HogwartsHouse.RAVENCLAW)
        setState(playerId, HogwartsHouse.GRYFFINDOR)
        setState(playerId, HogwartsHouse.HUFFLEPUFF)

        for (pair in playerImagePairs) {
            setLayoutConstraintStart(pair.second, cols[pair.first.pos.col])
            setLayoutConstraintTop(pair.second, rows[pair.first.pos.row])
        }

        mapGraph = Graph()

        for (x in 0..COLS) {
            for (y in 0..ROWS) {
                val current = Position(y, x)
                if (stepInRoom(current) == -1) {
                    if (y > 0 && stepInRoom(Position(y - 1, x)) == -1)
                        mapGraph.addEdge(current, Position(y - 1, x))
                    if (y < 24 && stepInRoom(Position(y + 1, x)) == -1)
                        mapGraph.addEdge(current, Position(y + 1, x))
                    if (x > 0 && stepInRoom(Position(y, x - 1)) == -1)
                        mapGraph.addEdge(current, Position(y, x - 1))
                    if (x < 24 && stepInRoom(Position(y, x + 1)) == -1)
                        mapGraph.addEdge(current, Position(y, x + 1))

                }
            }
        }

        for (door: Door in doorList) {
            for (i in 0..4) {
                mapGraph.addEdge(Position(door.room.top, door.room.left + i), door.position)
                mapGraph.addEdge(door.position, Position(door.room.top, door.room.left + i))
            }
        }

        letPlayerTurn()
    }

    private fun moveCameraToPlayer(playerId: Int) {
        val x = getPairById(playerId).second.left.toFloat() - context.resources.displayMetrics.widthPixels / 2
        val y = getPairById(playerId).second.top.toFloat() - context.resources.displayMetrics.heightPixels / 2

        mapRoot.panTo(-x, -y, true)
    }

    private fun moveCameraToCorner(house: HogwartsHouse) {
        val x = when(house) {
            HogwartsHouse.SLYTHERIN -> mapRoot.mapLayout.ivMap.left.toFloat()
            HogwartsHouse.RAVENCLAW -> mapRoot.mapLayout.ivMap.right.toFloat()
            HogwartsHouse.GRYFFINDOR -> mapRoot.mapLayout.ivMap.right.toFloat()
            else -> mapRoot.mapLayout.ivMap.left.toFloat()
        }
        val y = when(house) {
            HogwartsHouse.SLYTHERIN -> mapRoot.mapLayout.ivMap.top.toFloat()
            HogwartsHouse.RAVENCLAW -> mapRoot.mapLayout.ivMap.top.toFloat()
            HogwartsHouse.GRYFFINDOR -> mapRoot.mapLayout.ivMap.bottom.toFloat()
            else -> mapRoot.mapLayout.ivMap.bottom.toFloat()
        }

        mapRoot.panTo(-x, -y, true)
    }

    private fun setState(playerId: Int, house: HogwartsHouse) {
        moveCameraToCorner(house)

        when (house) {
            HogwartsHouse.SLYTHERIN -> {
                setChanges(playerId,
                    slytherinStates,
                    passageWayListSlytherin,
                    passageWayVisibilitiesSlytherin,
                    slytherinState,
                    HogwartsHouse.SLYTHERIN
                )
                slytherinState++
                if (slytherinState == 16)
                    slytherinState = 0
            }
            HogwartsHouse.RAVENCLAW -> {
                setChanges(playerId,
                    ravencalwStates,
                    passageWayListRavenclaw,
                    passageWayVisibilitiesRavenclaw,
                    ravenclawState,
                    HogwartsHouse.RAVENCLAW
                )
                ravenclawState++
                if (ravenclawState == 16)
                    ravenclawState = 0
            }
            HogwartsHouse.GRYFFINDOR -> {
                setChanges(playerId,
                    gryffindorStates,
                    passageWayListGryffindor,
                    passageWayVisibilitiesGryffindor,
                    gryffindorState,
                    HogwartsHouse.GRYFFINDOR
                )
                gryffindorState++
                if (gryffindorState == 16)
                    gryffindorState = 0
            }
            HogwartsHouse.HUFFLEPUFF -> {
                setChanges(playerId,
                    hufflepuffStates,
                    passageWayListHufflepuff,
                    passageWayVisibilitiesHufflepuff,
                    hufflepuffState,
                    HogwartsHouse.HUFFLEPUFF
                )
                hufflepuffState++
                if (hufflepuffState == 16)
                    hufflepuffState = 0
            }
        }
    }

    private fun setChanges(
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
            mapRoot.mapLayout.findViewById<ImageView>(gateways[visibleGatewaySerialNumbers[i]]).setOnClickListener {
                if (it.visibility == View.VISIBLE)
                    teleport(playerInTurn, stateList[state * 3 + gatewayNumbers[i]].roomId, stateList[state * 3 + gatewayNumbers[i]].passageWay!!)
            }
        }

        val gatewayAnimations: MutableList<Pair<ImageView, Boolean>> = ArrayList()
        for (p in gateways) {
            val visibility = visibilities[state][gateways.indexOf(p)]

            if (state > 0) {
                if (visibilities[state - 1][gateways.indexOf(p)] != visibility) {
                    gatewayAnimations.add(Pair(mapRoot.mapLayout.findViewById(p), visibility))
                    if (visibility)
                        setViewVisibility(mapRoot.mapLayout.findViewById(p), visibility)
                }
            }
            else
                setViewVisibility(mapRoot.mapLayout.findViewById(p), visibility)
        }

        for (s in stateList) {
            if (s.serialNum == state) {
                val oldState = doorList[s.doorId].state
                val doorAnimation = oldState != s.doorState
                doorList[s.doorId].state = s.doorState
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
                    setViewVisibility(ivDoor, s.doorState.boolean())
                    setViewVisibility(ivDarkMark, s.darkMark)
                }
                else
                    animateMapChanges(playerId, s, doorAnimation, ivDoor, darkMarkAnimation, ivDarkMark, gatewayAnimations)
            }
        }
    }

    private fun animateMapChanges(playerId: Int, s: State, doorAnimation: Boolean, ivDoor: ImageView, darkMarkAnimation: Boolean, ivDarkMark: ImageView, gatewayAnimations: List<Pair<ImageView, Boolean>>) {
        if (s.doorState == DoorState.CLOSED)
            setViewVisibility(ivDoor, s.doorState.boolean())
        if (s.darkMark)
            setViewVisibility(ivDarkMark, s.darkMark)

        if (doorAnimation) {
            val doorAnimId = when (s.doorState) {
                DoorState.CLOSED -> R.animator.appear
                else -> R.animator.disappear
            }
            (AnimatorInflater.loadAnimator(context, doorAnimId) as AnimatorSet).apply {
                setTarget(ivDoor)
                start()
                doOnEnd {
                    if (s.doorState == DoorState.OPENED)
                        setViewVisibility(ivDoor, s.doorState.boolean())
                    if (!darkMarkAnimation)
                        moveCameraToPlayer(playerInTurn)
                }
            }
        }
        if (darkMarkAnimation) {
            val darkMarkAnimId = when (s.darkMark) {
                true -> R.animator.appear
                else -> R.animator.disappear
            }
            (AnimatorInflater.loadAnimator(context, darkMarkAnimId) as AnimatorSet).apply {
                setTarget(ivDarkMark)
                start()
                doOnEnd {
                    if (gatewayAnimations.isEmpty())
                        moveCameraToPlayer(playerInTurn)
                    if (s.darkMark)
                        getCard(playerId, CardType.DARK)
                    else
                        setViewVisibility(ivDarkMark, s.darkMark)
                }
            }
        }
        if (gatewayAnimations.isNotEmpty()) {
            for (pair in gatewayAnimations) {
                val gatewayAnimId = when (pair.second) {
                    true -> R.animator.appear
                    else -> R.animator.disappear
                }
                (AnimatorInflater.loadAnimator(context, gatewayAnimId) as AnimatorSet).apply {
                    setTarget(pair.first)
                    start()
                    doOnEnd {
                        moveCameraToPlayer(playerInTurn)
                        if (!pair.second)
                            setViewVisibility(pair.first, pair.second)
                    }
                }
            }
        }
    }

    private fun teleport(playerId: Int, from: Int, to: Int) {
        if (stepInRoom(getPlayerById(playerId).pos) == from) {
            stepPlayer(playerId, Position(roomList[to].top, roomList[to].left))
            emptySelectionList()
        }
    }

    private fun setViewVisibility(imageView: ImageView, visible: Boolean) {
        if (visible)
            imageView.visibility = View.VISIBLE
        else
            imageView.visibility = View.GONE
    }

    private fun getPlayerById(id: Int): Player {
        for (player in playerList) {
            if (player.id == id)
                return player
        }
        return playerList[0]
    }

    private fun getPairById(id: Int): Pair<Player, ImageView> {
        for (pair in playerImagePairs) {
            if (pair.first.id == id)
                return pair
        }
        return playerImagePairs[0]
    }

    private fun stepInRoom(pos: Position): Int {
        for (room: Room in roomList) {
            if (pos.row >= room.top && pos.row <= room.bottom && pos.col >= room.left && pos.col <= room.right)
                return room.id
        }
        return -1
    }

    private fun isFieldOccupied(pos: Position): Boolean {
        for (player in playerList) {
            if (player.pos == pos)
                return true
        }
        return false
    }

    private fun isDoor(pos: Position): Boolean {
        for (door in doorList) {
            if (door.position == pos)
                return true
        }
        return false
    }

    private fun dijkstra(current: Position): HashMap<Position, Int> {
        val distances = HashMap<Position, Int>()
        val unvisited = HashSet<Position>()

        for (field in mapGraph.adjacencyMap.keys) {
            unvisited.add(field)
            if (field == current)
                distances[field] = 0
            else
                distances[field] = Int.MAX_VALUE
        }

        while (unvisited.isNotEmpty()) {
            var field: Position = unvisited.elementAt(0)
            for (pair in distances) {
                if (unvisited.contains(pair.key) && pair.value < distances[field]!!)
                    field = pair.key
            }

            for (neighbour in mapGraph.adjacencyMap[field]!!) {
                if (unvisited.contains(neighbour)) {
                    if (stepInRoom(field) == -1 || !isDoor(neighbour))
                        distances[neighbour] = min(distances[neighbour]!!, distances[field]!! + 1)
                }
            }
            unvisited.remove(field)
        }

        return distances
    }

    private fun mergeDistances(map1: HashMap<Position, Int>, map2: HashMap<Position, Int>? = null): HashMap<Position, Int> {
        val intersection: HashMap<Position, Int> = HashMap()
        for (pos in map1.keys) {
            intersection[pos] = map1[pos]!!
        }
        if (map2 != null) {
            for (pos in map2.keys) {
                if (intersection.containsKey(pos))
                    intersection[pos] = min(map1[pos]!!, map2[pos]!!)
                else
                    intersection[pos] = map2[pos]!!
            }
        }
        return intersection
    }

    fun showOptions(playerId: Int) {
        if (isGameRunning) {
            if (playerId == player.id && playerId == playerInTurn) {
                if (!userHasToStep) {
                    val roomId = stepInRoom(player.pos)
                    val snackbar = Snackbar.make(mapRoot.mapLayout, "Lépj!", Snackbar.LENGTH_LONG)
                        .setAction("Kockadobás") {
                            rollWithDice(playerId)
                        }
                    if (roomId != -1) {
                        snackbar.setAction("Gyanúsítás") {
                            incrimination(playerId, roomId)
                        }
                    }
                    snackbar.show()
                } else
                    Snackbar.make(mapRoot.mapLayout, "Muszáj lépned!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun rollWithDice(playerId: Int) {
        if (player.id != playerId) {
            val sum = Random.nextInt(2, 13)
            val hogwartsDice = Random.nextInt(1, 7)
            var cardType: CardType? = null
            var house: HogwartsHouse? = null
            when (hogwartsDice) {
                1 -> cardType = CardType.HELPER
                2 -> house = HogwartsHouse.GRYFFINDOR
                3 -> house = HogwartsHouse.SLYTHERIN
                4 -> house = HogwartsHouse.HUFFLEPUFF
                5 -> house = HogwartsHouse.RAVENCLAW
                6 -> cardType = CardType.DARK
            }
            cardType?.let {
                getCard(playerId, cardType)
            }
            onDiceRoll(playerId, sum, house)
        } else
            DiceRollerDialog(this, playerId).show(fm, "DIALOG_DICE")
    }

    override fun onDiceRoll(playerId: Int, sum: Int, house: HogwartsHouse?) {
        house?.let {
            setState(playerId, it)
        }
        calculateMovingOptions(playerId, sum)
        if (playerId == player.id)
            userHasToStep = true
    }

    private fun calculateMovingOptions(playerId: Int, stepCount: Int) {
        emptySelectionList()

        var limit = stepCount

        var distances: HashMap<Position, Int>? = null
        if (stepInRoom(getPlayerById(playerId).pos) == -1)
            distances = dijkstra(getPlayerById(playerId).pos)
        else {
            limit--
            val roomId = stepInRoom(getPlayerById(playerId).pos)
            for (door in doorList) {
                if (door.room.id == roomId && door.state == DoorState.OPENED) {
                    distances = mergeDistances(dijkstra(door.position), distances)
                }
            }
        }

        if (playerId == player.id) {
            if (!distances.isNullOrEmpty()) {
                for (x in 0..COLS) {
                    for (y in 0..ROWS) {
                        val current = Position(y, x)
                        if (stepInRoom(current) == -1 && !isFieldOccupied(current) && distances[current]!! <= limit) {
                            drawSelection(R.drawable.field_selection, y, x, playerId)
                        }
                    }
                }
            }

            if (stepInRoom(getPlayerById(playerId).pos) == -1) {
                for (door in doorList) {
                    if (distances!![door.position]!! <= limit - 1 && door.state == DoorState.OPENED) {
                        drawSelection(door.room.selection, door.room.top, door.room.left, playerId)
                    }
                }
            }
        }
        else {
            if (distances != null) {
                val validKeys: MutableList<Position> = ArrayList()
                for (pos in distances.keys) {
                    if (!(isFieldOccupied(pos) || distances[pos]!! > stepCount || (stepInRoom(getPlayerById(playerId).pos) != -1 && stepInRoom(pos) != -1)))
                        validKeys.add(pos)
                }
                var stepped = false
                for (pos in validKeys) {
                    if (stepInRoom(pos) != -1) {
                        stepPlayer(playerId, pos)
                        stepped = true
                        break
                    }
                }
                if (!stepped)
                    stepPlayer(playerId, validKeys[Random.nextInt(0, validKeys.size)])
            }
            else {
                moveToNextPlayer()
            }
        }
    }

    private fun drawSelection(@DrawableRes selRes: Int, row: Int, col: Int, playerId: Int) {
        val targetPosition = Position(row, col)

        val selection = ImageView(mapRoot.mapLayout.context)
        selectionList.add(selection)
        selection.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        selection.setImageResource(selRes)
        selection.visibility = ImageView.VISIBLE
        setLayoutConstraintStart(selection, cols[col])
        setLayoutConstraintTop(selection, rows[row])
        selection.setOnClickListener {
            stepPlayer(playerId, targetPosition)
            emptySelectionList()
        }
        mapRoot.mapLayout.addView(selection)
    }

    private fun stepPlayer(playerId: Int, targetPosition: Position) {
        while (stepInRoom(targetPosition) != -1 && isFieldOccupied(targetPosition))
            targetPosition.col++
        getPlayerById(playerId).pos = targetPosition
        moveCameraToPlayer(playerId)

        var starStep = false
        for (star in starList) {
            if (getPlayerById(playerId).pos == star) {
                getCard(playerId, CardType.HELPER)
                starStep = true
            }
        }

        val pair = getPairById(playerId)
        setLayoutConstraintStart(pair.second, cols[getPlayerById(playerId).pos.col])
        setLayoutConstraintTop(pair.second, rows[getPlayerById(playerId).pos.row])

        when {
            stepInRoom(getPlayerById(playerId).pos) != -1 -> {
                incrimination(playerId, stepInRoom(getPlayerById(playerId).pos))
                if (playerId == player.id) {
                    userHasToIncriminate = true
                    userHasToStep = false
                }
            }
            playerId != player.id -> {
                moveToNextPlayer()
            }
            starStep -> userFinishedHisTurn = true
            else -> {
                userFinishedHisTurn = true
                moveToNextPlayer()
            }
        }
    }

    private fun emptySelectionList() {
        for (sel in selectionList)
            mapRoot.mapLayout.removeView(sel)
        selectionList = ArrayList()
    }

    private fun incrimination(playerId: Int, roomId: Int) {
        if (playerId == player.id) {
            if (roomId != 4)
                IncriminationDialog(playerId, roomId, this).show(fm, "DIALOG_INCRIMINATION")
            else
                AccusationDialog(playerId, this).show(fm, "DIALOG_ACCUSATION")
        }
        else {
            val room = roomList[roomId].name
            val tool = context.resources.getStringArray(R.array.tools)[Random.nextInt(0, 6)]
            val suspect = context.resources.getStringArray(R.array.suspects)[Random.nextInt(0, 6)]

            if (room != "Dumbledore irodája")
                getIncrimination(Suspect(playerId, room, tool, suspect))
            else
                onAccusationDismiss(Suspect(playerId, room, tool, suspect))
        }
    }

    override fun getIncrimination(suspect: Suspect) {
        if (suspect.playerId != player.id) {
            val title = "${getPlayerById(suspect.playerId).card.name} gyanúsít"
            val message = "Ebben a helyiségben: ${suspect.room}\nEzzel az eszközzel: ${suspect.tool}\nGyanúsított: ${suspect.suspect}"
            InformationDialog(suspect, title, message, this).show(fm, "DIALOG_INFORMATION")
        }
        else {
            var someoneShowedSomething = false
            var playerIdx = playerList.indexOf(getPlayerById(suspect.playerId))
            for (i in 0 until playerList.size - 1) {
                playerIdx--
                if (playerIdx < 0)
                    playerIdx = playerList.lastIndex
                val cards = revealMysteryCards(playerIdx, suspect.room, suspect.tool, suspect.suspect)
                if (cards != null) {
                    val revealedCard = cards[Random.nextInt(0, cards.size)]
                    CardRevealDialog(revealedCard, playerList[playerIdx].card.name, this).show(fm, "DIALOG_CARD_REVEAL")
                    someoneShowedSomething = true
                    letOtherPlayersKnow(suspect, playerList[playerIdx].id, revealedCard.name)
                }
                if (someoneShowedSomething)
                    break
            }
            if (!someoneShowedSomething) {
                nothingHasBeenShowed(suspect)
                letOtherPlayersKnow(suspect)
            }

            userFinishedHisTurn = true
        }
    }

    override fun onIncriminationSkip() {
        if (userHasToIncriminate) {
            Snackbar.make(mapRoot.mapLayout, "Muszáj gyanúsítanod!", Snackbar.LENGTH_LONG).show()
            incrimination(player.id, stepInRoom(player.pos))
        }
        else {
            Snackbar.make(mapRoot.mapLayout, "Lépj!", Snackbar.LENGTH_SHORT).setAction("Kockadobás"){
                rollWithDice(player.id)
            }.show()
        }
    }

    private fun revealMysteryCards(playerIdx: Int, room: String, tool: String, suspect: String): List<MysteryCard>? {
        val cardList: MutableList<MysteryCard> = ArrayList()
        for (card in playerList[playerIdx].mysteryCards) {
            if (card.name == room || card.name == tool || card.name == suspect)
                cardList.add(card)
        }

        if (cardList.isNotEmpty())
            return cardList
        return null
    }

    private fun letOtherPlayersKnow(suspect: Suspect, playerWhoShowed: Int? = null, revealedMysteryCardName: String? = null) {
        if (playerWhoShowed != null && revealedMysteryCardName != null) {
            for (p in playerList) {
                if (p.id != playerWhoShowed && p.id != player.id) {
                    if (p.id == suspect.playerId)
                        p.getConclusion(revealedMysteryCardName, playerWhoShowed)
                    else
                        p.getSuspicion(suspect, playerWhoShowed)
                }
            }
        }
        else {
            for (p in playerList) {
                if (p.id != player.id)
                    p.getSuspicion(suspect)
            }
        }
    }

    override fun onSuspectInformationDismiss(suspect: Suspect) {
        var someoneShowedSomething = false
        var playerIdx = playerList.indexOf(getPlayerById(suspect.playerId))
        for (i in 0 until playerList.size - 1) {
            playerIdx--
            if (playerIdx < 0)
                playerIdx = playerList.lastIndex
            if (playerIdx == playerList.indexOf(player)) {
                val cards = revealMysteryCards(playerIdx, suspect.room, suspect.tool, suspect.suspect)
                if (cards != null) {
                    ShowCardDialog(suspect, getPlayerById(suspect.playerId).card.name, cards, this).show(fm, "DIALOG_SHOW_CARD")
                    someoneShowedSomething = true
                }
            }
            else {
                val cards = revealMysteryCards(playerIdx, suspect.room, suspect.tool, suspect.suspect)
                if (cards != null) {
                    val revealedCard = cards[Random.nextInt(0, cards.size)]

                    val title = "Kártyafelfedés történt"
                    val message = "${playerList[playerIdx].card.name} mutatott valamit neki: ${getPlayerById(suspect.playerId).card.name}\nGyanúsítás paraméterei:\n\tHelyiség: ${suspect.room}\n\t" +
                            "Eszköz: ${suspect.tool}\n\t" +
                            "Gyanúsított: ${suspect.suspect}"
                    InformationDialog(null, title, message, this).show(fm, "DIALOG_SIMPLE_INFORMATION")
                    someoneShowedSomething = true
                    letOtherPlayersKnow(suspect, playerList[playerIdx].id, revealedCard.name)
                }
            }
            if (someoneShowedSomething)
                break
        }
        if (!someoneShowedSomething) {
            nothingHasBeenShowed(suspect)
            letOtherPlayersKnow(suspect)
        }
    }

    override fun onSimpleInformationDismiss() {
        moveToNextPlayer()
    }

    override fun onCardRevealDismiss() {
        moveToNextPlayer()
    }

    override fun onCardShowDismiss(suspect: Suspect, card: MysteryCard) {
        letOtherPlayersKnow(suspect, player.id, card.name)
        moveToNextPlayer()
    }

    override fun onHelperCardDismiss() {
        if (userFinishedHisTurn)
            moveToNextPlayer()
    }

    override fun onAccusationDismiss(suspect: Suspect?) {
        if (suspect == null) {
            Snackbar.make(mapRoot.mapLayout, "Add le a gyanúdat!", Snackbar.LENGTH_LONG).show()
            AccusationDialog(playerInTurn, this).show(fm, "DIALOG_ACCUSATION")
            return
        }
        var correct = true
        for (card in gameSolution) {
            if (card.name != suspect.room && card.name != suspect.tool && card.name != suspect.suspect)
                correct = false
        }
        val titleId = if (correct) R.string.correct_accusation else R.string.incorrect_accusation
        EndOfGameDialog(this, getPlayerById(suspect.playerId).card.name, titleId, correct).show(fm, "DIALOG_END_OF_GAME")
        isGameRunning = false
    }

    override fun onEndOfGameDismiss() {
        activityListener.exitToMenu()
    }

    private fun nothingHasBeenShowed(suspect: Suspect) {
        val title = "Senki sem tudott mutatni..."
        val message = "Gyanúsítás paraméterei:\n\tHelyiség: ${suspect.room}\n\tEszköz: ${suspect.tool}\n\tGyanúsított: ${suspect.suspect}"
        InformationDialog(null, title, message, this).show(fm, "DIALOG_SIMPLE_INFORMATION")
    }

    private fun getRandomCardId(type: CardType): Int? {
        return when (type) {
            CardType.HELPER -> {
                if (helperCards.size > 0)
                    Random.nextInt(0, helperCards.size)
                else null
            }
            else -> {
                if (darkCards.size > 0)
                    Random.nextInt(0, darkCards.size)
                else
                    null
            }
        }
    }

    override fun getCard(playerId: Int, type: CardType?) {
        if (type == null)
            return
        val randomCard = getRandomCardId(type)
        when (type) {
            CardType.HELPER -> {
                if (randomCard != null) {
                    val card = helperCards[randomCard]
                    if (getPlayerById(playerId).helperCards.isNullOrEmpty()) {
                        getPlayerById(playerId).helperCards = ArrayList()
                    }
                    getPlayerById(playerId).helperCards!!.add(card)

                    if (card.count > 1)
                        helperCards[randomCard].count--
                    else
                        helperCards.remove(card)

                    if (playerId == player.id)
                        HelperCardDialog(card.imageRes, this).show(fm, "DIALOG_HELPER")
                }
            }
            else -> {
                if (randomCard != null) {
                    val card = darkCards[randomCard]
                    darkCards.remove(card)
                    if (playerId == player.id)
                        DarkCardDialog(player, card, this).show(fm, "DIALOG_DARK")
                    else {
                        val tools: ArrayList<String> = ArrayList()
                        val spells: ArrayList<String> = ArrayList()
                        val allys: ArrayList<String> = ArrayList()

                        getHelperObjects(getPlayerById(playerId), card, tools, spells, allys)

                        if (tools.size == 1 && spells.size == 1 && allys.size == 1)
                            getLoss(playerId, card)
                    }
                }
            }
        }
    }

    override fun getLoss(playerId: Int, card: DarkCard?) {
        if (card == null) {
            if (playerId == player.id)
                RescuedFromDarkCardDialog().show(fm, "DIALOG_RESCUED")
        } else {
            when (card.lossType) {
                LossType.HP -> {
                    getPlayerById(playerId).hp -= card.hpLoss
                    if (playerId == player.id)
                        HpLossDialog(card.hpLoss, player.hp).show(fm, "DIALOG_HP_LOSS")
                }
                else -> {
                    if (getPlayerById(playerId).helperCards != null) {
                        val properHelperCards: ArrayList<HelperCard> = ArrayList()
                        for (helperCard in getPlayerById(playerId).helperCards!!) {
                            if (helperCard.type.compareTo(card.lossType))
                                properHelperCards.add(helperCard)
                        }

                        if (properHelperCards.isNotEmpty()) {
                            if (playerId == player.id)
                                CardLossDialog(
                                    playerId,
                                    properHelperCards,
                                    card.lossType,
                                    this
                                ).show(
                                    fm,
                                    "DIALOG_CARD_LOSS"
                                )
                            else
                                throwCard(
                                    playerId,
                                    properHelperCards[Random.nextInt(0, properHelperCards.size)]
                                )
                        }
                    }
                }
            }
        }
    }

    override fun throwCard(playerId: Int, card: HelperCard) {
        getPlayerById(playerId).helperCards!!.remove(card)
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

    override fun onIdle(engine: ZoomEngine) {

    }

    override fun onUpdate(engine: ZoomEngine, matrix: Matrix) {

    }
}

interface DialogDismiss {
    fun onSuspectInformationDismiss(suspect: Suspect)
    fun onSimpleInformationDismiss()
    fun onCardRevealDismiss()
    fun onCardShowDismiss(suspect: Suspect, card: MysteryCard)
    fun onHelperCardDismiss()
    fun onAccusationDismiss(suspect: Suspect?)
    fun onEndOfGameDismiss()
}

interface MapActivityListener {
    fun exitToMenu()
}