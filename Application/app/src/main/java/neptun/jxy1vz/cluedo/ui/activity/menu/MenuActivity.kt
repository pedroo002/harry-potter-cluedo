package neptun.jxy1vz.cluedo.ui.activity.menu

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMenuBinding
import neptun.jxy1vz.cluedo.domain.model.helper.DatabaseAccess

class MenuActivity : AppCompatActivity(), MenuViewModel.MenuListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = applicationContext.getSharedPreferences(getString(R.string.database_name), Context.MODE_PRIVATE)
        val editor = pref.edit()
        if (!pref.contains(getString(R.string.first_start_pref))) {
            editor.putBoolean(getString(R.string.first_start_pref), true)
            GlobalScope.launch(Dispatchers.IO) {
                val db = DatabaseAccess(applicationContext)
                db.uploadDatabase()
            }
        }
        else
            editor.putBoolean(getString(R.string.first_start_pref), false)
        editor.apply()

        val activityMenuBinding = DataBindingUtil.setContentView<ActivityMenuBinding>(this, R.layout.activity_menu)

        val fm = supportFragmentManager
        activityMenuBinding.menuViewModel = MenuViewModel(fm, this)
    }

    override fun exitGame() {
        finish()
    }
}