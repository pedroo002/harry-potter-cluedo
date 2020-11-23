package neptun.jxy1vz.hp_cluedo.ui.activity.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_login.view.*
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.ActivityLoginBinding
import neptun.jxy1vz.hp_cluedo.ui.activity.menu.MenuActivity

class LoginActivity : AppCompatActivity(), LoginActivityListener {

    private lateinit var activityLoginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        activityLoginBinding.loginViewModel = LoginViewModel(activityLoginBinding, applicationContext, this, lifecycleScope)

        activityLoginBinding.root.txtPlayerName.addTextChangedListener {
            buttonEnableValidator()
        }
        activityLoginBinding.root.txtPassword.addTextChangedListener {
            buttonEnableValidator()
        }
    }

    private fun buttonEnableValidator() {
        activityLoginBinding.root.btnLogin.isEnabled = activityLoginBinding.root.txtPlayerName.text!!.isNotEmpty() && activityLoginBinding.root.txtPassword.text!!.isNotEmpty()
    }

    override fun goToMenu() {
        val menuIntent = Intent(applicationContext, MenuActivity::class.java)
        menuIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(menuIntent)
    }
}