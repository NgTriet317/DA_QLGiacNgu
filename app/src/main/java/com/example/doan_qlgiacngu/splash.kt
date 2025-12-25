package com.example.doan_qlgiacngu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class splash : AppCompatActivity() {

    lateinit var db : AppDatabase
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        db = AppDatabase.get(this)



        // Ánh xạ view
        progressBar = findViewById(R.id.progressBar)

        // Hiện loading
        progressBar.visibility = View.VISIBLE

        // Chờ 3 giây rồi chuyển màn
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
            startActivity(Intent(this, login::class.java))
            finish()
        }, 3000)
    }
}
