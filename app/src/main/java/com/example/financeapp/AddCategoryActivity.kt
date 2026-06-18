package com.example.financeapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.model.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var edtCategoryName: EditText
    private lateinit var spinnerCategoryType: Spinner
    private lateinit var btnAddCategory: Button
    private lateinit var btnBack: TextView  // thêm mới

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_category)

        edtCategoryName = findViewById(R.id.edtCategoryName)
        spinnerCategoryType = findViewById(R.id.spinnerCategoryType)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        btnBack = findViewById(R.id.btnBack)  // thêm mới

        val types = arrayOf("income", "expense")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            types
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCategoryType.adapter = adapter

        btnAddCategory.setOnClickListener {
            addCategory()
        }

        btnBack.setOnClickListener {  // thêm mới
            finish()
        }
    }

    private fun addCategory() {

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("MyApp", MODE_PRIVATE)

        val userId = sharedPreferences.getLong("userId", 0)

        val category = Category(
            null,
            edtCategoryName.text.toString(),
            spinnerCategoryType.selectedItem.toString(),
            userId
        )

        RetrofitClient.api
            .addCategory(category)
            .enqueue(object : Callback<Category> {

                override fun onResponse(
                    call: Call<Category>,
                    response: Response<Category>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddCategoryActivity,
                            "Category Added",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddCategoryActivity,
                            "Add Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Category>, t: Throwable) {
                    Toast.makeText(
                        this@AddCategoryActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
