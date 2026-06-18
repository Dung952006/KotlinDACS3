package com.example.financeapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.financeapp.adapter.SavingGoalAdapter
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.databinding.FragmentSavingGoalBinding
import com.example.financeapp.model.SavingGoal

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavingGoalFragment : Fragment() {

    private var _binding: FragmentSavingGoalBinding? = null

    private val binding get() = _binding!!

    private var userId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentSavingGoalBinding.inflate(
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

        binding.recyclerSavingGoal.layoutManager =
            LinearLayoutManager(requireContext())

        loadGoals()

        binding.btnAddGoal.setOnClickListener {

            showAddGoalDialog()
        }

        return binding.root
    }

    // =========================
    // LOAD GOALS
    // =========================

    private fun loadGoals() {

        RetrofitClient.api
            .getSavingGoals(userId)

            .enqueue(object :
                Callback<List<SavingGoal>> {

                override fun onResponse(
                    call: Call<List<SavingGoal>>,
                    response: Response<List<SavingGoal>>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        binding.recyclerSavingGoal.adapter =
                            SavingGoalAdapter(
                                response.body()!!
                            ) {

                                loadGoals()
                            }
                    }
                }

                override fun onFailure(
                    call: Call<List<SavingGoal>>,
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
    // ADD GOAL
    // =========================

    private fun showAddGoalDialog() {

        val layout =
            layoutInflater.inflate(
                android.R.layout.simple_list_item_1,
                null
            )

        val edtName =
            EditText(requireContext())

        edtName.hint =
            "Goal Name"

        val edtTarget =
            EditText(requireContext())

        edtTarget.hint =
            "Target Amount"

        val edtDeadline =
            EditText(requireContext())

        edtDeadline.hint =
            "2026-12-31"

        val container =
            android.widget.LinearLayout(
                requireContext()
            )

        container.orientation =
            android.widget.LinearLayout.VERTICAL

        container.addView(edtName)

        container.addView(edtTarget)

        container.addView(edtDeadline)

        AlertDialog.Builder(requireContext())

            .setTitle("Create Saving Goal")

            .setView(container)

            .setPositiveButton(
                "Save"
            ) { _, _ ->

                val goalName =
                    edtName.text.toString()

                val targetAmount =
                    edtTarget.text
                        .toString()
                        .toDoubleOrNull()

                if (targetAmount == null || targetAmount <= 0) {

                    Toast.makeText(
                        requireContext(),
                        "Target amount must be greater than 0",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val deadline =
                    edtDeadline.text.toString()

                if (
                    goalName.isEmpty() ||
                    targetAmount == null ||
                    deadline.isEmpty()
                ) {

                    Toast.makeText(
                        requireContext(),
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val goal =
                    SavingGoal(

                        id = null,

                        userId = userId,

                        goalName = goalName,

                        targetAmount = targetAmount,

                        currentAmount = 0.0,

                        deadline = deadline
                    )

                saveGoal(goal)
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }

    // =========================
    // SAVE GOAL
    // =========================

    private fun saveGoal(
        goal: SavingGoal
    ) {

        RetrofitClient.api
            .createSavingGoal(goal)

            .enqueue(object :
                Callback<SavingGoal> {

                override fun onResponse(
                    call: Call<SavingGoal>,
                    response: Response<SavingGoal>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            requireContext(),
                            "Goal Created",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadGoals()
                    }
                }

                override fun onFailure(
                    call: Call<SavingGoal>,
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

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}