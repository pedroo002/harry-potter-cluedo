package neptun.jxy1vz.cluedo.ui.map

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.*
import neptun.jxy1vz.cluedo.model.helper.*
import neptun.jxy1vz.cluedo.ui.dialog.RescuedFromDarkCardDialog
import neptun.jxy1vz.cluedo.ui.dialog.card_dialog.dark_mark.DarkCardDialog
import neptun.jxy1vz.cluedo.ui.dialog.card_dialog.helper.HelperCardDialog
import neptun.jxy1vz.cluedo.ui.dialog.dice.DiceRollerDialog
import neptun.jxy1vz.cluedo.ui.dialog.dice.DiceRollerViewModel.CardType
import neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.card_loss.CardLossDialog
import neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.hp_loss.HpLossDialog
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set
import kotlin.math.min
import kotlin.random.Random

class MapViewModel(
    playerId: Int,
    private var playerImagePairs: List<Pair<Player, ImageView>>,
    private var mapLayout: ConstraintLayout,
    private val fm: FragmentManager
) : BaseObservable(),
    DiceRollerDialog.DiceResultInterface, DarkCardDialog.DarkCardDialogListener,
    CardLossDialog.CardLossDialogListener {

    private var mapGraph: Graph<Position>
    private var selectionList: ArrayList<ImageView> = ArrayList()

    private var slytherinState = 0
    private var ravenclawState = 0
    private var gryffindorState = 0
    private var hufflepuffState = 0

    enum class HogwartsHouse {
        SLYTHERIN,
        RAVENCLAW,
        GRYFFINDOR,
        HUFFLEPUFF
    }

    private fun setState(playerId: Int, house: HogwartsHouse) {
        when (house) {
            HogwartsHouse.SLYTHERIN -> {
                setChanges(playerId, slytherinStates, slytherinState, HogwartsHouse.SLYTHERIN)
                slytherinState++
                if (slytherinState == 16)
                    slytherinState = 0
            }
            HogwartsHouse.RAVENCLAW -> {
                setChanges(playerId, ravencalwStates, ravenclawState, HogwartsHouse.RAVENCLAW)
                ravenclawState++
                if (ravenclawState == 16)
                    ravenclawState = 0
            }
            HogwartsHouse.GRYFFINDOR -> {
                setChanges(playerId, gryffindorStates, gryffindorState, HogwartsHouse.GRYFFINDOR)
                gryffindorState++
                if (gryffindorState == 16)
                    gryffindorState = 0
            }
            HogwartsHouse.HUFFLEPUFF -> {
                setChanges(playerId, hufflepuffStates, hufflepuffState, HogwartsHouse.HUFFLEPUFF)
                hufflepuffState++
                if (hufflepuffState == 16)
                    hufflepuffState = 0
            }
        }
    }

    private fun setChanges(playerId: Int, stateList: List<State>, state: Int, house: HogwartsHouse) {
        for (s in stateList) {
            if (s.serialNum == state) {
                doorList[s.doorId].state = s.doorState
                when (s.doorId) {
                    0 -> { setViewVisibility(mapLayout.ivDoor0, s.doorState.boolean()) }
                    2 -> { setViewVisibility(mapLayout.ivDoor2, s.doorState.boolean()) }
                    4 -> { setViewVisibility(mapLayout.ivDoor4, s.doorState.boolean()) }
                    6 -> { setViewVisibility(mapLayout.ivDoor6, s.doorState.boolean()) }
                    7 -> { setViewVisibility(mapLayout.ivDoor7, s.doorState.boolean()) }
                    12 -> { setViewVisibility(mapLayout.ivDoor12, s.doorState.boolean()) }
                    13 -> { setViewVisibility(mapLayout.ivDoor13, s.doorState.boolean()) }
                    15 -> { setViewVisibility(mapLayout.ivDoor15, s.doorState.boolean()) }
                    17 -> { setViewVisibility(mapLayout.ivDoor17, s.doorState.boolean()) }
                    19 -> { setViewVisibility(mapLayout.ivDoor19, s.doorState.boolean()) }
                    20 -> { setViewVisibility(mapLayout.ivDoor20, s.doorState.boolean()) }
                    21 -> { setViewVisibility(mapLayout.ivDoor21, s.doorState.boolean()) }
                }
                when (house) {
                    HogwartsHouse.SLYTHERIN -> {
                        setViewVisibility(mapLayout.ivDarkMarkSlytherin, s.darkMark)
                        for (p in passageWayListSlytherin) {
                            setViewVisibility(mapLayout.findViewById(p), passageWayVisibilitiesSlytherin[slytherinState][passageWayListSlytherin.indexOf(p)])
                        }
                    }
                    HogwartsHouse.RAVENCLAW -> {
                        setViewVisibility(mapLayout.ivDarkMarkRavenclaw, s.darkMark)
                        for (p in passageWayListRavenclaw) {
                            setViewVisibility(mapLayout.findViewById(p), passageWayVisibilitiesRavenclaw[ravenclawState][passageWayListRavenclaw.indexOf(p)])
                        }
                    }
                    HogwartsHouse.GRYFFINDOR -> {
                        setViewVisibility(mapLayout.ivDarkMarkGryffindor, s.darkMark)
                        for (p in passageWayListGryffindor) {
                            setViewVisibility(mapLayout.findViewById(p), passageWayVisibilitiesGryffindor[gryffindorState][passageWayListGryffindor.indexOf(p)])
                        }
                    }
                    HogwartsHouse.HUFFLEPUFF -> {
                        setViewVisibility(mapLayout.ivDarkMarkHufflepuff, s.darkMark)
                        for (p in passageWayListHufflepuff) {
                            setViewVisibility(mapLayout.findViewById(p), passageWayVisibilitiesHufflepuff[hufflepuffState][passageWayListHufflepuff.indexOf(p)])
                        }
                    }
                }
                if (s.darkMark)
                    showCard(playerId, CardType.DARK)
            }
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

    private var player = getPlayerById(playerId)

    companion object {
        const val ROWS = 24
        const val COLS = 24
    }

    init {
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
        var distances = HashMap<Position, Int>()
        var unvisited = HashSet<Position>()

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

    private fun mergeDistances(
        map1: HashMap<Position, Int>,
        map2: HashMap<Position, Int>? = null
    ): HashMap<Position, Int> {
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

    fun showDialog(playerId: Int) {
        if (player.id != playerId)
            return
        DiceRollerDialog(this, playerId).show(fm, "DIALOG_DICE")
    }

    private fun showMovingOptions(playerId: Int, stepCount: Int) {
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

        if (!distances.isNullOrEmpty()) {
            for (x in 0..COLS) {
                for (y in 0..ROWS) {
                    val current = Position(y, x)
                    println(current)
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

    private fun drawSelection(@DrawableRes selRes: Int, row: Int, col: Int, playerId: Int) {
        val targetPosition = Position(row, col)

        val selection = ImageView(mapLayout.context)
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
            while (stepInRoom(targetPosition) != -1 && isFieldOccupied(targetPosition))
                targetPosition.col++
            getPlayerById(playerId).pos = targetPosition

            for (star in starList) {
                if (getPlayerById(playerId).pos == star) {
                    if (helperCards.size > 0)
                        showCard(playerId, CardType.HELPER)
                }
            }

            val pair = getPairById(playerId)
            setLayoutConstraintStart(pair.second, cols[getPlayerById(playerId).pos.col])
            setLayoutConstraintTop(pair.second, rows[getPlayerById(playerId).pos.row])

            emptySelectionList()
        }
        mapLayout.addView(selection)
    }

    private fun emptySelectionList() {
        for (sel in selectionList)
            mapLayout.removeView(sel)
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

    private fun getRandomCardId(type: CardType): Int {
        return when (type) {
            CardType.HELPER -> Random.nextInt(0, helperCards.size)
            else -> Random.nextInt(0, darkCards.size)
        }
    }

    override fun onDiceRoll(playerId: Int, sum: Int, house: HogwartsHouse?) {
        house?.let {
            setState(playerId, it)
        }
        showMovingOptions(playerId, sum)
    }

    override fun showCard(playerId: Int, type: CardType?) {
        if (type == null)
            return
        val randomCard: Int = getRandomCardId(type)
        when (type) {
            CardType.HELPER -> {
                if (helperCards.size > 0) {
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
                        HelperCardDialog(card.imageRes).show(fm, "DIALOG_HELPER")
                }
            }
            else -> {
                if (darkCards.size > 0) {
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
                                throwCard(playerId, properHelperCards[Random.nextInt(0, properHelperCards.size)])
                        }
                    }
                }
            }
        }
    }

    override fun throwCard(playerId: Int, card: HelperCard) {
        getPlayerById(playerId).helperCards!!.remove(card)
    }
}