package com.example.doan_qlgiacngu


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.time.LocalTime
import java.time.format.DateTimeFormatter




class SleepActivity : AppCompatActivity() {
    private var tvname: TextView? = null
    private var tvemail: TextView? = null
    private var tvHellouser: TextView? = null
    lateinit var dbSleep: AppDatabase
    lateinit var dbAwake: AppDatabase


    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var TvTong: TextView
    lateinit var tvngunong: TextView
    lateinit var tvngusau: TextView
    lateinit var tvgiacmo: TextView
    lateinit var tvbatdau: TextView
    lateinit var tvSolan: TextView
    lateinit var btnPhanTich: Button
    lateinit var tvDanhap: TextView
    lateinit var tvhientong: TextView
    lateinit var tvhiennong: TextView
    lateinit var tvhiensau: TextView
    lateinit var tvhiengiac: TextView
    lateinit var tvhienbatdau: TextView
    lateinit var tvhiensolan: TextView
    lateinit var tvKetQua: TextView
    lateinit var tvhienngay: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sleep_ac)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setControl();
        setEvent();
    }




    fun setControl() {
        drawer = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)

        val headerView: View = navView.getHeaderView(0)
        tvname = headerView.findViewById(R.id.tvname)
        tvemail = headerView.findViewById(R.id.tvemail)
        tvHellouser = headerView.findViewById(R.id.tvHellouser)

        tvngunong = findViewById(R.id.tvngunong)
        tvngusau = findViewById(R.id.tvngusau)
        tvgiacmo = findViewById(R.id.tvgiacmo)
        tvbatdau = findViewById(R.id.tvbatdau)
        tvSolan = findViewById(R.id.tvSolan)
        btnPhanTich = findViewById(R.id.btnPhanTich)


        tvDanhap = findViewById(R.id.tvDanhap)
        tvKetQua = findViewById(R.id.tvKetQua)


        tvhientong = findViewById(R.id.tvhientong)
        tvhiensau = findViewById(R.id.tvhiensau)
        tvhiengiac = findViewById(R.id.tvhiengiac)
        tvhienbatdau = findViewById(R.id.tvhienbatdau)
        tvhiensolan = findViewById(R.id.tvhiensolan)
        tvhiennong = findViewById(R.id.tvhiennong)
        tvhienngay = findViewById(R.id.tvhienngay)
    }


    fun setEvent() {
        dbSleep = AppDatabase.get(this)
        dbAwake = AppDatabase.get(this)

        val user = dbSleep.userDataDao().getCurrentUser()
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
        val isAdmin = dbSleep.userDataDao().getCurrentUser()?.role == "admin"
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
                    val userDao = dbSleep.userDataDao()
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

        val TgSleep = dbSleep.timeSleepDao().getLast()
        val TgAwake = dbAwake.timeAwakeDao().getLast()

        tvhienngay.text=TgAwake.toString()

        val DemoSolan = listOf("1", "2", "3", "4")
        val rdSoLan = DemoSolan.random()




        val time1 = TgSleep.toString().split(" ")
        val time2 = TgAwake.toString().split(" ")


//cắt chuỗi theo khoảng trắng vi dinh dang dang la time + dd/mm/yyyy
        //Lấy phần giờ HH:mm
        val chuyen = DateTimeFormatter.ofPattern("HH:mm")
        val giongu = LocalTime.parse(time1[0].trim(), chuyen)
        val gioday = LocalTime.parse(time2[0].trim(), chuyen)

        // lay cho ngay
        val timeParts = TgSleep.toString().split(" ") // ["HH:mm", "dd/MM/yyyy"]
        val gioDay = timeParts[0].trim()  // HH:mm
        val ngayDay = timeParts[1].trim() // dd/MM/yyyy

        tvhienngay.text = ngayDay
        //chuyển chuỗi thành giờ hợp lệ
        var duration = java.time.Duration.between(giongu, gioday)
        //Tính khoảng thời gian giữa giờ ngủ và giờ thức


        if (duration.isNegative) {
            // Qua ngày hôm sau
            //Thêm 24 giờ để tránh âm
            duration = duration.plusHours(24)
        }
        // tong so gio  , so phut du
        val tongGio = "${duration.toHours().toString().padStart(2, '0')}:${(duration.toMinutes()%60).toString().padStart(2, '0')}"

        tvhientong.text = tongGio
        tvhiensolan.text = rdSoLan.toString()
        val tongStr = tvhientong.text.toString()
// cat date sau time khoang trang du
        val rawTime = TgSleep.gio.trim()
        val formattedTime = when {
            rawTime.length == 2 -> "$rawTime:00" //Nếu chỉ có giờ → thêm :00
            rawTime.length >= 5 -> rawTime.substring(0, 5)  // cắt chuỗi để lấy HH:mm
            else -> rawTime
        }
        tvhienbatdau.text = formattedTime
        btnPhanTich.setOnClickListener {


            val tongStr = tvhientong.text.toString()


            // Kiểm tra định dạng
            if (!tongStr.contains(":")) {
                Toast.makeText(this, "Tổng thời gian không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Chuyển tổng thời gian sang phút
            val parts = tongStr.split(":")
            val tongMin = parts[0].toInt() * 60 + parts[1].toInt()


            var nguNongMin = 0
            var nguSauMin = 0
            var giacMoMin = 0


            if (tongMin < 120) {
                //  Dưới 2 giờ: chỉ có ngủ nông
                nguNongMin = tongMin
                nguSauMin = 0
                giacMoMin = 0


                tvKetQua.text =
                    "Dựa vào dữ liệu vừa phân tích\nKết quả: Ngủ quá ngắn\nGiấc ngủ gãy không ngon\nNgủ nông chưa đủ thiếu ngủ sâu và  giấc mơ"


            } else if (tongMin < 240) {
                //  2 → dưới 4 giờ
                nguNongMin = 120
                nguSauMin = tongMin - 120
                giacMoMin = 0
                tvKetQua.text =
                    "Dựa vào dữ liệu vừa phân tích\nKết quả: Ngủ quá ngắn \nGiấc ngủ gãy không ngon \nNgủ sâu chưa đủ và thiếu giấc mơ "


            } else {


                //  Từ 4 giờ trở lên
                nguNongMin = 120
                nguSauMin = 120
                giacMoMin = tongMin - 240


                val gmGio = giacMoMin / 60


                tvKetQua.text = when {
                    gmGio in 1..2 -> {
                        "Dựa vào dữ liệu vừa phân tích\nKết quả: Ngủ còn ngắn \nGiấc ngủ còn gãy không ngon"
                    }
                    gmGio > 2 -> {
                        "Dựa vào dữ liệu vừa phân tích\nKết quả: Ngủ rất tốt\n Giấc ngủ ngon"
                    }
                    else -> {
                        "Dựa vào dữ liệu vừa phân tích\nKết quả: Ngủ rất tốt\n Giấc ngủ ngon"
                    }
                }
            }


            // Hiển thị ngủ nông
            tvhiennong.text = String.format(
                "%02d:%02d",
                nguNongMin / 60,
                nguNongMin % 60
            )
            // Hiển thị ngủ sâu
            tvhiensau.text = String.format(
                "%02d:%02d",
                nguSauMin / 60,
                nguSauMin % 60
            )
            // Hiển thị giấc mơ
            tvhiengiac.text = String.format(
                "%02d:%02d",
                giacMoMin / 60,
                giacMoMin % 60
            )
        }
    }
}
