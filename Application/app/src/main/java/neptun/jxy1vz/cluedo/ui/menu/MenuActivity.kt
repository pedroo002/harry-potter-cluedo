package neptun.jxy1vz.cluedo.ui.menu

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityMenuBinding = DataBindingUtil.setContentView<ActivityMenuBinding>(this, R.layout.activity_menu)

        val fm = supportFragmentManager
        activityMenuBinding.menu = MenuViewModel(applicationContext, fm)

        val btnExit = findViewById<Button>(R.id.btnExit)
        btnExit.setOnClickListener {
            finish()
        }
    }
}