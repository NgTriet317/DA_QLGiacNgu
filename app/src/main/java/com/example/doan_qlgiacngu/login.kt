package com.example.doan_qlgiacngu

import android.content.Intent
import android.database.Cursor
import android.graphics.Paint
import android.graphics.Point
import android.os.Bundle
import android.view.PointerIcon
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class login : AppCompatActivity() {

    lateinit var tvRegister: TextView
    lateinit var appDB: AppDatabase
    lateinit var btnLogin: MaterialButton
    lateinit var email: TextInputEditText
    lateinit var tilEmail: com.google.android.material.textfield.TextInputLayout
    lateinit var tilPassword: com.google.android.material.textfield.TextInputLayout
    lateinit var password: TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setControl()
        setEvent()

    }

    private fun setControl() {
        tvRegister = findViewById(R.id.tvRegister)
        btnLogin = findViewById(R.id.btnLogin)
        email = findViewById(R.id.edtEmail)
        password = findViewById(R.id.edtPassword)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
    }

    private fun setEvent() {
        appDB = AppDatabase.get(this)

        tvRegister.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                val intent = Intent(this@login, signup::class.java)
                startActivity(intent)
            }
        }

        btnLogin.setOnClickListener {
            val e = email.text.toString().trim()
            val p = password.text.toString().trim()

            val user = appDB.userDataDao().getByEmail(e)

            if (e.isEmpty()) {
                tilEmail.error = "Vui lòng nhập Email"
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                tilEmail.error = "Định dạng Email không hợp lệ (thiếu @ hoặc sai tên miền)"
            } else if (user == null) {
                tilEmail.error = "Email không tồn tại"
            } else {
                tilEmail.error = null
            }

            if (p.isEmpty()) {
                tilPassword.error = "Vui lòng nhập mật khẩu"
            } else if (user?.password != p) {
                tilPassword.error = "Mật khẩu không đúng"
            } else {
                tilPassword.error = null
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                user.login = true
                appDB.userDataDao().update(user)
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            }


        }
    }

}
