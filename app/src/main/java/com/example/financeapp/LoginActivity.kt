package com.example.financeapp

import android.content.Intent
import android.content.SharedPreferences
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

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoRegister: TextView  // đổi Button → TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoRegister = findViewById(R.id.btnGoRegister)  // tự động khớp TextView

        btnLogin.setOnClickListener {
            login()
        }

        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {

        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(0, "", email, password)

        RetrofitClient.api.login(user)
            .enqueue(object : Callback<User> {

                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val loggedUser = response.body()

                        val sharedPreferences: SharedPreferences =
                            getSharedPreferences("MyApp", MODE_PRIVATE)

                        sharedPreferences.edit()
                            .putLong("userId", loggedUser?.id ?: 0)
                            .apply()

                        Toast.makeText(
                            this@LoginActivity,
                            "Đăng nhập thành công!",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Email hoặc mật khẩu không đúng",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Lỗi kết nối: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
