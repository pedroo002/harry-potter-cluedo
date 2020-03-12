package neptun.jxy1vz.cluedo.ui.map

import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.Graph
import neptun.jxy1vz.cluedo.model.Player
import neptun.jxy1vz.cluedo.model.Position
import neptun.jxy1vz.cluedo.model.Room
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MapViewModel(players: List<ImageView>, layout: ConstraintLayout) : BaseObservable() {
    private var mapLayout = layout

    private var playerGreen = Player(0, Position(7, 0))
    private var playerRed = Player(1, Position(0, 7))
    private var playerYellow = Player(2, Position(24, 7))
    private var playerBlue = Player(3, Position(0, 17))
    private var playerPurple = Player(4, Position(24, 17))
    private var playerWhite = Player(5, Position(17, 24))

    private val roomList = listOf(
        Room(0, 0, 6, 5, 0, 42),
        Room(1, 0, 15, 6, 9, 49),
        Room(2, 0, 24, 6, 18, 49),
        Room(3, 8, 6, 11, 0, 28),
        Room(4, 10, 14, 15, 10, 30),
        Room(5, 9, 24, 15, 18, 49),
        Room(6, 13, 6, 16, 0, 28),
        Room(7, 19, 6, 24, 0, 42),
        Room(8, 18, 15, 24, 9, 49),
        Room(9, 18, 24, 24, 18, 49)
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

    private var green = players[0]

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
        setLayoutConstraintStart(players[0], cols[playerGreen.pos.col])
        setLayoutConstraintTop(players[0], rows[playerGreen.pos.row])

        val mapGraph = Graph<Position>()
        for (x in 0..24) {
            for (y in 0..24) {
                val current = Position(y, x)
                if (!stepInRoom(current)) {
                    if (y > 0 && !stepInRoom(Position(y - 1, x)))
                        mapGraph.addEdge(current, Position(y - 1, x))
                    if (y < 24 && !stepInRoom(Position(y + 1, x)))
                        mapGraph.addEdge(current, Position(y + 1, x))
                    if (x > 0 && !stepInRoom(Position(y, x - 1)))
                        mapGraph.addEdge(current, Position(y, x - 1))
                    if (x < 24 &&!stepInRoom(Position(y, x + 1)))
                        mapGraph.addEdge(current, Position(y, x + 1))

                }
            }
        }
    }

    private fun stepInRoom(pos: Position): Boolean {
        for (room: Room in roomList) {
            if (pos.row >= room.top && pos.row <= room.bottom && pos.col >= room.left && pos.col <= room.right)
                return true
        }
        return false
    }

    private fun canStepOnIt(pos: Position, playerPos: Position, stepCount: Int): Boolean {
        return true
    }

    fun showMovingOptions() {
        val stepCount = Random.nextInt(2, 12)
        val minLimitX = max(playerGreen.pos.col - stepCount, 0)
        val maxLimitX = min(playerGreen.pos.col + stepCount, 24)
        val minLimitY = max(playerGreen.pos.row - stepCount, 0)
        val maxLimitY = min(playerGreen.pos.row + stepCount, 24)

        val selectionList: ArrayList<ImageView> = ArrayList()

        for (x in minLimitX..maxLimitX) {
            for (y in minLimitY..maxLimitY) {
                if (canStepOnIt(Position(y, x), playerGreen.pos, stepCount)) {
                    val selection = ImageView(mapLayout.context)
                    selectionList.add(selection)
                    selection.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    selection.setImageResource(R.drawable.field_selection)
                    selection.visibility = ImageView.VISIBLE
                    setLayoutConstraintStart(selection, cols[x])
                    setLayoutConstraintTop(selection, rows[y])
                    selection.setOnClickListener {
                        playerGreen.pos = Position(y, x)
                        setLayoutConstraintStart(green, cols[x])
                        setLayoutConstraintTop(green, rows[y])

                        for (sel: ImageView in selectionList)
                            mapLayout.removeView(sel)
                    }
                    mapLayout.addView(selection)
                }
            }
        }
    }

    fun moveDown() {
        if (playerGreen.pos.row == ROWS)
            return
        playerGreen.pos.row++
        setLayoutConstraintTop(green, rows[playerGreen.pos.row])
    }

    fun moveUp() {
        if (playerGreen.pos.row == 0)
            return
        playerGreen.pos.row--
        setLayoutConstraintTop(green, rows[playerGreen.pos.row])
    }

    fun moveLeft() {
        if (playerGreen.pos.col == 0)
            return
        playerGreen.pos.col--
        setLayoutConstraintStart(green, cols[playerGreen.pos.col])
    }

    fun moveRight() {
        if (playerGreen.pos.col == COLS)
            return
        playerGreen.pos.col++
        setLayoutConstraintStart(green, cols[playerGreen.pos.col])
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