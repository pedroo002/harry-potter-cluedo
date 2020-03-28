package neptun.jxy1vz.cluedo.ui.map

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.elementAt
import kotlin.collections.isNotEmpty
import kotlin.collections.iterator
import kotlin.collections.listOf
import kotlin.collections.set
import kotlin.math.min
import kotlin.random.Random

class MapViewModel(players: List<ImageView>, layout: ConstraintLayout) : BaseObservable() {
    private var mapLayout = layout
    private var mapGraph: Graph<Position>
    private lateinit var selectionList: ArrayList<ImageView>

    private val playerList = listOf(
        Player(0, Position(7, 0)),
        Player(1, Position(0, 7)),
        Player(2, Position(24, 7)),
        Player(3, Position(0, 17)),
        Player(4, Position(24, 17)),
        Player(5, Position(17, 24))
    )

    private val roomList = listOf(
        Room(0, 0, 6, 5, 0, 42, R.drawable.selection_room_sotet_varazslatok_kivedese),
        Room(1, 0, 15, 6, 9, 49, R.drawable.selection_room_nagyterem),
        Room(2, 0, 24, 6, 18, 49, R.drawable.selection_room_gyengelkedo),
        Room(3, 8, 6, 11, 0, 28, R.drawable.selection_room_konyvtar),
        Room(4, 10, 14, 15, 10, 30, R.drawable.selection_room_dumbledore),
        Room(5, 9, 24, 15, 18, 49, R.drawable.selection_room_szukseg_szobaja),
        Room(6, 13, 6, 16, 0, 28, R.drawable.selection_room_bagolyhaz),
        Room(7, 19, 6, 24, 0, 42, R.drawable.selection_room_joslastan_terem),
        Room(8, 18, 15, 24, 9, 49, R.drawable.selection_room_serleg_terem),
        Room(9, 18, 24, 24, 18, 49, R.drawable.selection_room_bajitaltan_terem)
    )

    private val doorList = listOf(
        Door(Position(1, 7), roomList[0]),
        Door(Position(6, 1), roomList[0]),
        Door(Position(5, 8), roomList[1]),
        Door(Position(7, 12), roomList[1]),
        Door(Position(5, 16), roomList[1]),
        Door(Position(1, 17), roomList[2]),
        Door(Position(7, 23), roomList[2]),
        Door(Position(8, 7), roomList[3]),
        Door(Position(12, 2), roomList[3]),
        Door(Position(11, 9), roomList[4]),
        Door(Position(14, 9), roomList[4]),
        Door(Position(13, 15), roomList[4]),
        Door(Position(8, 19), roomList[5]),
        Door(Position(16, 19), roomList[5]),
        Door(Position(12, 4), roomList[6]),
        Door(Position(16, 7), roomList[6]),
        Door(Position(18, 2), roomList[7]),
        Door(Position(22, 7), roomList[7]),
        Door(Position(17, 12), roomList[8]),
        Door(Position(19, 8), roomList[8]),
        Door(Position(19, 16), roomList[8]),
        Door(Position(17, 22), roomList[9]),
        Door(Position(21, 17), roomList[9])
    )

    private var starPositions = arrayOf(
        Position(2, 8),
        Position(8, 10),
        Position(8, 23),
        Position(14, 17),
        Position(16, 11),
        Position(18, 16),
        Position(13, 19),
        Position(23, 16)
    )

    private var playerImageList = players

