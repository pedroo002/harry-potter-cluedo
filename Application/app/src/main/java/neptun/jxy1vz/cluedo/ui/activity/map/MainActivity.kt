package neptun.jxy1vz.cluedo.ui.activity.map

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil.setContentView
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val greenTile = findViewById<ImageView>(R.id.ivGreenTile)
        activityMainBinding.player1 = MapViewModel(resources.displayMetrics.densityDpi, greenTile)
        activityMainBinding.executePendingBindings()
    }

    @BindingAdapter("android:layout_marginTop")
    fun setLayoutMarginTop(view: View, margin: Float) {
        val layoutParams: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, margin.toInt(), layoutParams.rightMargin, layoutParams.bottomMargin)
        view.layoutParams = layoutParams
    }
}
