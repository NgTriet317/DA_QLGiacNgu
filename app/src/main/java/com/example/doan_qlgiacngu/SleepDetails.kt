package com.example.doan_qlgiacngu


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class SleepDetails : AppCompatActivity() {
    private var tvname: TextView? = null
    private var tvemail: TextView? = null
    private var tvHellouser: TextView? = null

    lateinit var db: AppDatabase

    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var tvTGNgu: TextView
    lateinit var tvTGDay: TextView
    lateinit var tvTongTG: TextView
    lateinit var tvLoiKhuyen: TextView
    lateinit var tvChatLuong: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sleep_details)
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
        val headerView: View = navView.getHeaderView(0)
        tvname = headerView.findViewById(R.id.tvname)
        tvemail = headerView.findViewById(R.id.tvemail)
        tvHellouser = headerView.findViewById(R.id.tvHellouser)
        tvTGNgu = findViewById(R.id.tvTgNgu)
        tvTGDay = findViewById(R.id.tvTgDay)
        tvTongTG = findViewById(R.id.tvTongTG)
        tvLoiKhuyen = findViewById(R.id.tvLoiKhuyen)
        tvChatLuong = findViewById(R.id.tvChatLuong)
    }

    private fun setEvent() {
        db = AppDatabase.get(this)

        val user = db.userDataDao().getCurrentUser()
        user?.let {
            tvname?.text = "${it.userName}"
            tvemail?.text = "${it.email}"
            tvHellouser?.text = "Xin chào, ${it.userName}"
        }

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )

        val currentUser = db.userDataDao().getCurrentUser()
        val isAdmin = currentUser?.role == "admin"
        navView.menu.findItem(R.id.btnSetting)?.isVisible = isAdmin

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, Home::class.java))
                R.id.nav_chitiet -> drawer.closeDrawers()
                R.id.nav_trangthai -> startActivity(Intent(this, SleepActivity::class.java))
                R.id.nav_lichsu -> startActivity(Intent(this, SleepWM::class.java))
                R.id.nav_thongke -> startActivity(Intent(this, SleepHistory::class.java))
                R.id.nav_thongkemuctieu -> startActivity(Intent(this, XemMucTieu::class.java))
                R.id.nav_logout -> {
                    currentUser?.let {
                        it.login = false
                        db.userDataDao().update(it)
                    }
                    val intent = Intent(this, login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                R.id.btnSetting -> startActivity(Intent(this, Setting::class.java))
            }
            drawer.closeDrawers()
            true
        }

        // Lấy dữ liệu cuối cùng từ DB
        val lastSleep = db.timeSleepDao().getLast()
        val lastAwake = db.timeAwakeDao().getLast()

        tvTGNgu.text = lastSleep.toString()
        tvTGDay.text = lastAwake.toString()

        val time1 = tvTGNgu.text.split(" ")
        val time2 = tvTGDay.text.split(" ")


        val chuyen = DateTimeFormatter.ofPattern("HH:mm")
        val giongu = LocalTime.parse(time1[0].trim(), chuyen)
        val gioday = LocalTime.parse(time2[0].trim(), chuyen)


        var duration = java.time.Duration.between(giongu, gioday)

        if (duration.isNegative) {
            // Qua ngày hôm sau
            duration = duration.plusHours(24)
        }
        val tongGio = "${duration.toHours().toString().padStart(2, '0')}:${(duration.toMinutes()%60).toString().padStart(2, '0')}"
        tvTongTG.text = tongGio

        val lasttgngu = db.tgnguDao().getLast()

        lasttgngu?.tgngu = duration.toHours().toFloat()

        db.tgnguDao().update(lasttgngu!!)


        // Tính toán hiển thị tổng thời gian
        when {
            duration.toHours() < 5 -> tvChatLuong.text = "Giấc ngủ ngắn"
            duration.toHours() < 8 -> tvChatLuong.text = "Giấc ngủ trung bình"
            duration.toHours() >= 8 -> tvChatLuong.text = "Giấc ngủ hoàn hảo"
        }

        if(duration.toHours() < 5){
            val listLoiKhuyen1 = listOf<String>("1. Ngủ đúng giờ – đều đặn mỗi ngày","2. Tránh dùng điện thoại trước khi ngủ","3. Tạo không gian ngủ thoải mái")
            val listLoiKhuyen2 = listOf<String>("1. Không uống cà phê hoặc nước tăng lực buổi chiều tối","2. Không ăn quá no ngay trước khi ngủ","3. Vận động nhẹ nhàng")
            val listLoiKhuyen3 = listOf<String>("1. Hạn chế ngủ ngày quá lâu","2. Giữ tinh thần thoải mái","3. Không dùng rượu bia hoặc thuốc lá gần giờ ngủ")
            val listLoiKhuyen4 = listOf<String>("1. Tắm nước ấm trước khi ngủ 1 giờ","2. Không nằm trên giường dùng điện thoại","3. Đặt mục tiêu ngủ đủ giờ")
            val ds = listOf(listLoiKhuyen1,listLoiKhuyen2,listLoiKhuyen3,listLoiKhuyen4)
            tvLoiKhuyen.text = "Giấc ngủ quá ngắn\nNhững cách giúp cải thiện thời gian ngủ:\n"+ds.random().joinToString("\n")
        }
        else if (duration.toHours() < 8)
        {
            tvLoiKhuyen.text = "Bạn có một giấc ngủ ổn định nhưng chưa quá tốt!"
        }
        else{
            tvLoiKhuyen.text = "Bạn đã có một giấc ngủ thật ngon!"
        }

    }
}
