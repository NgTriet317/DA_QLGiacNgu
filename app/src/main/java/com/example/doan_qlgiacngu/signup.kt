package com.example.doan_qlgiacngu

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.service.autofill.UserData
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class signup : AppCompatActivity() {
    lateinit var tvsignup: TextView
    lateinit var edtUsername: TextInputEditText
    lateinit var edtEmail: TextInputEditText
    lateinit var edtPassword: TextInputEditText
    lateinit var edtConfirmPassword: TextInputEditText

    lateinit var tilUsername: com.google.android.material.textfield.TextInputLayout
    lateinit var tilEmail: com.google.android.material.textfield.TextInputLayout
    lateinit var tilPassword: com.google.android.material.textfield.TextInputLayout
    lateinit var tilConfirmPassword: com.google.android.material.textfield.TextInputLayout


    lateinit var btnsignup: Button
    lateinit var appDBUser: AppDatabase

    lateinit var lienKet: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setControl()
        setEvent()
    }

    private fun setControl() {
        tvsignup = findViewById(R.id.tvRegister)
        edtUsername = findViewById(R.id.edtUsername)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnsignup = findViewById(R.id.btnSignUp)
        tilUsername = findViewById(R.id.tilUsername)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
    }

    private fun setEvent() {
        appDBUser = AppDatabase.get(this)

        tvsignup.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                val intent = Intent(this@signup, login::class.java)
                startActivity(intent)
            }
        }
        btnsignup.setOnClickListener {
            if (edtUsername.text.toString().isEmpty() || edtEmail.text.toString()
                    .isEmpty() || edtPassword.text.toString()
                    .isEmpty() || edtConfirmPassword.text.toString().isEmpty()
            )
            {
                tilUsername.error = "Vui lòng nhập đầy đủ thông tin"
                tilEmail.error = "Vui lòng nhập đầy đủ thông tin"
                tilPassword.error = "Vui lòng nhập đầy đủ thông tin"
                tilConfirmPassword.error = "Vui lòng nhập đầy đủ thông tin"
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.text).matches()) {
                tilEmail.error = "Định dạng Email không hợp lệ (thiếu @ hoặc sai tên miền)"
            }
            else if (edtPassword.text.toString() != edtConfirmPassword.text.toString()) {
                tilPassword.error = "Mật khẩu không khớp"
                tilConfirmPassword.error = "Mật khẩu không khớp"
            }
            else {
                tilUsername.error = null
                tilEmail.error = null
                tilPassword.error = null
                tilPassword.error = null
                tilConfirmPassword.error = null

                appDBUser.userDataDao().insert(
                    userData(
                        userName = edtUsername.text.toString(),
                        email = edtEmail.text.toString(),
                        password = edtPassword.text.toString()
                    )
                )

                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, login::class.java))
            }

        }
    }
}