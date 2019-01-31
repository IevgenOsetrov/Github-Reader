package com.dev.joks.githubreader.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.dev.joks.githubreader.R
import com.dev.joks.githubreader.screens.base.BaseActivity
import org.jetbrains.anko.toast


class MainActivity : BaseActivity() {

    private var firstBackPressed = false

    override fun getLayoutRes(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)



        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.searchFragment -> supportActionBar?.setTitle(R.string.search)
                R.id.userDetailsFragment -> supportActionBar?.setTitle(R.string.user_details)
            }
        }
    }

    override fun onBackPressed() {
        val host = supportFragmentManager
            .findFragmentById(com.dev.joks.githubreader.R.id.nav_host_fragment) as NavHostFragment
        when {
            host.navController.currentDestination?.id == com.dev.joks.githubreader.R.id.searchFragment -> {
                if (firstBackPressed) {
                    finish()
                    return
                }

                firstBackPressed = true
                Handler(Looper.getMainLooper()).postDelayed({ firstBackPressed = false }, 2000)
                toast(getString(R.string.press_back_more))
            }
            else -> super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
