package com.example.doan_qlgiacngu

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class startSleep : AppCompatActivity() {
    lateinit var tvSleep: TextView
    lateinit var tvAwake: TextView

    lateinit var db: AppDatabase
    lateinit var btnFix: Button
    lateinit var btnAwake: Button
    lateinit var tvDate : TextView
    lateinit var tcClock: TextClock



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_sleep)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setControl()
        setEvent()
    }

    private fun setControl() {
        tvSleep = findViewById(R.id.tvSleep)
        tvAwake = findViewById(R.id.tvAwake)
        btnFix = findViewById(R.id.btnFix)
        btnAwake = findViewById(R.id.btnAwake)
        tcClock = findViewById(R.id.textClock)
        tvDate = findViewById(R.id.tvDate)
    }
    private fun setEvent() {
        tvDate.text = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() + "/" +
                (Calendar.getInstance().get(Calendar.MONTH) + 1).toString() + "/" +
                Calendar.getInstance().get(Calendar.YEAR).toString()
        db = AppDatabase.get(this)

        tvSleep.text = db.timeSleepDao().getLast().toString()
        tvAwake.text = db.timeAwakeDao().getLast().toString()


        tvSleep.setOnClickListener {
            //Lấy thời gian hiện tại
            val ngayGioHienTai = Calendar.getInstance()
            //lấy giờ và phút hiện tại
            val hour = ngayGioHienTai.get(Calendar.HOUR_OF_DAY)
            val minute = ngayGioHienTai.get(Calendar.MINUTE)
            //lấy ngày / tháng / năm hiện tại
            val day = ngayGioHienTai.get(Calendar.DAY_OF_MONTH)
            var month = ngayGioHienTai.get(Calendar.MONTH) + 1
            var year = ngayGioHienTai.get(Calendar.YEAR)

            val chonThoiGian = TimePickerDialog(
                this,
                { _, chonHour, chonMinute ->
                    val result = "${chonHour.toString().padStart(2, '0')}:" +
                            "${chonMinute.toString().padStart(2, '0')} ${day}/${month}/${year}"
                    tvSleep.text = result
                },
                hour, minute, true // true = kiểu 24h, false = kiểu AM/PM
            )
            chonThoiGian.show()
        }


        tvAwake.setOnClickListener {
            //Lấy thời gian hiện tại
            val ngayGioHienTai = Calendar.getInstance()
            //lấy giờ và phút hiện tại
            val hour = ngayGioHienTai.get(Calendar.HOUR_OF_DAY)
            val minute = ngayGioHienTai.get(Calendar.MINUTE)


            //hiển thị bảng chọn thời gian cho textview thức
            val chonThoiGian = TimePickerDialog(
                this,
                { _, chonHour, chonMinute ->
                    //lấy thời gian ngủ đã chọn bên trên
                    val gioNguText = tvSleep.text.toString()
                    //nếu trống thì thông báo và không lưu
                    if (gioNguText.isEmpty()) {
                        Toast.makeText(this, "Hãy chọn giờ đi ngủ trước!", Toast.LENGTH_SHORT)
                            .show()
                        return@TimePickerDialog
                    }


                    // tách ngày giờ ngủ ra thành 2 phần riêng biệt
                    val parts = gioNguText.split(" ")
                    val timePart = parts[0] // HH:mm
                    val datePart = parts[1] // dd/MM/yyyy


                    val (nguHour, nguMinute) = timePart.split(":").map { it.toInt() }
                    val (nguDay, nguMonth, nguYear) = datePart.split("/").map { it.toInt() }


                    val ngayGioHienTai = Calendar.getInstance()
                    ngayGioHienTai.set(nguYear, nguMonth - 1, nguDay)


                    if (chonHour < nguHour || chonHour == nguHour && chonMinute < nguMinute) {
                        ngayGioHienTai.add(Calendar.DAY_OF_MONTH, 1)
                    }

                    //Cho ngày tháng năm bằng ngayGioHienTai
                    val dayThuc = ngayGioHienTai.get(Calendar.DAY_OF_MONTH)
                    val monthThuc = ngayGioHienTai.get(Calendar.MONTH) + 1
                    val yearThuc = ngayGioHienTai.get(Calendar.YEAR)

                    // gắn vào result theo định dạng hh:mm dd/MM/yyyy
                    val result = String.format(
                        "%02d:%02d %02d/%02d/%04d",
                        chonHour, chonMinute, dayThuc, monthThuc, yearThuc
                    )
                    //gắn result vào textview thức
                    tvAwake.setText(result)
                },
                hour, minute, true
            )
            chonThoiGian.show()
        }

        btnFix.setOnClickListener {

            val gioNguText = tvSleep.text.toString()
            val gioThucText = tvAwake.text.toString()

            if (gioNguText.isEmpty() || gioThucText.isEmpty()) {
                Toast.makeText(this, "Chưa chọn giờ ngủ / giờ thức", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // HH:mm dd/MM/yyyy
            fun parseTime(text: String): List<String>? {
                if (!text.contains(" ")) return null
                val parts = text.split(" ")
                if (parts.size != 2) return null
                val time = parts[0].split(":")
                val date = parts[1].split("/")
                if (time.size != 2 || date.size != 3) return null
                return listOf(time[0], time[1], date[0], date[1], date[2])
            }

            val ngu = parseTime(gioNguText) ?: return@setOnClickListener
            val thuc = parseTime(gioThucText) ?: return@setOnClickListener


            val oldSleep = db.timeSleepDao().getAll().last()
            val newSleep = oldSleep.copy(
                gio = ngu[0],
                phut = ngu[1],
                ngay = ngu[2],
                thang = ngu[3],
                nam = ngu[4]
            )
            db.timeSleepDao().update(newSleep)


            val oldAwake = db.timeAwakeDao().getAll().last()
            val newAwake = oldAwake.copy(
                gio = thuc[0],
                phut = thuc[1],
                ngay = thuc[2],
                thang = thuc[3],
                nam = thuc[4]
            )
            db.timeAwakeDao().update(newAwake)

            Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show()
        }


        btnAwake.setOnClickListener {
            val gioThucText = Calendar.getInstance()

            val hour = gioThucText.get(Calendar.HOUR_OF_DAY)
            val minute = gioThucText.get(Calendar.MINUTE)
            //lấy ngày / tháng / năm hiện tại
            val day = gioThucText.get(Calendar.DAY_OF_MONTH)
            var month = gioThucText.get(Calendar.MONTH) + 1
            var year = gioThucText.get(Calendar.YEAR)

            val tg = "$hour:$minute $day/$month/$year"

            fun parseTime(text: String): List<String>? {
                if (!text.contains(" ")) return null
                val parts = text.split(" ")
                if (parts.size != 2) return null
                val time = parts[0].split(":")
                val date = parts[1].split("/")
                if (time.size != 2 || date.size != 3) return null
                return listOf(time[0], time[1], date[0], date[1], date[2])
            }

            val thuc = parseTime(tg) ?: return@setOnClickListener

            val oldAwake = db.timeAwakeDao().getAll().last()
            val newAwake = oldAwake.copy(
                gio = thuc[0],
                phut = thuc[1],
                ngay = thuc[2],
                thang = thuc[3],
                nam = thuc[4]
            )
            db.timeAwakeDao().update(newAwake)

            val intent = Intent(this, SleepDetails::class.java)
            startActivity(intent)
        }

    }
}