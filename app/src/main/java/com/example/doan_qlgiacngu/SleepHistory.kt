package com.example.doan_qlgiacngu

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.doan_qlgiacngu.AppDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SleepHistory : AppCompatActivity() {
    private var tvname: TextView? = null
    private var tvemail: TextView? = null
    private var tvHellouser: TextView? = null
    lateinit var dbSleep: AppDatabase
    lateinit var dbAwake: AppDatabase
    lateinit var dbSleep_Extra: AppDatabase
    lateinit var dbUser: AppDatabase
    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var tvTongGioNgu: TextView
    lateinit var tvGioDiNgu: TextView
    lateinit var tvGioThucDay: TextView
    lateinit var tvDiemGioNgu: TextView
    lateinit var chart: LineChart
    lateinit var tvNgu: TextView
    lateinit var tvThayDoiTuThe: TextView
    lateinit var tvThucDayNgan: TextView
    lateinit var tvRemManh: TextView
    lateinit var chonNgay: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sleep_history)
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
        tvTongGioNgu = findViewById(R.id.tvTongGioNgu)
        tvGioDiNgu = findViewById(R.id.tvGioDiNgu)
        tvGioThucDay = findViewById(R.id.tvGioThucDay)
        tvDiemGioNgu = findViewById(R.id.tvDiemGioNgu)
        chart = findViewById(R.id.sleepChart)
        tvNgu = findViewById(R.id.tvNgu)
        tvThayDoiTuThe = findViewById(R.id.tvThayDoiTuThe)
        tvThucDayNgan = findViewById(R.id.tvThucDayNgan)
        tvRemManh = findViewById(R.id.tvRemManh)
        chonNgay = findViewById(R.id.chonNgay)
    }

    private fun randomChart(num: Int): List<Float> {
        return List(num) { (10..90).random().toFloat() }
    }

    private fun listToString(list: List<Float>): String =
        list.joinToString(",")

    private fun stringToList(data: String): List<Float> =
        data.split(",").map { it.toFloat() }

    private fun setEvent() {
        dbUser = AppDatabase.get(this)

        val user = dbUser.userDataDao().getCurrentUser()
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

        val menu = navView.menu
        val isAdmin = dbUser.userDataDao().getCurrentUser()?.role == "admin"
        menu.findItem(R.id.btnSetting)?.isVisible = isAdmin // Chỉ hiện btnSetting nếu là admin

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

                R.id.nav_thongkemuctieu -> startActivity(Intent(this, XemMucTieu::class.java))
                R.id.nav_logout -> {
                    val userDao = dbUser.userDataDao()
                    val user = userDao.getCurrentUser()
                    if (user?.login == true) {
                        user.login = false
                        userDao.update(user)
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

        // Lấy Database
        dbSleep = AppDatabase.get(this)
        dbAwake = AppDatabase.get(this)
        dbSleep_Extra = AppDatabase.get(this)
        dbUser = AppDatabase.get(this)
        val dbUser = dbUser.userDataDao().getCurrentUser()

        // Tạo biến
        var sleep: timeSleep? = null
        var awake: timeAwake? = null
        if (dbUser?.userid != null) {
            val userId = dbUser?.userid
            val dbNgay = dbSleep.timeSleepDao().getTimeSleepByUser(userId)
            // Chọn ngày
            val adapter = ArrayAdapter(
                this@SleepHistory,
                android.R.layout.simple_list_item_1,
                dbNgay.map { "${it.ngay.padStart(2, '0')}/${it.thang.padStart(2, '0')}/${it.nam}" })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            chonNgay.adapter = adapter
            // Chọn ngày mới nhất
            if (dbNgay.isNotEmpty()) {
                chonNgay.setSelection(dbNgay.size - 1)
            }
            chonNgay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val ngayDuocChon = dbNgay[position]
                    sleep = dbSleep.timeSleepDao()
                        .getById(ngayDuocChon.id)!!
                    var id = ngayDuocChon.id
                    awake = dbAwake.timeAwakeDao()
                        .getById(id)!!
                    // Lấy dữ liệu từ database gán vào cho giờ đi ngủ giờ thức
                    var chuoiGioThuc: String? = ""
                    var chuoiGioNgu: String? = ""
                    if (sleep != null && awake != null) {
                        chuoiGioNgu = "${sleep.gio.padStart(2, '0')}:${sleep.phut.padStart(2, '0')}"
                        chuoiGioThuc =
                            "${awake.gio.padStart(2, '0')}:${awake.phut.padStart(2, '0')}"
                    }
                    tvGioDiNgu.text = chuoiGioNgu
                    tvGioThucDay.text = chuoiGioThuc
                    val formatGio = DateTimeFormatter.ofPattern("HH:mm")
                    val gioNgu = LocalTime.parse(chuoiGioNgu, formatGio)
                    val gioThuc = LocalTime.parse(chuoiGioThuc, formatGio)

                    // Tính tổng thời gian ngủ
                    var tongGioNgu = Duration.between(gioNgu, gioThuc)
                    if (tongGioNgu.isNegative) {
                        tongGioNgu = tongGioNgu.plusHours(24)
                    }
                    tvTongGioNgu.text = tongGioNgu.toHours().toString() + " tiếng"


                    // Tính điểm giờ ngủ
                    if (tongGioNgu.toHours().toInt() <= 4) {
                        when (tongGioNgu.toHours().toInt()) {
                            0 -> tvDiemGioNgu.text = "10"
                            1 -> tvDiemGioNgu.text = "20"
                            2 -> tvDiemGioNgu.text = "30"
                            3 -> tvDiemGioNgu.text = "40"
                            4 -> tvDiemGioNgu.text = "50"
                        }
                    } else if (tongGioNgu.toHours().toInt() <= 7) {
                        when (tongGioNgu.toHours().toInt()) {
                            5 -> tvDiemGioNgu.text = "60"
                            6 -> tvDiemGioNgu.text = "70"
                            7 -> tvDiemGioNgu.text = "80"
                        }
                    } else if (tongGioNgu.toHours().toInt() <= 10) {
                        when (tongGioNgu.toHours().toInt()) {
                            8 -> tvDiemGioNgu.text = "90"
                            9 -> tvDiemGioNgu.text = "100"
                            10 -> tvDiemGioNgu.text = "80"
                        }
                    }
                    // Tạo dữ liệu cho các chức năng hoạt động trong đêm
                    var dayId = id

                    var extra = dbSleep_Extra.sleepExtraDao().getByDayId(id)

                    if (extra == null) {
                        // 🔰 CHỈ TẠO 1 LẦN DUY NHẤT
                        val gioThayDoiTuThe =
                            gioNgu.plusHours((1 until gioThuc.hour).random().toLong())
                        val gioThucDayNgan = gioNgu.plusHours((1..gioThuc.hour).random().toLong())
                        val remManh = gioNgu.plusHours((1..gioThuc.hour).random().toLong())

                        val chartRandom = randomChart(tongGioNgu.toHours().toInt() + 1)

                        extra = SleepExtraEntity(
                            dayId = dayId,
                            gioThayDoiTuThe = gioThayDoiTuThe.toString(),
                            gioThucDayNgan = gioThucDayNgan.toString(),
                            remManh = remManh.toString(),
                            chartData = listToString(chartRandom)
                        )

                        dbSleep_Extra.sleepExtraDao().insert(extra)
                    }
                    // Dữ liệu ảo để cộng vào gioThayDoiTuThe, gioThucDayNgan, remManh
                    tvNgu.text = tvGioDiNgu.text
                    tvThayDoiTuThe.text = extra.gioThayDoiTuThe
                    tvThucDayNgan.text = extra.gioThucDayNgan
                    tvRemManh.text = extra.remManh

                    // 🟣 1. Tạo dữ liệu giả lập (độ sâu giấc ngủ theo giờ)
                    val chartValues = stringToList(extra.chartData)

                    val entries = chartValues.mapIndexed { index, value ->
                        Entry(index.toFloat(), value)
                    }

                    // 🟣 2. Cấu hình đường biểu đồ
                    val dataSet =
                        LineDataSet(entries, "")          //Tập hợp dữ liệu cho đường biểu đồ
                    dataSet.color = Color.parseColor("#A9C8FF")       // Màu đường
                    dataSet.lineWidth = 3f                             // Độ dày của đường
                    dataSet.setDrawCircles(true)                       // Hiển thị các điểm dữ liệu
                    dataSet.circleRadius = 4f                          // Bán kính điểm
                    dataSet.setCircleColor(Color.parseColor("#A9C8FF")) // Màu điểm
                    dataSet.setDrawValues(false)                      // Ẩn giá trị Y trên điểm
                    dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER      // Đường cong mượt
                    dataSet.cubicIntensity = 0.2f                     // Mức độ cong
                    dataSet.highLightColor = Color.TRANSPARENT         // Không highlight khi chạm
                    dataSet.setDrawFilled(true)                        // Vẽ vùng đổ bóng
                    dataSet.fillColor = Color.parseColor("#A9C8FF")    // Tô màu bên dưới đường
                    dataSet.fillAlpha = 60 // Độ trong suốt của vùng


                    // 🟣 3. Áp dữ liệu vào chart
                    val lineData = LineData(dataSet) //tạo dữ liệu cho biểu đồ
                    chart.data = lineData //gắn dữ liệu vào LineChart


                    // 🟣 4. Cấu hình trục X
                    val xAxis = chart.xAxis // Tạo biến gán thuộc tính trục X của biểu đồ
                    xAxis.position = XAxis.XAxisPosition.BOTTOM // Gắn nhãn bên dưới cho trục X
                    xAxis.textColor = Color.BLACK // Màu của nhãn
                    xAxis.setDrawGridLines(false) // Không vẽ lưới dọc
                    xAxis.setDrawAxisLine(true) // Hiện trục X
                    xAxis.labelCount = entries.size// Số nhãn được vẽ dưỡi trục X
                    xAxis.textSize = 10f // Kích cỡ của nhãn
                    xAxis.axisMinimum = 0f // Giới hạn trục X từ Minimum đến Maximum
                    xAxis.axisMaximum = (entries.size).toFloat() - 1f
                    xAxis.granularity = 1f // Khoảng cách nhỏ nhất giữa 2 giá trị trên trục X
                    xAxis.isGranularityEnabled = true
                    val dsTrucX = mutableListOf<String>()
                    var startTime = gioNgu
                    for (i in 0..tongGioNgu.toHours().toInt()) {
                        dsTrucX.add("${startTime.hour}h")
                        startTime = startTime.plusHours(1)
                    }
                    xAxis.valueFormatter =
                        com.github.mikephil.charting.formatter.IndexAxisValueFormatter(dsTrucX) // Map giá trị x thành giờ


                    // 🟣 5. Cấu hình trục Y
                    val yAxisLeft: YAxis =
                        chart.axisLeft // Tạo biến gán thuộc tính trục Y bên trái của biểu đồ
                    yAxisLeft.textColor = Color.BLACK // Màu của nhãn
                    yAxisLeft.setDrawGridLines(true) //  Hiện lưới ngang của nhãn
                    yAxisLeft.axisMinimum = 0f // Giới hạn trục Y từ Minimum đến Maximum
                    yAxisLeft.axisMaximum = 100f


                    val yAxisRight: YAxis =
                        chart.axisRight // Tạo biến gán thuộc tính trục Y bên phải của biểu đồ
                    yAxisRight.isEnabled = false // Ẩn trục Y bên phải


                    // 🟣 6. Cấu hình biểu đồ tổng thể
                    chart.setTouchEnabled(false) // Tắt mọi tương tác với biểu đồ
                    chart.isDragEnabled = false //  Tắt khả năng kéo hoặc di chuyển
                    chart.setScaleEnabled(false) // Tắt zoom lên xuống theo trục X và Y
                    chart.setPinchZoom(false) // Tắt điểu khiển zoom bằng 2 ngón tay
                    chart.setDrawGridBackground(false) // Tắt việc vẽ nền của lưới
                    chart.description.isEnabled = false // Ẩn phần mô tả
                    chart.legend.isEnabled = false // Ẩn chú giải các dataset — màu, nhãn
                    chart.setBackgroundColor(Color.parseColor("#CBD5E1")) // Tạo màu cho background


                    // 🟣 7. Hiệu ứng load
                    chart.animateY(
                        1500,
                        Easing.EaseInOutCubic
                    ) //đường biểu đồ xuất hiện từ 0 → giá trị y trong 1.5 giây, mượt với easing.


                    chart.invalidate() // Cho phép chart vẽ lại
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }
}
