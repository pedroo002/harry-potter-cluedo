package neptun.jxy1vz.cluedo.ui.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity(), MenuViewModel.MenuListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityMenuBinding = DataBindingUtil.setContentView<ActivityMenuBinding>(this, R.layout.activity_menu)

        val fm = supportFragmentManager
        activityMenuBinding.menuViewModel = MenuViewModel(fm, this)
    }

    override fun exitGame() {
        finish()
    }
}