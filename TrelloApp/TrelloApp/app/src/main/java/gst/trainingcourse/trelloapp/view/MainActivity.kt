package gst.trainingcourse.trelloapp.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.databinding.ActivityMainBinding
import gst.trainingcourse.trelloapp.model.User
import gst.trainingcourse.trelloapp.request.UserRequest
import gst.trainingcourse.trelloapp.utils.Constants.MY_PREFS_NAME
import gst.trainingcourse.trelloapp.viewmodel.login.ValidateViewModel


/**
 * Class MainActivity
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var drawerLayout: DrawerLayout? = null
    private var appBarConfiguration: AppBarConfiguration? = null
    private lateinit var navHostFragment: NavHostFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        setDefaultTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        createNavDrawer()
        checkUser()
    }

    /**
     * set default theme for Activity again after splash screen.
     */
    private fun setDefaultTheme() {
        setTheme(R.style.Theme_TrelloApp)
        supportActionBar?.hide()
        // Hide status bar
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * init view and object of Main Activity.
     */
    private fun initViews() {
        //navController
         navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHost) as NavHostFragment
        navController = navHostFragment.navController
        //navDrawer
        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration.Builder(R.id.homeFragment, R.id.workspaceFragment, R.id.helpFragment)
            .setOpenableLayout(drawerLayout).build()
    }

    /**
     * set up MainActivity with a navDrawer.
     */
    private fun createNavDrawer() {
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration!!)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    /**
     * support navigate up on action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHost)
        return NavigationUI.navigateUp(navController, appBarConfiguration!!)
    }


    private fun checkUser() {
        val prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        val userId = prefs.getInt(getString(R.string.user_id), 0) //0 is the default value.
        if (userId != 0) {
            UserRequest.getUserById(userId) {it ->
                if (it != null) {
                    user = it
                }
            }
        } else {

        }

    }

    companion object {
        var user: User? = null
    }

}