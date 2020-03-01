package neptun.jxy1vz.cluedo.ui.activity.map

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
    private var player1 = Player(34, 288, "34,288")
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
        SCREEN_DPI = dpi
    }

    @Bindable
    @Dimension
    fun getPlayer1MarginX() = player1.x

    fun setPlayer1MarginX(x: Int) {
        player1.x = x
        setCoordinates("${player1.x},${player1.y}")
        notifyPropertyChanged(BR.player1MarginX)
    }

    @Bindable
    @Dimension
    fun getPlayer1MarginY() = player1.y

    fun setPlayer1MarginY(y: Int) {
        player1.y = y
        setCoordinates("${player1.x},${player1.y}")
        notifyPropertyChanged(BR.player1MarginY)
    }

    @Bindable
    fun getCoordinates() = player1.coordinates

    fun setCoordinates(coord: String) {
        player1.coordinates = coord
        notifyPropertyChanged(BR.coordinates)
    }

    fun moveDown() {
        val newY = player1.y + TILE_HEIGHT_PX / (SCREEN_DPI!! / 160)
        setLayoutMarginTop(green, newY.toFloat() * SCREEN_DPI!! / 160)
        setPlayer1MarginY(newY)
    }

    fun moveUp() {
        val newY = player1.y - TILE_HEIGHT_PX / (SCREEN_DPI!! / 160)
        setLayoutMarginTop(green, newY.toFloat() * SCREEN_DPI!! / 160)
        setPlayer1MarginY(newY)
    }

    fun moveLeft() {
        val newX = player1.x - TILE_WIDTH_PX * 160 / SCREEN_DPI!!
        setLayoutMarginStart(green, newX.toFloat() * SCREEN_DPI!! / 160)
        setPlayer1MarginX(newX)
    }

    fun moveRight() {
        val newX = player1.x + TILE_WIDTH_PX * 160 / SCREEN_DPI!!
        setLayoutMarginStart(green, newX.toFloat() * SCREEN_DPI!! / 160)
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