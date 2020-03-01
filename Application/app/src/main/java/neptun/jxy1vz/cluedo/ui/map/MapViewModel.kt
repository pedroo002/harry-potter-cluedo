package neptun.jxy1vz.cluedo.ui.map

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Dimension
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.databinding.library.baseAdapters.BR
import neptun.jxy1vz.cluedo.model.Player

class MapViewModel(dpi: Int, greenTile: ImageView) : BaseObservable() {
    private var player1 = Player(34, 288)
    private var green = greenTile

    /*
    ELtolás számítás: dp = px / (dpi / 160) = 160 * px / dpi
     */
    companion object {
        var SCREEN_DPI: Int? = null
        const val TILE_HEIGHT_PX = 40
        const val TILE_WIDTH_PX = 41
    }

    init {
        SCREEN_DPI = 432
    }

    @Bindable
    @Dimension
    fun getPlayer1MarginX() = player1.x

    private fun setPlayer1MarginX(x: Int) {
        player1.x = x
        notifyPropertyChanged(BR.player1MarginX)
    }

    @Bindable
    @Dimension
    fun getPlayer1MarginY() = player1.y

    private fun setPlayer1MarginY(y: Int) {
        player1.y = y
        notifyPropertyChanged(BR.player1MarginY)
    }

    fun moveDown() {
        val newY = player1.y + TILE_HEIGHT_PX
        setLayoutMarginTop(green, newY.toFloat())
        setPlayer1MarginY(newY)
    }

    fun moveUp() {
        val newY = player1.y - TILE_HEIGHT_PX
        setLayoutMarginTop(green, newY.toFloat())
        setPlayer1MarginY(newY)
    }

    fun moveLeft() {
        val newX = player1.x - TILE_WIDTH_PX
        setLayoutMarginStart(green, newX.toFloat())
        setPlayer1MarginX(newX)
    }

    fun moveRight() {
        val newX = player1.x + TILE_WIDTH_PX
        setLayoutMarginStart(green, newX.toFloat())
        setPlayer1MarginX(newX)
    }

    @BindingAdapter("android:layout_marginTop")
    fun setLayoutMarginTop(view: View, margin: Float) {
        val layoutParams: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = margin.toInt()
        view.layoutParams = layoutParams
    }

    @BindingAdapter("android:layout_marginLeft")
    fun setLayoutMarginStart(view: View, margin: Float) {
        val layoutParams: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.marginStart = margin.toInt()
        view.layoutParams = layoutParams
    }
}