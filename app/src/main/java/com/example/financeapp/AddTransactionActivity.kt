package com.example.financeapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.model.Category
import com.example.financeapp.model.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtAmount: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnAdd: Button
    private lateinit var btnBack: TextView  // thêm mới

    private var categoryList: List<Category> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_transaction)

        edtTitle = findViewById(R.id.edtTitle)
        edtAmount = findViewById(R.id.edtAmount)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnAdd = findViewById(R.id.btnAdd)
        btnBack = findViewById(R.id.btnBack)  // thêm mới

        loadCategories()

        btnAdd.setOnClickListener {
            addTransaction()
        }

        // thêm mới — giữ nguyên logic, chỉ đóng màn hình
        btnBack.setOnClickListener {
            finish()
        }
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

                        categoryList = response.body()!!

                        if (categoryList.isEmpty()) {
                            Toast.makeText(
                                this@AddTransactionActivity,
                                "Please add category first",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }

                        val names = categoryList.map { it.name }

                        val adapter = ArrayAdapter(
                            this@AddTransactionActivity,
                            android.R.layout.simple_spinner_item,
                            names
                        )

                        adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                        )

                        spinnerCategory.adapter = adapter
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    Toast.makeText(
                        this@AddTransactionActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun addTransaction() {

        val title = edtTitle.text.toString().trim()
        val amountText = edtAmount.text.toString().trim()

        if (title.isEmpty()) {
            edtTitle.error = "Enter title"
            return
        }

        if (amountText.isEmpty()) {
            edtAmount.error = "Enter amount"
            return
        }

        if (categoryList.isEmpty()) {
            Toast.makeText(this, "No category found", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("MyApp", MODE_PRIVATE)

        val userId = sharedPreferences.getLong("userId", 0)

        val selectedCategory = categoryList[spinnerCategory.selectedItemPosition]

        val transaction = Transaction(
            null,
            title,
            amountText.toDouble(),
            selectedCategory.type,       // TYPE AUTO FROM CATEGORY
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            userId,
            selectedCategory.id ?: 0
        )

        RetrofitClient.api
            .addTransaction(transaction)
            .enqueue(object : Callback<Transaction> {

                override fun onResponse(
                    call: Call<Transaction>,
                    response: Response<Transaction>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddTransactionActivity,
                            "Transaction Added",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddTransactionActivity,
                            "Add Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Transaction>, t: Throwable) {
                    Toast.makeText(
                        this@AddTransactionActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
