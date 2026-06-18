package com.example.financeapp.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.financeapp.AddTransactionActivity
import com.example.financeapp.CategoryActivity
import com.example.financeapp.R
import com.example.financeapp.adapter.TransactionAdapter
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.model.Transaction
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionFragment : Fragment() {

    private lateinit var recyclerTransactions: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_transaction,
            container,
            false
        )

        recyclerTransactions = view.findViewById(R.id.recyclerTransactions)
        fabAdd = view.findViewById(R.id.fabAdd)

        recyclerTransactions.layoutManager = LinearLayoutManager(requireContext())

        loadTransactions()

        fabAdd.setOnClickListener {
            showAddMenu()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {

        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("MyApp", android.content.Context.MODE_PRIVATE)

        val userId = sharedPreferences.getLong("userId", 0)

        RetrofitClient.api.getTransactions(userId)
            .enqueue(object : Callback<List<Transaction>> {

                override fun onResponse(
                    call: Call<List<Transaction>>,
                    response: Response<List<Transaction>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        recyclerTransactions.adapter =
                            TransactionAdapter(response.body()!!)
                    } else {
                        Toast.makeText(requireContext(), "Load Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    // BOTTOM SHEET MENU
    private fun showAddMenu() {

        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyle)

        val sheetView = LayoutInflater.from(requireContext())
            .inflate(R.layout.bottom_sheet_add_menu, null)

        // Option 1: Add Transaction
        sheetView.findViewById<LinearLayout>(R.id.optionAddTransaction)
            .setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(requireContext(), AddTransactionActivity::class.java))
            }

        // Option 2: Add Category
        sheetView.findViewById<LinearLayout>(R.id.optionAddCategory)
            .setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(requireContext(), CategoryActivity::class.java))
            }

        dialog.setContentView(sheetView)
        dialog.show()
    }
}
