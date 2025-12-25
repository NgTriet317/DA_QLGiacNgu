package com.example.doan_qlgiacngu

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast

class admin_MucTieu : AppCompatActivity() {
    private lateinit var spnUser: Spinner
    private lateinit var tvGoalValue: TextView
    private lateinit var seekBarGoal: SeekBar
    private lateinit var arrBack: LinearLayout
    private lateinit var btnSaveGoal: Button
    private lateinit var rgGoalType: RadioGroup
    private lateinit var dbMucTieu: AppDatabase
    private lateinit var dbLienKetMT: AppDatabase
    private lateinit var dbUser: AppDatabase

    // Trạng thái
    private var baseHour = 8            // số giờ cơ bản (theo ngày)
    private var currentMode = GoalMode.DAY

    enum class GoalMode {
        DAY, WEEK, MONTH
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_muc_tieu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setControl()
        setEvent()
    }

    private fun setControl() {
        spnUser = findViewById(R.id.spnUser)
        tvGoalValue = findViewById(R.id.tvGoalValue)
        seekBarGoal = findViewById(R.id.seekBarGoal)
        rgGoalType = findViewById(R.id.rgGoalType)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)
        arrBack = findViewById(R.id.arrBack)
    }

    private fun setEvent() {
        seekBarGoal.max = 12          // 4–16h là hợp lý
        seekBarGoal.progress = baseHour

        seekBarGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                baseHour = progress
                updateGoalText()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // RadioGroup đổi loại mục tiêu
        rgGoalType.setOnCheckedChangeListener { _, checkedId ->
            currentMode = when (checkedId) {
                R.id.rbDay -> GoalMode.DAY
                R.id.rbWeek -> GoalMode.WEEK
                R.id.rbMonth -> GoalMode.MONTH
                else -> GoalMode.DAY
            }
            updateGoalText()
        }
        dbMucTieu = AppDatabase.get(this)
        dbLienKetMT = AppDatabase.get(this)
        dbUser = AppDatabase.get(this)
        val dsUser = dbUser.userDataDao().getALL()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dsUser.map { it.userName })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        var userid = 0
        spnUser.adapter = adapter
        spnUser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var userDuocChon = dsUser[position]
                userid = userDuocChon.userid
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
        btnSaveGoal.setOnClickListener {
            Toast.makeText(
                this,
                "Đã lưu mục tiêu giấc ngủ",
                Toast.LENGTH_SHORT
            ).show()
            if (userid != 0) {
                if (dbMucTieu.muctieuDataDao().getKiemTraMucTieu(userid) == false) {
                    var mucTieu = dbMucTieu.muctieuDataDao()
                    var muctieuNgay = ""
                    var muctieuTuan = ""
                    var muctieuThang = ""
                    if (currentMode == GoalMode.DAY){
                        muctieuNgay = tvGoalValue.text.toString()
                    }
                    if (currentMode == GoalMode.WEEK) {
                        muctieuTuan = tvGoalValue.text.toString()
                    }
                    if (currentMode == GoalMode.MONTH) {
                        muctieuThang = tvGoalValue.text.toString()
                    }
                    val mt = muctieuData(userId = userid, mucTieuNgay = muctieuNgay, mucTieuTuan = muctieuTuan, mucTieuThang = muctieuThang)
                    mucTieu.insert(mt)
                }
                else {
                    val mucTieu = dbMucTieu.muctieuDataDao()
                    var mt = dbMucTieu.muctieuDataDao().getMucTieu(userid)
                    if (mt != null) {
                        var muctieuNgay = ""
                        var muctieuTuan = ""
                        var muctieuThang = ""
                        if (currentMode == GoalMode.DAY){
                            muctieuNgay = tvGoalValue.text.toString()
                            if (mt?.mucTieuNgay == "") {
                                mt?.mucTieuNgay = muctieuNgay
                            } else {
                                mt?.mucTieuNgay = tvGoalValue.text.toString()
                            }
                        }
                        if (currentMode == GoalMode.WEEK) {
                            muctieuTuan = tvGoalValue.text.toString()
                            if (mt?.mucTieuTuan == "") {
                                mt?.mucTieuTuan = muctieuTuan
                            } else {
                                mt?.mucTieuTuan = tvGoalValue.text.toString()
                            }
                        }
                        if (currentMode == GoalMode.MONTH) {
                            muctieuThang = tvGoalValue.text.toString()
                            if (mt?.mucTieuThang == "") {
                                mt?.mucTieuThang = muctieuThang
                            } else {
                                mt?.mucTieuThang = tvGoalValue.text.toString()
                            }
                        }
                        mucTieu.update(mt)
                    }
                }
            }
        }

        arrBack.setOnClickListener { finish() }

    }

    // =========================
    // CẬP NHẬT TEXT MỤC TIÊU
    // =========================
    private fun updateGoalText() {
        val resultText = when (currentMode) {
            GoalMode.DAY -> {
                "$baseHour giờ / ngày"
            }

            GoalMode.WEEK -> {
                val total = baseHour * 7
                "$total giờ / tuần"
            }

            GoalMode.MONTH -> {
                val total = baseHour * 30
                "$total giờ / tháng"
            }
        }
        tvGoalValue.text = resultText
    }
}