    private var cols = arrayOf(
        R.id.borderLeft,
        R.id.guidelineCol1,
        R.id.guidelineCol2,
        R.id.guidelineCol3,
        R.id.guidelineCol4,
        R.id.guidelineCol5,
        R.id.guidelineCol6,
        R.id.guidelineCol7,
        R.id.guidelineCol8,
        R.id.guidelineCol9,
        R.id.guidelineCol10,
        R.id.guidelineCol11,
        R.id.guidelineCol12,
        R.id.guidelineCol13,
        R.id.guidelineCol14,
        R.id.guidelineCol15,
        R.id.guidelineCol16,
        R.id.guidelineCol17,
        R.id.guidelineCol18,
        R.id.guidelineCol19,
        R.id.guidelineCol20,
        R.id.guidelineCol21,
        R.id.guidelineCol22,
        R.id.guidelineCol23,
        R.id.guidelineCol24,
        R.id.borderRight
    )
    private var rows = arrayOf(
        R.id.borderTop,
        R.id.guidelineRow1,
        R.id.guidelineRow2,
        R.id.guidelineRow3,
        R.id.guidelineRow4,
        R.id.guidelineRow5,
        R.id.guidelineRow6,
        R.id.guidelineRow7,
        R.id.guidelineRow8,
        R.id.guidelineRow9,
        R.id.guidelineRow10,
        R.id.guidelineRow11,
        R.id.guidelineRow12,
        R.id.guidelineRow13,
        R.id.guidelineRow14,
        R.id.guidelineRow15,
        R.id.guidelineRow16,
        R.id.guidelineRow17,
        R.id.guidelineRow18,
        R.id.guidelineRow19,
        R.id.guidelineRow20,
        R.id.guidelineRow21,
        R.id.guidelineRow22,
        R.id.guidelineRow23,
        R.id.guidelineRow24,
        R.id.borderBottom
    )

    companion object {
        const val ROWS = 24
        const val COLS = 24
    }

    init {
        for (i in 0..5) {
            setLayoutConstraintStart(playerImageList[i], cols[playerList[i].pos.col])
            setLayoutConstraintTop(playerImageList[i], rows[playerList[i].pos.row])
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
                    if (x < 24 &&stepInRoom(Position(y, x + 1)) == -1)
                        mapGraph.addEdge(current, Position(y, x + 1))

                }
            }
        }

        for (door: Door in doorList) {
            mapGraph.addEdge(Position(door.room.top, door.room.left), door.position)
        }
    }

    private fun stepInRoom(pos: Position): Int {
        for (room: Room in roomList) {
            if (pos.row >= room.top && pos.row <= room.bottom && pos.col >= room.left && pos.col <= room.right)
                return room.id
        }
        return -1
    }

    private fun Dijkstra(current: Position): HashMap<Position, Int> {
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

    fun showMovingOptions(idx: Int) {
        val stepCount = Random.nextInt(2, 12)

        selectionList = ArrayList()

        var distances: HashMap<Position, Int>? = null
        if (stepInRoom(playerList[idx].pos) == -1)
            distances = Dijkstra(playerList[idx].pos)
        else {
            val roomId = stepInRoom(playerList[idx].pos)
            for (door in doorList) {
                if (door.room.id == roomId) {
                    distances = mergeDistances(Dijkstra(Position(door.room.top, door.room.left)), distances)
                }
            }
        }

        for (x in 0..COLS) {
            for (y in 0..ROWS) {
                val current = Position(y, x)
                if (stepInRoom(current) == -1 && distances!![current]!! <= stepCount) {
                    drawSelection(R.drawable.field_selection, y, x, idx)
                }
            }
        }

        if (stepInRoom(playerList[idx].pos) == -1) {
            for (door in doorList) {
                if (distances!![door.position]!! <= stepCount - 1) {
                    drawSelection(door.room.selection, door.room.top, door.room.left, idx)
                }
            }
        }
    }

    private fun drawSelection(@DrawableRes selRes: Int, row: Int, col: Int, playerId: Int) {
        val selection = ImageView(mapLayout.context)
        selectionList.add(selection)
        selection.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        selection.setImageResource(selRes)
        selection.visibility = ImageView.VISIBLE
        setLayoutConstraintStart(selection, cols[col])
        setLayoutConstraintTop(selection, rows[row])
        selection.setOnClickListener {
            playerList[playerId].pos = Position(row, col)
            setLayoutConstraintStart(playerImageList[playerId], cols[playerList[playerId].pos.col])
            setLayoutConstraintTop(playerImageList[playerId], rows[playerList[playerId].pos.row])

            for (sel in selectionList)
                mapLayout.removeView(sel)
        }
        mapLayout.addView(selection)
    }

    @BindingAdapter("app:layout_constraintTop_toTopOf")
    fun setLayoutConstraintTop(view: View, row: Int) {
        val layoutParams: ConstraintLayout.LayoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = row
        view.layoutParams = layoutParams
    }

    @BindingAdapter("app:layout_constraintStart_toStartOf")
    fun setLayoutConstraintStart(view: View, col: Int) {
        val layoutParams: ConstraintLayout.LayoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.startToStart = col
        view.layoutParams = layoutParams
    }
}