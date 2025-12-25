package com.example.doan_qlgiacngu

import android.R.attr.duration
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Half.toFloat
import android.util.Log.i
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.nio.file.Files.size
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class SleepWM : AppCompatActivity() {

    // 1. Class dữ liệu database
    data class giongu(val ngayThang: LocalDateTime, val soGio: Double)

    // 2. Class dữ liệu hiển thị
    private var tvname: TextView? = null
    private var tvemail: TextView? = null
    private var tvHellouser: TextView? = null
    data class StatItem(val title: String, val detail: String)

    lateinit var db: AppDatabase
    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var tvtuan: TextView
    lateinit var tvthang: TextView
    lateinit var tvSelectedInfo: TextView
    lateinit var lvStats: ListView

    lateinit var adapter: ArrayAdapter<StatItem>
    private val listData = mutableListOf<StatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sleep_wm)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tktuanthang)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setcontrol()
        seteven()
        updateData("tuan")
    }

    private fun setcontrol() {
        drawer = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        val headerView: View = navView.getHeaderView(0)
        tvname = headerView.findViewById(R.id.tvname)
        tvemail = headerView.findViewById(R.id.tvemail)
        tvHellouser = headerView.findViewById(R.id.tvHellouser)
        tvtuan = findViewById(R.id.tvtuan)
        tvthang = findViewById(R.id.tvthang)
        tvSelectedInfo = findViewById(R.id.tvSelectedInfo)
        lvStats = findViewById(R.id.lvStats)

        adapter = object : ArrayAdapter<StatItem>(this, android.R.layout.simple_list_item_1, listData) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text = view.findViewById<TextView>(android.R.id.text1)
                text.text = getItem(position)?.title
                text.setTextColor(Color.WHITE)
                text.textSize = 18f
                text.gravity = android.view.Gravity.CENTER
                return view
            }
        }
        lvStats.adapter = adapter
    }

    private fun seteven() {
        db = AppDatabase.get(this)
        val user = db.userDataDao().getCurrentUser()
        user?.let {
            tvname?.text = "${it.userName}"
            tvemail?.text = "${it.email}"
            tvHellouser?.text = "Xin chào, ${it.userName}"
        }
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val isAdmin = db.userDataDao().getCurrentUser()?.role == "admin"
        navView.menu.findItem(R.id.btnSetting)?.isVisible = isAdmin

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, Home::class.java))
                R.id.nav_chitiet -> startActivity(Intent(this, SleepDetails::class.java))
                R.id.nav_trangthai -> startActivity(Intent(this, SleepActivity::class.java))
                R.id.nav_lichsu -> startActivity(Intent(this, SleepWM::class.java))
                R.id.nav_thongke -> startActivity(Intent(this, SleepHistory::class.java))
                R.id.nav_thongkemuctieu -> startActivity(Intent(this, XemMucTieu::class.java))
                R.id.nav_logout -> {
                    val user = db.userDataDao().getCurrentUser()
                    user?.let { it.login = false; db.userDataDao().update(it) }
                    val intent = Intent(this, login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                R.id.btnSetting -> startActivity(Intent(this, MainActivity::class.java))
            }
            drawer.closeDrawers()
            true
        }

        tvtuan.setOnClickListener {
            tvtuan.setBackgroundColor(Color.parseColor("#9C37D3"))
            tvthang.background = ContextCompat.getDrawable(this, R.drawable.vienmau)
            updateData("tuan")
        }

        tvthang.setOnClickListener {
            tvthang.setBackgroundColor(Color.parseColor("#9C37D3"))
            tvtuan.background = ContextCompat.getDrawable(this, R.drawable.vienmau)
            updateData("thang")
        }

        lvStats.setOnItemClickListener { _, _, position, _ ->
            val item = listData[position]
            tvSelectedInfo.text = item.detail
        }
    }

    private fun getDuLieuTuDB(): List<giongu> {
        val layid = db.userDataDao().getCurrentUser()
        val uid = layid?.userid ?: return emptyList() // Kiểm tra null an toàn hơn
        val lienKet = db.lienKetDao().getByUser(uid)

        // Kiểm tra nếu không tìm thấy liên kết thì trả về danh sách rỗng
        if (lienKet == null) return emptyList()

        val dbngu = db.timeSleepDao().getTimeSleepByUser(uid)
        val dbthuc = db.timeAwakeDao().getTimeAwakeByUser(uid)

        // Lấy size nhỏ nhất để tránh lỗi IndexOutOfBounds
        val size = minOf(dbngu.size, dbthuc.size)

        val arr = mutableListOf<giongu>()

        for (i in 0 until size) {
            try {
                // 1. Lấy dữ liệu giờ/phút
                val itemNgu = dbngu[i]
                val itemThuc = dbthuc[i]

                // 2. Quy đổi tất cả ra PHÚT để tính toán chính xác
                val phutDiNgu = itemNgu.gio.toInt() * 60 + itemNgu.phut.toInt()
                val phutThucDay = itemThuc.gio.toInt() * 60 + itemThuc.phut.toInt()

                var diffMinutes = phutThucDay - phutDiNgu

                // Nếu qua ngày hôm sau (ví dụ ngủ 22h, dậy 6h -> diff âm)
                if (diffMinutes < 0) {
                    diffMinutes += 1440 // Cộng thêm 24 giờ (24 * 60 = 1440 phút)
                }

                // 3. Đổi lại ra giờ (số thực)
                val duration = diffMinutes / 60.0

                // 4. Tạo đối tượng ngày tháng (lấy theo ngày thức dậy)
                val date = LocalDateTime.of(
                    itemThuc.nam.toInt(), itemThuc.thang.toInt(), itemThuc.ngay.toInt(),
                    itemThuc.gio.toInt(), itemThuc.phut.toInt()
                )

                arr.add(giongu(date, duration))

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return arr.sortedBy { it.ngayThang }
    }

    private fun updateData(type: String) {
        val arr = getDuLieuTuDB()
        listData.clear()
        tvSelectedInfo.text = "Chọn một dòng để xem chi tiết"

        if (arr.isEmpty()) {
            listData.add(StatItem("Chưa có dữ liệu", "Vui lòng thêm dữ liệu giấc ngủ"))
            adapter.notifyDataSetChanged()
            return
        }

        if (type == "tuan") {


            val weekFields = WeekFields.of(DayOfWeek.MONDAY, 1)

            val groups = arr.groupBy {
                Pair(it.ngayThang.get(weekFields.weekBasedYear()), it.ngayThang.get(weekFields.weekOfWeekBasedYear()))
            }

            var count = 1
            for ((key, items) in groups) {
                val tongTuan = items.sumOf { it.soGio }
                val tb = tongTuan / 7.0

                val minGio = items.minOf { it.soGio }
                val maxGio = items.maxOf { it.soGio }

                val title = "Tuần $count"
                val detail = """
                    TUẦN $count:
                    • Thời Gian Ngủ Trung bình: ${String.format("%.1fH", tb)}
                    • Tổng cộng: ${String.format("%.1fH", tongTuan)}
                    • Ngủ ít nhất: ${String.format("%.1fH", minGio)}
                    • Ngủ nhiều nhất: ${String.format("%.1fH", maxGio)}
                """.trimIndent()

                listData.add(StatItem(title, detail))
                count++
            }
        } else {
            val groups = arr.groupBy { Pair(it.ngayThang.monthValue, it.ngayThang.year) }
            for ((key, items) in groups) {
                val tongGioThang = items.sumOf { it.soGio }
                val soNgayTrongThang = items[0].ngayThang.toLocalDate().lengthOfMonth()
                val tb = tongGioThang / soNgayTrongThang.toDouble()

                val minGio = items.minOf { it.soGio }
                val maxGio = items.maxOf { it.soGio }

                val title = "Tháng ${key.first}/${key.second}"
                val detail = """
                    THÁNG ${key.first}/${key.second}:
                    • Thời Gian Ngủ Trung bình: ${String.format("%.1fH", tb)}
                    • Tổng cộng: ${String.format("%.1fH", tongGioThang)}
                    • Ngủ ít nhất: ${String.format("%.1fH", minGio)}
                    • Ngủ nhiều nhất: ${String.format("%.1fH", maxGio)}
                """.trimIndent()

                listData.add(StatItem(title, detail))
            }
        }

        listData.reverse()
        adapter.notifyDataSetChanged()
    }
}