package com.example.financeapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.model.Category
import com.example.financeapp.model.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtAmount: EditText
    private lateinit var spinnerCategory: Spinner

    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private var transactionId: Long = 0
    private var userId: Long = 0

    private var transactionDate = ""
    private var transactionType = ""

    private var categoryList: List<Category> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_transaction)

        edtTitle =
            findViewById(R.id.edtTitle)

        edtAmount =
            findViewById(R.id.edtAmount)

        spinnerCategory =
            findViewById(R.id.spinnerCategory)

        btnUpdate =
            findViewById(R.id.btnUpdate)

        btnDelete =
            findViewById(R.id.btnDelete)

        // GET DATA

        transactionId =
            intent.getLongExtra("id", 0)

        userId =
            intent.getLongExtra("userId", 0)

        val title =
            intent.getStringExtra("title")

        val amount =
            intent.getDoubleExtra("amount", 0.0)

        transactionDate =
            intent.getStringExtra("date") ?: ""

        transactionType =
            intent.getStringExtra("type") ?: "expense"

        // SET DATA

        edtTitle.setText(title)

        edtAmount.setText(
            amount.toString()
        )

        // LOAD CATEGORY

        loadCategories()

        // UPDATE

        btnUpdate.setOnClickListener {

            updateTransaction()
        }

        // DELETE

        btnDelete.setOnClickListener {

            deleteTransaction()
        }
    }

    private fun loadCategories() {

        RetrofitClient.api
            .getCategories(userId)
            .enqueue(object : Callback<List<Category>> {

                override fun onResponse(
                    call: Call<List<Category>>,
                    response: Response<List<Category>>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        categoryList =
                            response.body()!!

                        val names =
                            categoryList.map { it.name }

                        val adapter =
                            ArrayAdapter(
                                this@EditTransactionActivity,
                                android.R.layout.simple_spinner_item,
                                names
                            )

                        adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                        )

                        spinnerCategory.adapter =
                            adapter

                        // SET CURRENT CATEGORY

                        val currentCategoryId =
                            intent.getLongExtra(
                                "categoryId",
                                0
                            )

                        val position =
                            categoryList.indexOfFirst {

                                it.id == currentCategoryId
                            }

                        if (position != -1) {

                            spinnerCategory.setSelection(position)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<List<Category>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@EditTransactionActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun updateTransaction() {

        // VALIDATE

        if (
            edtTitle.text.toString()
                .trim()
                .isEmpty()
        ) {

            edtTitle.error =
                "Enter title"

            return
        }

        if (
            edtAmount.text.toString()
                .trim()
                .isEmpty()
        ) {

            edtAmount.error =
                "Enter amount"

            return
        }

        if (categoryList.isEmpty()) {

            Toast.makeText(
                this,
                "No category",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val selectedCategory =
            categoryList[
                spinnerCategory.selectedItemPosition
            ]

        val transaction = Transaction(

            transactionId,

            edtTitle.text.toString(),

            edtAmount.text.toString()
                .toDouble(),

            // TYPE
            transactionType,

            // DATE
            transactionDate,

            // USER ID
            userId,

            // CATEGORY ID
            selectedCategory.id ?: 0
        )

        RetrofitClient.api
            .updateTransaction(
                transactionId,
                transaction
            )
            .enqueue(object : Callback<Transaction> {

                override fun onResponse(
                    call: Call<Transaction>,
                    response: Response<Transaction>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@EditTransactionActivity,
                            "Updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@EditTransactionActivity,
                            "Update failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<Transaction>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@EditTransactionActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun deleteTransaction() {

        RetrofitClient.api
            .deleteTransaction(transactionId)
            .enqueue(object : Callback<Void> {

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {

                    Toast.makeText(
                        this@EditTransactionActivity,
                        "Deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }

                override fun onFailure(
                    call: Call<Void>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@EditTransactionActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}