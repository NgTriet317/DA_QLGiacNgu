package com.example.doan_qlgiacngu

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Thêm vào trong MainActivity.kt
class Setting : AppCompatActivity() {
    lateinit var tvNhac: TextView
    lateinit var tvMucTieu: TextView
    lateinit var arrBack: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setControl()
        setEvent()
    }

    private fun setControl(){
        tvNhac = findViewById(R.id.tvNhac)
        arrBack = findViewById(R.id.arrBack)
        tvMucTieu = findViewById(R.id.tvMucTieu)
    }
    private fun setEvent(){
        tvMucTieu.setOnClickListener {
            val intent = Intent(this, admin_MucTieu::class.java)
            startActivity(intent)
        }

        arrBack.setOnClickListener {
            finish()
        }

        tvNhac.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Chọn nhạc chuông báo thức")
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, null as Uri?)
            startActivityForResult(intent, 999)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == RESULT_OK) {
            val uri: Uri? = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                // Lưu URI vào SharedPreferences
                val sharedPref = getSharedPreferences("ALARM_SETTINGS", Context.MODE_PRIVATE)
                sharedPref.edit().putString("SELECTED_RINGTONE_URI", uri.toString()).apply()

                tvNhac.text = "Đã chọn: " + RingtoneManager.getRingtone(this, uri).getTitle(this)
            }
        }
    }
}
