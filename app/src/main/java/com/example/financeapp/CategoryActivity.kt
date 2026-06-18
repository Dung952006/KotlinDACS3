package com.example.financeapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp.adapter.CategoryAdapter
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.model.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryActivity : AppCompatActivity() {

    private lateinit var edtCategoryName: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var btnAddCategory: Button
    private lateinit var recyclerCategories: RecyclerView
    private lateinit var btnBack: TextView  // thêm mới

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_category)

        edtCategoryName = findViewById(R.id.edtCategoryName)
        spinnerType = findViewById(R.id.spinnerType)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        recyclerCategories = findViewById(R.id.recyclerCategories)
        btnBack = findViewById(R.id.btnBack)  // thêm mới

        recyclerCategories.layoutManager = LinearLayoutManager(this)

        val types = arrayOf("income", "expense")

        spinnerType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        btnAddCategory.setOnClickListener {
            addCategory()
        }

        btnBack.setOnClickListener {  // thêm mới
            finish()
        }

        loadCategories()
    }

    private fun loadCategories() {

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("MyApp", MODE_PRIVATE)

        val userId = sharedPreferences.getLong("userId", 0)

        RetrofitClient.api
            .getCategories(userId)
            .enqueue(object : Callback<List<Category>> {

                override fun onResponse(
                    call: Call<List<Category>>,
                    response: Response<List<Category>>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val categoryList = response.body()!!

                        val adapter = CategoryAdapter(categoryList) { category ->
                            deleteCategory(category.id ?: 0)
                        }

                        recyclerCategories.adapter = adapter
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    Toast.makeText(
                        this@CategoryActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun addCategory() {

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("MyApp", MODE_PRIVATE)

        val userId = sharedPreferences.getLong("userId", 0)

        val category = Category(
            null,
            edtCategoryName.text.toString(),
            spinnerType.selectedItem.toString(),
            userId
        )

        RetrofitClient.api
            .addCategory(category)
            .enqueue(object : Callback<Category> {

                override fun onResponse(
                    call: Call<Category>,
                    response: Response<Category>
                ) {
                    Toast.makeText(
                        this@CategoryActivity,
                        "Added",
                        Toast.LENGTH_SHORT
                    ).show()

                    edtCategoryName.setText("")
                    loadCategories()
                }

                override fun onFailure(call: Call<Category>, t: Throwable) {
                    Toast.makeText(
                        this@CategoryActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun deleteCategory(id: Long) {

        RetrofitClient.api
            .deleteCategory(id)
            .enqueue(object : Callback<Void> {

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Toast.makeText(
                        this@CategoryActivity,
                        "Deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadCategories()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        this@CategoryActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
