package neptun.jxy1vz.cluedo.ui.map

import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.Player

class MapViewModel(dpi: Int, greenTile: ImageView) : BaseObservable() {
    private var player1 = Player(0, 0)
    private var green = greenTile

    var cols = arrayOf(
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

    var rows = arrayOf(
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
        setLayoutConstraintStart(greenTile, cols[player1.col])
        setLayoutConstraintTop(greenTile, rows[player1.row])
    }

    fun moveDown() {
        if (player1.row == ROWS)
            return
        player1.row++
        setLayoutConstraintTop(green, rows[player1.row])
    }

    fun moveUp() {
        if (player1.row == 0)
            return
        player1.row--
        setLayoutConstraintTop(green, rows[player1.row])
    }

    fun moveLeft() {
        if (player1.col == 0)
            return
        player1.col--
        setLayoutConstraintStart(green, cols[player1.col])
    }

    fun moveRight() {
        if (player1.col == COLS)
            return
        player1.col++
        setLayoutConstraintStart(green, cols[player1.col])
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