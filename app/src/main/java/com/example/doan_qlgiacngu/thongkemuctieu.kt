package com.example.doan_qlgiacngu

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log.e
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import kotlin.math.ceil

class thongkemuctieu : AppCompatActivity() {

    private var tvname: TextView? = null
    private var tvemail: TextView? = null
    private var tvHellouser: TextView? = null
    lateinit var tvngay: TextView
    lateinit var tvtuan: TextView
    lateinit var tvthang: TextView
    lateinit var tvhienthi: TextView
    lateinit var arrBack: LinearLayout

    // List chứa 10 ô vuông hiển thị mức độ hoàn thành
    var listTextViews = ArrayList<TextView>()

    data class giongu(val ngayThang: LocalDateTime, val soGio: Double)

    lateinit var db: AppDatabase

    // Đặt mục tiêu mặc định là 8 tiếng
    val TARGET_DEFAULT = 8.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_thongkemuctieu)

        // Xử lý Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setcontrol()
        seteven()

        // Mặc định hiển thị Ngày khi vừa vào màn hình
        highlightTab(tvngay, tvtuan, tvthang)
        layngayhientai()
    }

    private fun setcontrol() {
        tvngay = findViewById(R.id.tvngay)
        tvtuan = findViewById(R.id.tvtuan)
        tvthang = findViewById(R.id.tvthang)
        tvhienthi = findViewById(R.id.tvhienthi)
        arrBack = findViewById(R.id.arrBack)

        // Lấy các ô vuông hiển thị thống kê (LinearLayout chứa các TextView nhỏ)
        val container = findViewById<LinearLayout>(R.id.linearLayout4)
        listTextViews.clear()
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is TextView) {
                listTextViews.add(view)
            }
        }
    }

    private fun seteven() {
        db = AppDatabase.get(this)

        arrBack.setOnClickListener {
            finish()
        }

        // Sự kiện click chuyển tab (Ngày/Tuần/Tháng)
        tvngay.setOnClickListener {
            highlightTab(tvngay, tvtuan, tvthang)
            layngayhientai()
        }
        tvtuan.setOnClickListener {
            highlightTab(tvtuan, tvngay, tvthang)
            laytuan()
        }
        tvthang.setOnClickListener {
            highlightTab(tvthang, tvngay, tvtuan)
            laythang()
        }
    }

    // --- CÁC HÀM HELPER & LOGIC (Phải nằm trong class thongkemuctieu) ---

    private fun highlightTab(active: TextView, vararg inactive: TextView) {
        active.setBackgroundColor(Color.parseColor("#9C37D3"))
        inactive.forEach {
            it.background = ContextCompat.getDrawable(this, R.drawable.vienmau)
        }
    }

    // 1. NGÀY: Tổng giờ ngủ hôm nay
    private fun layngayhientai() {

        val layid = db.userDataDao().getCurrentUser()
        val uid = layid?.userid ?: return
        db.lienKetDao().getByUser(uid) ?: return
        val mtngay= db.muctieuDataDao().getMucTieu(uid)?.mucTieuNgay.toString().split(" ")[0].toDouble()
        val listngay = gettime()
        val today = java.time.LocalDate.now()

        val listHomNay = listngay.filter { it.ngayThang.toLocalDate().isEqual(today) }
        val tongGio = listHomNay.sumOf { it.soGio }

        hienThiKetQua("NGÀY", tongGio, mtngay)
    }

    // 2. TUẦN: Luôn chia cho 7
    private fun laytuan() {
        val layid = db.userDataDao().getCurrentUser()
        val uid = layid?.userid ?: return
        db.lienKetDao().getByUser(uid) ?: return
        val mttuan= db.muctieuDataDao().getMucTieu(uid)?.mucTieuTuan.toString().split(" ")[0].toDouble()
        val listAll = gettime()
        val weekFields = WeekFields.of(DayOfWeek.MONDAY, 1)

        val groups = listAll.groupBy {
            Pair(
                it.ngayThang.get(weekFields.weekBasedYear()),
                it.ngayThang.get(weekFields.weekOfWeekBasedYear())
            )
        }

        val today = LocalDateTime.now()
        val currentKey = Pair(
            today.get(weekFields.weekBasedYear()),
            today.get(weekFields.weekOfWeekBasedYear())
        )

        val listTuanNay = groups[currentKey] ?: emptyList()
        val tongGioTuan = listTuanNay.sumOf { it.soGio }

        // Tính trung bình mỗi ngày trong tuần

        hienThiKetQua("TUẦN (TB)", tongGioTuan, mttuan)
    }

    // 3. THÁNG: Chia cho tổng số ngày trong tháng
    private fun laythang() {
        val layid = db.userDataDao().getCurrentUser()
        val uid = layid?.userid ?: return
        db.lienKetDao().getByUser(uid) ?: return
        val mthang= db.muctieuDataDao().getMucTieu(uid)?.mucTieuThang.toString().split(" ")[0].toDouble()
        val listngay = gettime()
        val today = java.time.LocalDate.now()

        val listThang = listngay.filter {
            it.ngayThang.month == today.month && it.ngayThang.year == today.year
        }

        val tongGioThang = listThang.sumOf { it.soGio }

        hienThiKetQua("THÁNG (TB)", tongGioThang, mthang)
    }

    private fun hienThiKetQua(tieuDe: String, daNgu: Double, mucTieu: Double) {
        val hienThiGio = kotlin.math.round(daNgu * 10) / 10.0
        var tiendo= kotlin.math.round ((daNgu/mucTieu)*100.0)
        if(tiendo > 100){
            tiendo = 100.0
        }
        val danhGia = if (daNgu >= mucTieu) "Đạt mục tiêu" else "Cần ngủ thêm"

        val detail = """
        • THỐNG KÊ $tieuDe
        • Mục tiêu: ${mucTieu}h
        • Đã ngủ: ${hienThiGio}h
        • Đánh giá: $danhGia
        • Tiến độ: $tiendo%
    """.trimIndent()

        tvhienthi.text = detail

        // Tính toán số ô vuông cần tô màu (trên thang 10)
        val rawScore = if (mucTieu > 0) (daNgu / mucTieu) * 10 else 0.0
        val soO = ceil(rawScore).toInt()
        doinen(soO)
    }

    private fun doinen(soLuong: Int) {
        val limit = if (soLuong > 10) 10 else if (soLuong < 0) 0 else soLuong
        for (i in listTextViews.indices) {
            if (i < limit) {
                listTextViews[i].setBackgroundColor(Color.parseColor("#9C37D3"))
            } else {
                listTextViews[i].setBackgroundColor(Color.parseColor("#444444"))
            }
        }
    }

    private fun gettime(): List<giongu> {
        val layid = db.userDataDao().getCurrentUser()
        val uid = layid?.userid ?: return emptyList()
        db.lienKetDao().getByUser(uid) ?: return emptyList()

        val dbngu = db.timeSleepDao().getTimeSleepByUser(uid)
        val dbthuc = db.timeAwakeDao().getTimeAwakeByUser(uid)

        // Chỉ lấy số lượng cặp giờ ngủ/thức tương ứng
        val size = minOf(dbngu.size, dbthuc.size)
        val arr = mutableListOf<giongu>()

        for (i in 0 until size) {

            val itemNgu = dbngu[i]
            val itemThuc = dbthuc[i]

            val phutDiNgu = itemNgu.gio.toInt() * 60 + itemNgu.phut.toInt()
            val phutThucDay = itemThuc.gio.toInt() * 60 + itemThuc.phut.toInt()

            var diffMinutes = phutThucDay - phutDiNgu
            // Xử lý trường hợp ngủ qua đêm (vd: ngủ 23h, dậy 6h sáng)
            if (diffMinutes < 0) {
                diffMinutes += 1440 // +24 giờ
            }

            val duration = diffMinutes / 60.0
            val date = LocalDateTime.of(
                itemThuc.nam.toInt(), itemThuc.thang.toInt(), itemThuc.ngay.toInt(),
                itemThuc.gio.toInt(), itemThuc.phut.toInt()
            )
            arr.add(giongu(date, duration))
        }
        return arr.sortedBy { it.ngayThang }
    }
}