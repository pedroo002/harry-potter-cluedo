package neptun.jxy1vz.cluedo.ui.menu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.ui.map.MapActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnStart = findViewById<Button>(R.id.btnStart)
        btnStart.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java)
            startActivity(mapIntent)
        }

        val btnExit = findViewById<Button>(R.id.btnExit)
        btnExit.setOnClickListener {
            finish()
        }
    }
}