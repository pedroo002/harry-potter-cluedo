package neptun.jxy1vz.hp_cluedo.ui.activity.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import neptun.jxy1vz.hp_cluedo.databinding.ActivityLoginBinding
import neptun.jxy1vz.hp_cluedo.domain.model.helper.DatabaseAccess
import neptun.jxy1vz.hp_cluedo.domain.util.isServerReachable
import neptun.jxy1vz.hp_cluedo.ui.activity.menu.MenuActivity
import java.net.Inet4Address

class LoginActivity : AppCompatActivity(), LoginActivityListener {

    private lateinit var progressBar: ProgressBar
    private lateinit var activityLoginBinding: ActivityLoginBinding

    override fun setMaxProgress(max: Int) {
        progressBar.max = max
        activityLoginBinding.loadingAssetsProgressBar.visibility = View.VISIBLE
        activityLoginBinding.tvLoadingAssets.visibility = View.VISIBLE
    }

    override fun increaseProgress() {
        activityLoginBinding.loadingAssetsProgressBar.progress++
        activityLoginBinding.tvLoadingAssets.text = applicationContext.resources.getString(R.string.loading_assets, progressBar.progress, progressBar.max)
            if (progressBar.progress == progressBar.max) {
                activityLoginBinding.loadingAssetsProgressBar.visibility = View.GONE
                activityLoginBinding.tvLoadingAssets.visibility = View.GONE
                activityLoginBinding.txtPlayerName.isEnabled = true
                activityLoginBinding.txtPassword.isEnabled = true
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        activityLoginBinding.loginViewModel = LoginViewModel(activityLoginBinding, applicationContext, this, lifecycleScope)
        progressBar = activityLoginBinding.loadingAssetsProgressBar

        activityLoginBinding.txtPlayerName.isEnabled = false
        activityLoginBinding.txtPassword.isEnabled = false

        activityLoginBinding.loginRefresh.isRefreshing = true
        lifecycleScope.launch(Dispatchers.IO) {
            val ipAddress = Inet4Address.getByName(RetrofitInstance.DOMAIN).hostAddress
            if (!isServerReachable(ipAddress)) {
                withContext(Dispatchers.Main) {
                    showWarning()
                }
            }
            else {
                withContext(Dispatchers.Main) {
                    activityLoginBinding.loginRefresh.isRefreshing = false
                    activityLoginBinding.txtPlayerName.isEnabled = true
                    activityLoginBinding.txtPassword.isEnabled = true
                }
            }
            withContext(Dispatchers.Main) {
                activityLoginBinding.loginRefresh.setOnRefreshListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val online = isServerReachable(ipAddress)
                        withContext(Dispatchers.Main) {
                            if (online) {
                                Snackbar.make(activityLoginBinding.root, applicationContext.resources.getString(R.string.connected), Snackbar.LENGTH_LONG).show()
                                activityLoginBinding.txtPlayerName.isEnabled = true
                                activityLoginBinding.txtPassword.isEnabled = true
                                checkIfFirstStart()
                                activityLoginBinding.loginRefresh.isRefreshing = false
                            }
                            else {
                                showWarning()
                            }
                        }
                    }
                }

                activityLoginBinding.root.txtPlayerName.addTextChangedListener {
                    buttonEnableValidator()
                }
                activityLoginBinding.root.txtPassword.addTextChangedListener {
                    buttonEnableValidator()
                }
            }
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

    private fun checkIfFirstStart() {
        val pref = applicationContext.getSharedPreferences(
            getString(R.string.database_name),
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        if (!pref.contains(getString(R.string.first_start_pref))) {
            editor.putBoolean(getString(R.string.first_start_pref), true)
            activityLoginBinding.txtPlayerName.isEnabled = false
            activityLoginBinding.txtPassword.isEnabled = false
            lifecycleScope.launch(Dispatchers.IO) {
                val db = DatabaseAccess(applicationContext)
                db.uploadDatabase(this@LoginActivity)
            }
        } else {
            editor.putBoolean(getString(R.string.first_start_pref), false)
        }
        editor.apply()
    }

    private fun showWarning() {
        activityLoginBinding.loginRefresh.isRefreshing = false
        Snackbar.make(activityLoginBinding.root, applicationContext.resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
        activityLoginBinding.txtPlayerName.isEnabled = false
        activityLoginBinding.txtPassword.isEnabled = false
    }
}