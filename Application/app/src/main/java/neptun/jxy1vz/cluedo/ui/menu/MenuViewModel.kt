package neptun.jxy1vz.cluedo.ui.menu

import android.content.Context
import android.content.Intent
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import neptun.jxy1vz.cluedo.ui.map.MapActivity

class MenuViewModel(private val context: Context, private val fragmentManager: FragmentManager) : BaseObservable() {
    fun openMap() {
        val mapIntent = Intent(context, MapActivity::class.java)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mapIntent)
    }
}