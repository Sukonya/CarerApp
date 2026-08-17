package com.cse22201576.carerapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navDrawer: NavigationView
    private lateinit var toolbar: Toolbar
    private var role = "FAMILY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        role = intent.getStringExtra("role") ?: "FAMILY"

        drawerLayout = findViewById(R.id.drawerLayout)
        bottomNav = findViewById(R.id.bottomNav)
        navDrawer = findViewById(R.id.navDrawer)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = if (role == "FAMILY") "Find a Carer" else "My Dashboard"

        // hamburger icon
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.app_name, R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // load default fragment
        loadFragment(HomeFragment.newInstance(role))

        // bottom nav clicks
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment.newInstance(role))
                R.id.nav_bookings -> loadFragment(BookingsFragment.newInstance(role))
                R.id.nav_notifications -> loadFragment(NotificationsFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment.newInstance(role))
            }
            true
        }

        // drawer clicks
        navDrawer.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                R.id.drawer_settings -> {
                    // settings coming later
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}