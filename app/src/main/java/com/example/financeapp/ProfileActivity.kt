package com.example.financeapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.model.User
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText

    private lateinit var txtProfileName: TextView
    private lateinit var txtProfileEmail: TextView

    private lateinit var btnSave: MaterialButton
    private lateinit var btnLogout: MaterialButton

    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)

        txtProfileName = findViewById(R.id.txtProfileName)
        txtProfileEmail = findViewById(R.id.txtProfileEmail)

        btnSave = findViewById(R.id.btnSave)
        btnLogout = findViewById(R.id.btnLogout)

        val sp = getSharedPreferences("MyApp", MODE_PRIVATE)
        userId = sp.getLong("userId", 0)

        loadUser()

        btnSave.setOnClickListener {
            updateUser()
        }

        btnLogout.setOnClickListener {

            sp.edit().clear().apply()

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }

    private fun loadUser() {

        RetrofitClient.api.getUser(userId)
            .enqueue(object : Callback<User> {

                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        val user = response.body()!!

                        txtProfileName.text = user.name
                        txtProfileEmail.text = user.email

                        edtName.setText(user.name)
                        edtEmail.setText(user.email)
                    }
                }

                override fun onFailure(
                    call: Call<User>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ProfileActivity,
                        "Không tải được thông tin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun updateUser() {

        val user = User(
            userId,
            edtName.text.toString(),
            edtEmail.text.toString(),
            ""
        )

        RetrofitClient.api.updateUser(userId, user)
            .enqueue(object : Callback<User> {

                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {

                    Toast.makeText(
                        this@ProfileActivity,
                        "Cập nhật thành công",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadUser()
                }

                override fun onFailure(
                    call: Call<User>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ProfileActivity,
                        "Cập nhật thất bại",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}