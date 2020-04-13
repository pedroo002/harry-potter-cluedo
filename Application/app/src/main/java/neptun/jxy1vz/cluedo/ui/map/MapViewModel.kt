package neptun.jxy1vz.cluedo.ui.map

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.*
import neptun.jxy1vz.cluedo.model.helper.*
import neptun.jxy1vz.cluedo.ui.dialog.RescuedFromDarkCardDialog
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
    private val context: Context,
    playerId: Int,
    private var playerImagePairs: List<Pair<Player, ImageView>>,
    private var mapLayout: ConstraintLayout,
    private val fm: FragmentManager
) : BaseObservable(),
    DiceRollerDialog.DiceResultInterface, DarkCardDialog.DarkCardDialogListener,
    CardLossDialog.CardLossDialogListener, IncriminationDialog.MapInterface,
    DialogDismiss {

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

    //TODO: mentő kártya vagy sötét lap feltűnésénél megvárni a legutolsó dismisst, mielőtt új játékos következne
    //TODO: többi játékos tájékoztatása valamilyen módon arról, hogy valaki mutatott valakinek valamit
    //TODO: a többi játékos lépéseinek nyomon követhetősége: mit dobott, mit forgatott, hova lépett, mit gyanúsít, stb.
    private fun letPlayerTurn() {
        if (isGameRunning) {
            if (playerInTurn != player.id)
                rollWithDice(playerInTurn)
            else {
                Snackbar.make(mapLayout, R.string.your_turn, Snackbar.LENGTH_LONG).show()
                userFinishedHisTurn = false
            }
        }
    }

    init {
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

    private fun setState(playerId: Int, house: HogwartsHouse) {
        when (house) {
            HogwartsHouse.SLYTHERIN -> {
                setChanges(playerId, slytherinStates, passageWayListSlytherin, passageWayVisibilitiesSlytherin, slytherinState, HogwartsHouse.SLYTHERIN)
                slytherinState++
                if (slytherinState == 16)
                    slytherinState = 0
            }
            HogwartsHouse.RAVENCLAW -> {
                setChanges(playerId, ravencalwStates, passageWayListRavenclaw, passageWayVisibilitiesRavenclaw, ravenclawState, HogwartsHouse.RAVENCLAW)
                ravenclawState++
                if (ravenclawState == 16)
                    ravenclawState = 0
            }
            HogwartsHouse.GRYFFINDOR -> {
                setChanges(playerId, gryffindorStates, passageWayListGryffindor, passageWayVisibilitiesGryffindor, gryffindorState, HogwartsHouse.GRYFFINDOR)
                gryffindorState++
                if (gryffindorState == 16)
                    gryffindorState = 0
            }
            HogwartsHouse.HUFFLEPUFF -> {
                setChanges(playerId, hufflepuffStates, passageWayListHufflepuff, passageWayVisibilitiesHufflepuff, hufflepuffState, HogwartsHouse.HUFFLEPUFF)
                hufflepuffState++
                if (hufflepuffState == 16)
                    hufflepuffState = 0
            }
        }
    }

    private fun setChanges(playerId: Int, stateList: List<State>, gateways: List<Int>, visibilities: List<List<Boolean>>, state: Int, house: HogwartsHouse) {
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
            mapLayout.findViewById<ImageView>(gateways[visibleGatewaySerialNumbers[i]]).setOnClickListener {
                if (it.visibility == View.VISIBLE)
                    teleport(playerInTurn, stateList[state * 3 + gatewayNumbers[i]].roomId, stateList[state * 3 + gatewayNumbers[i]].passageWay!!)
            }
        }

        for (p in gateways) {
            val visibility = visibilities[state][gateways.indexOf(p)]
            setViewVisibility(mapLayout.findViewById(p), visibility)
        }

        for (s in stateList) {
            if (s.serialNum == state) {
                doorList[s.doorId].state = s.doorState
                when (s.doorId) {
                    0 -> setViewVisibility(mapLayout.ivDoor0, s.doorState.boolean())
                    2 -> setViewVisibility(mapLayout.ivDoor2, s.doorState.boolean())
                    4 -> setViewVisibility(mapLayout.ivDoor4, s.doorState.boolean())
                    6 -> setViewVisibility(mapLayout.ivDoor6, s.doorState.boolean())
                    7 -> setViewVisibility(mapLayout.ivDoor7, s.doorState.boolean())
                    12 -> setViewVisibility(mapLayout.ivDoor12, s.doorState.boolean())
                    13 -> setViewVisibility(mapLayout.ivDoor13, s.doorState.boolean())
                    15 -> setViewVisibility(mapLayout.ivDoor15, s.doorState.boolean())
                    17 -> setViewVisibility(mapLayout.ivDoor17, s.doorState.boolean())
                    19 -> setViewVisibility(mapLayout.ivDoor19, s.doorState.boolean())
                    20 -> setViewVisibility(mapLayout.ivDoor20, s.doorState.boolean())
                    21 -> setViewVisibility(mapLayout.ivDoor21, s.doorState.boolean())
                }
                when (house) {
                    HogwartsHouse.SLYTHERIN -> {
                        setViewVisibility(mapLayout.ivDarkMarkSlytherin, s.darkMark)
                    }
                    HogwartsHouse.RAVENCLAW -> {
                        setViewVisibility(mapLayout.ivDarkMarkRavenclaw, s.darkMark)
                    }
                    HogwartsHouse.GRYFFINDOR -> {
                        setViewVisibility(mapLayout.ivDarkMarkGryffindor, s.darkMark)
                    }
                    HogwartsHouse.HUFFLEPUFF -> {
                        setViewVisibility(mapLayout.ivDarkMarkHufflepuff, s.darkMark)
                    }
                }
                if (s.darkMark)
                    getCard(playerId, CardType.DARK)
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

    fun rollWithDice(playerId: Int) {
        if (playerId == playerInTurn) {
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
    }

    override fun onDiceRoll(playerId: Int, sum: Int, house: HogwartsHouse?) {
        house?.let {
            setState(playerId, it)
        }
        calculateMovingOptions(playerId, sum)
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
                    if (!(pos == getPlayerById(playerId).pos || distances[pos]!! > stepCount || (stepInRoom(getPlayerById(playerId).pos) != -1 && stepInRoom(pos) != -1)))
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
            stepPlayer(playerId, targetPosition)
            emptySelectionList()
        }
        mapLayout.addView(selection)
    }

    private fun stepPlayer(playerId: Int, targetPosition: Position) {
        while (stepInRoom(targetPosition) != -1 && isFieldOccupied(targetPosition))
            targetPosition.col++
        getPlayerById(playerId).pos = targetPosition

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
            stepInRoom(getPlayerById(playerId).pos) != -1 -> incrimination(playerId, stepInRoom(getPlayerById(playerId).pos))
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
            mapLayout.removeView(sel)
        selectionList = ArrayList()
    }

    private fun incrimination(playerId: Int, roomId: Int) {
        if (playerId == player.id) {
            val title = when (roomId) {
                4 -> R.string.accusation
                else -> R.string.incrimination
            }
            IncriminationDialog(playerId, roomId, this, title).show(fm, "DIALOG_INCRIMINATION")
        }
        else {
            val room = roomList[roomId].name
            val tool = context.resources.getStringArray(R.array.tools)[Random.nextInt(0, 6)]
            val suspect = context.resources.getStringArray(R.array.suspects)[Random.nextInt(0, 6)]

            getIncrimination(Suspect(playerId, room, tool, suspect), roomId == 4)
        }
    }

    override fun getIncrimination(suspect: Suspect, solution: Boolean) {
        if (solution) {
            var correct = true
            for (card in gameSolution) {
                if (card.name != suspect.room && card.name != suspect.tool && card.name != suspect.suspect)
                    correct = false
            }
            val titleId = if (correct) R.string.correct_accusation else R.string.incorrect_accusation
            EndOfGameDialog(getPlayerById(suspect.playerId).card.name, titleId, correct).show(fm, "DIALOG_END_OF_GAME")
            isGameRunning = false
            return
        }
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
                    CardRevealDialog(cards[Random.nextInt(0, cards.size)], playerList[playerIdx].card.name, this).show(fm, "DIALOG_CARD_REVEAL")
                    someoneShowedSomething = true
                }
                if (someoneShowedSomething)
                    break
            }
            if (!someoneShowedSomething) {
                val title = "Senki sem tudott mutatni..."
                val message = "Gyanúsítás paraméterei:\n\tHelyiség: ${suspect.room}\n\tEszköz: ${suspect.tool}\n\tGyanúsított: ${suspect.suspect}"
                InformationDialog(null, title, message, this).show(fm, "DIALOG_SIMPLE_INFORMATION")
            }

            userFinishedHisTurn = true
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

    private fun letOtherPlayersKnow() {

    }

    override fun onInformationDismiss(suspect: Suspect) {
        var someoneShowedSomething = false
        var playerIdx = playerList.indexOf(getPlayerById(suspect.playerId))
        for (i in 0 until playerList.size - 1) {
            playerIdx--
            if (playerIdx < 0)
                playerIdx = playerList.lastIndex
            if (playerIdx == playerList.indexOf(player)) {
                val cards = revealMysteryCards(playerIdx, suspect.room, suspect.tool, suspect.suspect)
                if (cards != null) {
                    ShowCardDialog(getPlayerById(suspect.playerId).card.name, cards, this).show(fm, "DIALOG_SHOW_CARD")
                    someoneShowedSomething = true
                }
            }
            else {
                val cards = revealMysteryCards(playerIdx, suspect.room, suspect.tool, suspect.suspect)
                if (cards != null) {
                    val title = "Kártyafelfedés történt"
                    val message = "${playerList[playerIdx].card.name} mutatott valamit neki: ${getPlayerById(suspect.playerId).card.name}\nGyanúsítás paraméterei:\n\tHelyiség: ${suspect.room}\n\t" +
                            "Eszköz: ${suspect.tool}\n\t" +
                            "Gyanúsított: ${suspect.suspect}"
                    InformationDialog(null, title, message, this).show(fm, "DIALOG_SIMPLE_INFORMATION")
                    someoneShowedSomething = true
                }
            }
            if (someoneShowedSomething)
                break
        }
        if (!someoneShowedSomething) {
            val title = "Senki sem tudott mutatni..."
            val message = "Gyanúsítás paraméterei:\n\tHelyiség: ${suspect.room}\n\tEszköz: ${suspect.tool}\n\tGyanúsított: ${suspect.suspect}"
            InformationDialog(null, title, message, this).show(fm, "DIALOG_SIMPLE_INFORMATION")
        }
    }

    override fun onSimpleInformationDismiss() {
        moveToNextPlayer()
    }

    override fun onCardRevealDismiss() {
        moveToNextPlayer()
    }

    override fun onCardShowDismiss(card: MysteryCard) {
        moveToNextPlayer()
    }

    override fun onHelperCardDismiss() {
        if (userFinishedHisTurn)
            moveToNextPlayer()
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
}

interface DialogDismiss {
    fun onInformationDismiss(suspect: Suspect)
    fun onSimpleInformationDismiss()
    fun onCardRevealDismiss()
    fun onCardShowDismiss(card: MysteryCard)
    fun onHelperCardDismiss()
}