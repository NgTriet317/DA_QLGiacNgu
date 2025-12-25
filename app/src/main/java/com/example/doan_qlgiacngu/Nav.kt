package com.example.doan_qlgiacngu


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class Nav : AppCompatActivity() {
    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setControl()
        setEvent()
    }

    private fun setControl() {
        drawer = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

    }

    private fun setEvent() {
        toggle = ActionBarDrawerToggle(
            this, drawer,
            R.string.app_name, R.string.app_name
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                }

                R.id.nav_chitiet -> {
                    val intent = Intent(this, SleepDetails::class.java)
                    startActivity(intent)
                }

                R.id.nav_trangthai -> {
                    val intent = Intent(this, SleepActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_lichsu -> {
                    val intent = Intent(this, SleepWM::class.java)
                    startActivity(intent)
                }

                R.id.nav_thongke -> {
                    val intent = Intent(this, SleepHistory::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }
}