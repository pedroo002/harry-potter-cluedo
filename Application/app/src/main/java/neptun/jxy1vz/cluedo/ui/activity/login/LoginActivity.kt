package neptun.jxy1vz.cluedo.ui.activity.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityLoginBinding
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuActivity

class LoginActivity : AppCompatActivity(), LoginActivityListener {

    private lateinit var activityLoginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        activityLoginBinding.loginViewModel = LoginViewModel(activityLoginBinding, applicationContext, this)
    }

    override fun goToMenu(playerName: String) {
        val pref = application.getSharedPreferences(applicationContext.resources.getString(R.string.user_pref), Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(applicationContext.resources.getString(R.string.logged_in_user), playerName)
        editor.apply()

        val menuIntent = Intent(applicationContext, MenuActivity::class.java)
        menuIntent.putExtra(applicationContext.resources.getString(R.string.logged_in_user), playerName)
        applicationContext.startActivity(menuIntent)
    }
}