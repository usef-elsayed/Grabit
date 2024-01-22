package com.yousefelsayed.gamescheap.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.forEach
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var v: ActivityMainBinding
    private lateinit var controller: NavController
    private val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        // react on change
        // you can check destination.id or destination.label and act based on that
        if (destination.id == R.id.noInternetFragment){
            unCheckAllBottomNavItems()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        v = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
    }

    private fun init() {
        //Remove background to prevent overdraw
        window.setBackgroundDrawable(null)
        //Bottom nav setup
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavFragmentContainer)
        controller = navHostFragment?.findNavController()!!
        v.homeScreenBottomNav.setupWithNavController(controller)
        if (intent.data != null && intent.data.toString() == "GamesFragment"){
            controller.navigate(R.id.gamesFragment)
        }else if (intent.data != null && intent.data.toString() == "SearchFragment"){
            controller.navigate(R.id.searchFragment)
        }
    }
    private fun unCheckAllBottomNavItems() {
        v.homeScreenBottomNav.menu.findItem(R.id.uncheckedItem).isChecked = true
    }

    override fun onPause() {
        controller.removeOnDestinationChangedListener(listener)
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
        controller.addOnDestinationChangedListener(listener)
    }
}