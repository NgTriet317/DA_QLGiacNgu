package com.example.doan_qlgiacngu

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class showDatabase : AppCompatActivity() {
    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var dbSleep: AppDatabase
    lateinit var dbAwake: AppDatabase
    lateinit var luuTgSleep: List<timeSleep>
    lateinit var luuTgAwake: List<timeAwake>
    lateinit var lvDTB: ListView
    lateinit var lvDTB2: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_database)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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
        toolbar = findViewById(R.id.toolbar)
        lvDTB = findViewById(R.id.lvDTB)
        lvDTB2 = findViewById(R.id.lvDTB2)
    }

    private fun setEvent() {
        dbSleep = AppDatabase.get(this)
        dbAwake = AppDatabase.get(this)
        layDS()



        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home ->{
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                }
                R.id.nav_chitiet ->{
                    val intent = Intent(this, SleepDetails::class.java)
                    startActivity(intent)
                }
                R.id.nav_trangthai ->{
                    val intent = Intent(this, SleepActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_lichsu ->{
                    val intent = Intent(this, SleepWM::class.java)
                    startActivity(intent)
                }
                R.id.nav_thongke ->{
                    val intent = Intent(this, SleepHistory::class.java)
                    startActivity(intent)
                }
            }
            drawer.closeDrawers()
            true
        }
    }
    fun layDS()
    {
        luuTgSleep = dbSleep.timeSleepDao().getAll()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, luuTgSleep)
        lvDTB.adapter = adapter

        luuTgAwake = dbAwake.timeAwakeDao().getAll()
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, luuTgAwake)
        lvDTB2.adapter = adapter2
    }
}