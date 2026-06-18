package com.example.financeapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeapp.R
import com.example.financeapp.adapter.BudgetAdapter
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.databinding.FragmentBudgetBinding
import com.example.financeapp.model.BudgetRequest
import com.example.financeapp.model.BudgetResponse
import com.example.financeapp.model.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private var categoryList = mutableListOf<Category>()

    private var userId: Long = 0

    private var month = 0
    private var year = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentBudgetBinding.inflate(
                inflater,
                container,
                false
            )

        val sharedPreferences =
            requireActivity()
                .getSharedPreferences(
                    "MyApp",
                    Context.MODE_PRIVATE
                )

        userId =
            sharedPreferences.getLong(
                "userId",
                0
            )

        val calendar =
            Calendar.getInstance()

        month =
            calendar.get(Calendar.MONTH) + 1

        year =
            calendar.get(Calendar.YEAR)

        binding.recyclerBudget.layoutManager =
            LinearLayoutManager(requireContext())

        loadCategories()

        loadBudgets()

        binding.btnAddBudget.setOnClickListener {

            showAddBudgetDialog()
        }

        return binding.root
    }

    // =========================
    // LOAD BUDGETS
    // =========================

    private fun loadBudgets() {

        RetrofitClient.api
            .getBudgets(
                userId,
                month,
                year
            )

            .enqueue(object :
                Callback<List<BudgetResponse>> {

                override fun onResponse(
                    call: Call<List<BudgetResponse>>,
                    response: Response<List<BudgetResponse>>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        val budgetList =
                            response.body()!!

                        binding.recyclerBudget.adapter =
                            BudgetAdapter(
                                budgetList
                            )

                    } else {

                        Toast.makeText(
                            requireContext(),
                            "Cannot load budgets",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<BudgetResponse>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // =========================
    // LOAD CATEGORY
    // =========================

    private fun loadCategories() {

        RetrofitClient.api
            .getCategories(userId)

            .enqueue(object :
                Callback<List<Category>> {

                override fun onResponse(
                    call: Call<List<Category>>,
                    response: Response<List<Category>>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        categoryList.clear()

                        categoryList.addAll(

                            response.body()!!.filter {

                                it.type == "EXPENSE"
                            }
                        )
                    }
                }

                override fun onFailure(
                    call: Call<List<Category>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // =========================
    // SHOW ADD BUDGET DIALOG
    // =========================

    private fun showAddBudgetDialog() {

        if (categoryList.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "No category found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val view =
            layoutInflater.inflate(
                R.layout.dialog_add_budget,
                null
            )

        val spinner =
            view.findViewById<Spinner>(
                R.id.spinnerCategory
            )

        val edtAmount =
            view.findViewById<EditText>(
                R.id.edtAmount
            )

        val categoryNames =
            categoryList.map {
                it.name
            }

        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categoryNames
            )

        spinner.adapter = adapter

        val dialog =
            AlertDialog.Builder(requireContext())
                .setTitle("Add Budget")
                .setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {

                val amountText =
                    edtAmount.text.toString()

                if (amountText.isEmpty()) {

                    Toast.makeText(
                        requireContext(),
                        "Enter amount",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val amount =
                    amountText.toDouble()

                if (amount <= 0) {

                    Toast.makeText(
                        requireContext(),
                        "Budget must be greater than 0",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val selectedCategory =
                    categoryList[
                        spinner.selectedItemPosition
                    ]

                saveBudget(
                    selectedCategory.id!!,
                    amount
                )

                dialog.dismiss()
            }
    }

    // =========================
    // SAVE BUDGET
    // =========================

    private fun saveBudget(
        categoryId: Long,
        amount: Double
    ) {

        val request =
            BudgetRequest(

                userId = userId,

                categoryId = categoryId,

                month = month,

                year = year,

                amount = amount,

                carryOver = true
            )

        RetrofitClient.api
            .saveBudget(request)

            .enqueue(object :
                Callback<Void> {

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            requireContext(),
                            "Budget Saved",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadBudgets()

                    } else {

                        val error =
                            response.errorBody()?.string()

                        Toast.makeText(
                            requireContext(),
                            "Error ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        android.util.Log.e(
                            "BUDGET_ERROR",
                            error ?: "No error body"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<Void>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}