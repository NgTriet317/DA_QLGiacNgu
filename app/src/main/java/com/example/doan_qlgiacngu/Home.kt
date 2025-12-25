package com.example.doan_qlgiacngu


import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class Home : AppCompatActivity() {
    private var tvname: TextView? = null
    private var tvemail: TextView? = null
    private var tvHellouser: TextView? = null

    lateinit var layoutAlarm: LinearLayout

    lateinit var appDB: AppDatabase

    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var tvNgu: TextView
    lateinit var tvThuc: TextView

    lateinit var rdiOption1: RadioButton
    lateinit var rdiOption2: RadioButton
    lateinit var rdiOption3: RadioButton

    lateinit var switchBaoThuc: Switch

    lateinit var cbBaoThuc: CheckBox
    lateinit var btnNgu: Button

    lateinit var ringtoneLauncher: ActivityResultLauncher<Intent>
    var alarmSoundUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setvontrol()
        setEvent()
    }

    private fun setvontrol() {
        // 1. Phải findViewById cho navView trước
        drawer = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)

        // 2. Sau đó mới lấy Header View từ navView
        val headerView: View = navView.getHeaderView(0)
        tvname = headerView.findViewById(R.id.tvname)
        tvemail = headerView.findViewById(R.id.tvemail)
        tvHellouser = headerView.findViewById(R.id.tvHellouser)

        tvNgu = findViewById(R.id.tvNgu)
        tvThuc = findViewById(R.id.tvThuc)

        rdiOption1 = findViewById(R.id.rbAmThanh)
        rdiOption2 = findViewById(R.id.rbRung)
        rdiOption3 = findViewById(R.id.rbCaHai)

        switchBaoThuc = findViewById(R.id.switchAlarm)
        cbBaoThuc = findViewById(R.id.cbBaoLai)
        btnNgu = findViewById(R.id.btnNgu)

        layoutAlarm = findViewById(R.id.layoutAlarm)
    }


    private fun setEvent() {
        appDB = AppDatabase.get(this)

        val user = appDB.userDataDao().getCurrentUser()
        user?.let {
            tvname?.text = "${it.userName}"
            tvemail?.text = "${it.email}"
            tvHellouser?.text = "Xin chào, ${it.userName}"
        }

        setSupportActionBar(toolbar)   // ⭐ DÒNG QUAN TRỌNG

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // Trước khi lắng nghe sự kiện, hãy ẩn/hiện menu item dựa trên Role
        val menu = navView.menu
        val isAdmin = appDB.userDataDao().getCurrentUser()?.role == "admin"
        menu.findItem(R.id.btnSetting)?.isVisible = isAdmin // Chỉ hiện btnSetting nếu là admin

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Nếu đang ở Home rồi thì chỉ cần đóng Drawer
                    drawer.closeDrawers()
                }

                R.id.nav_chitiet -> {
                    startActivity(Intent(this, SleepDetails::class.java))
                }

                R.id.nav_trangthai -> {
                    startActivity(Intent(this, SleepActivity::class.java))
                }

                R.id.nav_lichsu -> {
                    startActivity(Intent(this, SleepWM::class.java))
                }

                R.id.nav_thongke -> {
                    startActivity(Intent(this, SleepHistory::class.java))
                }

                R.id.nav_thongkemuctieu -> startActivity(Intent(this, XemMucTieu::class.java))

                R.id.nav_logout -> {
                    val userDao = appDB.userDataDao()
                    val user = userDao.getCurrentUser()
                    user?.let {
                        it.login = false
                        userDao.update(it)
                    }
                    val intent = Intent(this, login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }


                R.id.btnSetting -> {
                    // Dòng này thực tế sẽ không bao giờ chạy nếu role không phải admin vì item đã bị ẩn
                    startActivity(Intent(this, Setting::class.java))
                }
            }
            drawer.closeDrawers()
            true
        }


        tvNgu.setOnClickListener {
            //Lấy thời gian hiện tại
            val ngayGioHienTai = Calendar.getInstance()
            //lấy giờ và phút hiện tại
            val hour = ngayGioHienTai.get(Calendar.HOUR_OF_DAY)
            val minute = ngayGioHienTai.get(Calendar.MINUTE)
            //lấy ngày / tháng / năm hiện tại
            val day = ngayGioHienTai.get(Calendar.DAY_OF_MONTH)
            var month = ngayGioHienTai.get(Calendar.MONTH) + 1
            var year = ngayGioHienTai.get(Calendar.YEAR)

            // Tạo bảng chọn thời gian
            val chonThoiGian = TimePickerDialog(
                this,
                { _, chonHour, chonMinute ->
                    val result = "${chonHour.toString().padStart(2, '0')}:" +
                            "${chonMinute.toString().padStart(2, '0')} ${day}/${month}/${year}"
                    tvNgu.text = result
                },
                hour, minute, true // true = kiểu 24h, false = kiểu AM/PM
            )
            chonThoiGian.show()
        }



        tvThuc.setOnClickListener {
            //Lấy thời gian hiện tại
            val ngayGioHienTai = Calendar.getInstance()
            //lấy giờ và phút hiện tại
            val hour = ngayGioHienTai.get(Calendar.HOUR_OF_DAY)
            val minute = ngayGioHienTai.get(Calendar.MINUTE)


            //hiển thị bảng chọn thời gian cho textview thức
            var chonThoiGian = TimePickerDialog(
                this,
                { _, chonHour, chonMinute ->
                    //lấy thời gian ngủ đã chọn bên trên
                    val gioNguText = tvNgu.text.toString()
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


                    //tách giờ phút thành 2 phần riêng
                    val (nguHour, nguMinute) = timePart.split(":").map { it.toInt() }
                    //tách ngày tháng năm thành 3 phần riêng
                    val (nguDay, nguMonth, nguYear) = datePart.split("/").map { it.toInt() }


                    //Tạo ngayGioHienTai cho ngày thức dậy
                    val ngayGioHienTai = Calendar.getInstance()
                    //set ngày tháng năm thức dậy bằng ngày tháng năm ngủ
                    ngayGioHienTai.set(nguYear, nguMonth - 1, nguDay)
                    //Month này là lấy từ text bên trên (hiện là 11) nên phải - 1 để ra đúng tháng


                    // nếu giờ thức < giờ ngủ => qua ngày hôm sau
                    if (chonHour < nguHour || chonHour == nguHour && chonMinute < nguMinute) {
                        //Month lấy từ month hiện tại (đang là 10 vì là november) nên phải + 1 để ra đúng
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
                    tvThuc.setText(result)

                },
                hour, minute, true
            )
            chonThoiGian.show()
        }

        switchBaoThuc.setOnCheckedChangeListener { _, isChecked ->
            layoutAlarm.visibility = if (isChecked) LinearLayout.VISIBLE else LinearLayout.GONE

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // 1. Kiểm tra quyền (Giữ nguyên đoạn này của bạn là đúng)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                    Toast.makeText(this, "Vui lòng cho phép quyền Báo thức", Toast.LENGTH_LONG)
                        .show()
                    switchBaoThuc.isChecked = false // Trả switch về off nếu chưa có quyền
                    return@setOnCheckedChangeListener
                }
            }

            // 2. Xác định Mode báo thức dựa trên RadioButton
            val mode = when {
                rdiOption3.isChecked -> "BOTH"
                rdiOption2.isChecked -> "VIBRATE"
                else -> "SOUND"
            }

            // 3. Tạo Intent với đầy đủ thông tin
            // Trong Home.kt, sửa lại đoạn đặt báo thức trong setOnCheckedChangeListener
            val sharedPref = getSharedPreferences("ALARM_SETTINGS", Context.MODE_PRIVATE)
            val adminRingtone = sharedPref.getString("SELECTED_RINGTONE_URI", null)

            val intent = Intent(this, AlarmReceiver::class.java).apply {
                // Ưu tiên dùng nhạc Admin đã cài, nếu chưa có thì dùng mặc định
                putExtra("ALARM_SOUND_URI", adminRingtone)
                putExtra("ALARM_MODE", mode)
            }


            // Sử dụng FLAG_MUTABLE để đảm bảo Extra data được truyền đi chính xác
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                1001, // Nên dùng một mã định danh cố định
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            if (isChecked) {
                val calendar = getCalendarFromTvThuc() // Đảm bảo hàm này trả về đúng giờ tương lai
                if (calendar == null) {
                    Toast.makeText(this, "Hãy chọn giờ thức dậy!", Toast.LENGTH_SHORT).show()
                    switchBaoThuc.isChecked = false
                    return@setOnCheckedChangeListener
                }

                // Kiểm tra nếu thời gian đã trôi qua thì báo thức sẽ không nổ
                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    Toast.makeText(this, "Thời gian chọn phải ở tương lai!", Toast.LENGTH_SHORT)
                        .show()
                    switchBaoThuc.isChecked = false
                    return@setOnCheckedChangeListener
                }

                // 4. Đặt báo thức với độ ưu tiên cao nhất
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }

                Toast.makeText(this, "Đã đặt báo thức lúc: ${tvThuc.text}", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Hủy báo thức
                alarmManager.cancel(pendingIntent)
                Toast.makeText(this, "Đã tắt báo thức", Toast.LENGTH_SHORT).show()
            }
        }



        btnNgu.setOnClickListener {

            val gioNguText = tvNgu.text.toString()
            val gioThucText = tvThuc.text.toString()

            if (gioNguText.isEmpty() || gioThucText.isEmpty()) {
                Toast.makeText(this, "Chưa chọn đủ giờ ngủ / giờ thức", Toast.LENGTH_SHORT).show()
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


            val sleep = timeSleep(
                gio = ngu[0],
                phut = ngu[1],
                ngay = ngu[2],
                thang = ngu[3],
                nam = ngu[4]
            )

            val awake = timeAwake(
                gio = thuc[0],
                phut = thuc[1],
                ngay = thuc[2],
                thang = thuc[3],
                nam = thuc[4]
            )

            // Chèn và lấy ID trực tiếp từ hàm insert (trả về Long)
            val idsleep = appDB.timeSleepDao().insert(sleep).toInt()
            val idawake = appDB.timeAwakeDao().insert(awake).toInt()

            // TÍNH TOÁN THỜI GIAN NGỦ (tgngu) ĐỂ TRÁNH LỖI FOREIGN KEY
            val calNgu = Calendar.getInstance().apply {
                set(ngu[4].toInt(), ngu[3].toInt() - 1, ngu[2].toInt(), ngu[0].toInt(), ngu[1].toInt(), 0)
                set(Calendar.MILLISECOND, 0)
            }
            val calThuc = Calendar.getInstance().apply {
                set(thuc[4].toInt(), thuc[3].toInt() - 1, thuc[2].toInt(), thuc[0].toInt(), thuc[1].toInt(), 0)
                set(Calendar.MILLISECOND, 0)
            }
            val diffMs = calThuc.timeInMillis - calNgu.timeInMillis
            val diffHours = diffMs.toFloat() / (1000 * 60 * 60)

            // Chèn tgngu và lấy ID
            val idTgNgu = appDB.tgnguDao().insert(tgngu(tgngu = diffHours)).toInt()

            val userHT = appDB.userDataDao().getCurrentUser()

            if (userHT != null) {
                val userid = userHT.userid
                appDB.lienKetDao().insert(
                    lienKet(
                        userid = userid,
                        timeSleepId = idsleep,
                        timeAwakeId = idawake,
                        tgnguId = idTgNgu
                    )
                )
            }


            val baoThuc = if (cbBaoThuc.isChecked) "Bật báo thức" else "Tắt báo thức"
            val selectedOption = when {
                rdiOption1.isChecked -> rdiOption1.text
                rdiOption2.isChecked -> rdiOption2.text
                rdiOption3.isChecked -> rdiOption3.text
                else -> "Chưa chọn"
            }

            //thông báo sau khi thực hiện
            Toast.makeText(this, "Chúc bạn ngủ ngon", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, startSleep::class.java)
            startActivity(intent)
        }
    }

    private fun getCalendarFromTvThuc(): Calendar? {
        val text = tvThuc.text.toString()
        if (text.isEmpty()) return null

        // format: HH:mm dd/MM/yyyy
        val parts = text.split(" ")
        val (hour, minute) = parts[0].split(":").map { it.toInt() }
        val (day, month, year) = parts[1].split("/").map { it.toInt() }

        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
    }
}
