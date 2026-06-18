package com.example.financeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnGoLogin: TextView  // thêm mới — nút quay lại login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoLogin = findViewById(R.id.btnGoLogin)  // thêm mới

        btnRegister.setOnClickListener {
            register()
        }

        // Quay lại màn login
        btnGoLogin.setOnClickListener {
            finish()  // đóng Register → tự quay về Login
        }
    }

    private fun register() {

        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(null, name, email, password)

        RetrofitClient.api.register(user)
            .enqueue(object : Callback<User> {

                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        Toast.makeText(
                            this@RegisterActivity,
                            "Đăng ký thành công!",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Đăng ký thất bại: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Lỗi kết nối: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
