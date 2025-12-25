package com.example.doan_qlgiacngu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class XemMucTieu : AppCompatActivity() {
    private var tvname: TextView? = null
    private var tvemail: TextView? = null
    private var tvHellouser: TextView? = null

    lateinit var layoutAlarm: LinearLayout

    lateinit var drawer: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var db: AppDatabase
    lateinit var tvmttn : TextView
    lateinit var tvhmttn : TextView
    lateinit var tvcn : TextView
    lateinit var tvhcn : TextView
    lateinit var tvmtttuan : TextView
    lateinit var tvhmtttuan : TextView
    lateinit var tvcntuan : TextView
    lateinit var tvhcntuan : TextView
    lateinit var tvmtthang : TextView
    lateinit var tvhmtthang : TextView
    lateinit var tvcnthang : TextView
    lateinit var tvhcnthang : TextView
    lateinit var btnThongKe : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_xem_muc_tieu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setControl()
        setEvent()
    }
    public fun setControl(){
        drawer = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)

        // 2. Sau đó mới lấy Header View từ navView
        val headerView: View = navView.getHeaderView(0)
        tvname = headerView.findViewById(R.id.tvname)
        tvemail = headerView.findViewById(R.id.tvemail)
        tvHellouser = headerView.findViewById(R.id.tvHellouser)

        tvmttn=findViewById(R.id.tvmttn)
        tvhmttn=findViewById(R.id.tvhmttn)
        tvcn=findViewById(R.id.tvcn)
        tvhcn=findViewById(R.id.tvhcn)
        tvmtttuan=findViewById(R.id.tvmtttuan)
        tvhmtttuan=findViewById(R.id.tvhmtttuan)
        tvcntuan=findViewById(R.id.tvcntuan)
        tvhcntuan=findViewById(R.id.tvhcntuan)
        tvmtthang=findViewById(R.id.tvmtthang)
        tvhmtthang=findViewById(R.id.tvhmtthang)
        tvcnthang=findViewById(R.id.tvcnthang)
        tvhcnthang=findViewById(R.id.tvhcnthang)
        btnThongKe=findViewById(R.id.btnThongKe)

    } public fun setEvent(){
        db = AppDatabase.get(this)

        val user = db.userDataDao().getCurrentUser()
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
        val isAdmin = db.userDataDao().getCurrentUser()?.role == "admin"
        menu.findItem(R.id.btnSetting)?.isVisible = isAdmin // Chỉ hiện btnSetting nếu là admin

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, Home::class.java))
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
                R.id.nav_thongkemuctieu -> {
                    val intent = Intent(this, XemMucTieu::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    val userDao = db.userDataDao()
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

        val uid = db.userDataDao().getCurrentUser()?.userid
        val muctieu = db.muctieuDataDao().getMucTieu(uid.toString().toInt())

        val ngay= muctieu?.mucTieuNgay.toString().split(" ")[0]
        tvhcn.text=ngay + " Giờ";
        val tuan= muctieu?.mucTieuTuan.toString().split(" ")[0]
        tvhcntuan.text=tuan + " Giờ";
        val thang= muctieu?.mucTieuThang.toString().split(" ")[0]
        tvhcnthang.text=thang + " Giờ";
        btnThongKe.setOnClickListener {
            val intent = Intent(this, startSleep::class.java)
            startActivity(intent)
        }

        btnThongKe.setOnClickListener {
            val intent = Intent(this, thongkemuctieu::class.java)
            startActivity(intent)
        }
    }
}