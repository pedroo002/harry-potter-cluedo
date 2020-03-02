package neptun.jxy1vz.cluedo.ui.map

import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.Player
import neptun.jxy1vz.cluedo.model.Position

class MapViewModel(players: List<ImageView>) : BaseObservable() {
    private var playerGreen = Player(0, Position(0, 7))
    private var playerRed = Player(1, Position(7, 0))
    private var playerYellow = Player(2, Position(24, 7))
    private var playerBlue = Player(3, Position(0, 17))
    private var playerPurple = Player(4, Position(24, 17))
    private var playerWhite = Player(5, Position(17, 24))

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