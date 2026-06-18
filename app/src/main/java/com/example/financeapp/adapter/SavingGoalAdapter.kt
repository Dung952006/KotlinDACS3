package com.example.financeapp.adapter

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.databinding.ItemSavingGoalBinding
import com.example.financeapp.model.SavingGoal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class SavingGoalAdapter(

    private val list: List<SavingGoal>,

    private val reload: () -> Unit

) : RecyclerView.Adapter<SavingGoalAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemSavingGoalBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemSavingGoalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.binding.txtGoalName.text =
            "🎯 ${item.goalName}"

        holder.binding.txtAmount.text =
            "${formatMoney(item.currentAmount)} / ${formatMoney(item.targetAmount)}"

        val percent =
            if (item.targetAmount > 0) {

                min(
                    100,
                    (
                            item.currentAmount /
                                    item.targetAmount * 100
                            ).toInt()
                )

            } else {
                0
            }

        holder.binding.progressGoal.progress =
            percent

        when {

            percent >= 100 -> {

                holder.binding.progressGoal.progressTintList =
                    ColorStateList.valueOf(
                        Color.parseColor("#4CAF50")
                    )
            }

            percent >= 70 -> {

                holder.binding.progressGoal.progressTintList =
                    ColorStateList.valueOf(
                        Color.parseColor("#FF9800")
                    )
            }

            else -> {

                holder.binding.progressGoal.progressTintList =
                    ColorStateList.valueOf(
                        Color.parseColor("#2196F3")
                    )
            }
        }

        val remaining =
            max(
                0.0,
                item.targetAmount -
                        item.currentAmount
            )

        if (percent >= 100) {

            holder.binding.txtPercent.text =
                "🎉 Goal Completed"

            holder.binding.txtPercent.setTextColor(
                Color.parseColor("#4CAF50")
            )

            holder.binding.txtRemaining.text =
                "Target achieved"

            holder.binding.btnAddMoney.visibility =
                View.GONE

        } else {

            holder.binding.txtPercent.text =
                "$percent%"

            holder.binding.txtPercent.setTextColor(
                Color.parseColor("#2196F3")
            )

            holder.binding.txtRemaining.text =
                "Remaining: ${formatMoney(remaining)}"

            holder.binding.btnAddMoney.visibility =
                View.VISIBLE
        }

        try {

            val sdf =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            val deadlineDate =
                sdf.parse(item.deadline)

            val currentDate =
                Date()

            val diff =
                deadlineDate.time -
                        currentDate.time

            val daysLeft =
                TimeUnit.MILLISECONDS
                    .toDays(diff)

            holder.binding.txtDeadline.text =
                when {

                    daysLeft > 30 ->
                        "🟢 $daysLeft days left"

                    daysLeft > 7 ->
                        "🟡 $daysLeft days left"

                    daysLeft >= 0 ->
                        "🔴 $daysLeft days left"

                    else ->
                        "❌ Expired"
                }

            if (daysLeft > 0 && remaining > 0) {

                val dailyNeed =
                    remaining / daysLeft

                holder.binding.txtDailyNeed.text =
                    "Need: ${formatMoney(dailyNeed)}/day"

            } else {

                holder.binding.txtDailyNeed.text =
                    ""
            }

        } catch (e: Exception) {

            holder.binding.txtDeadline.text =
                item.deadline

            holder.binding.txtDailyNeed.text =
                ""
        }

        // =====================
        // ADD MONEY
        // =====================

        holder.binding.btnAddMoney.setOnClickListener {

            if (item.currentAmount >= item.targetAmount) {

                Toast.makeText(
                    holder.itemView.context,
                    "Goal already completed",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val editText =
                EditText(holder.itemView.context)

            editText.hint =
                "Enter amount"

            AlertDialog.Builder(
                holder.itemView.context
            )
                .setTitle("Add Money")
                .setView(editText)
                .setPositiveButton(
                    "Save",
                    null
                )
                .create()
                .apply {

                    show()

                    getButton(
                        AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener {

                        val amount =
                            editText.text
                                .toString()
                                .toDoubleOrNull()

                        if (
                            amount == null ||
                            amount <= 0
                        ) {

                            Toast.makeText(
                                context,
                                "Amount must be greater than 0",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@setOnClickListener
                        }

                        RetrofitClient.api
                            .addMoneyToGoal(
                                item.id!!,
                                amount
                            )
                            .enqueue(
                                object :
                                    Callback<SavingGoal> {

                                    override fun onResponse(
                                        call: Call<SavingGoal>,
                                        response: Response<SavingGoal>
                                    ) {

                                        if (response.isSuccessful) {

                                            dismiss()

                                            Toast.makeText(
                                                context,
                                                "Money added successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            reload()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<SavingGoal>,
                                        t: Throwable
                                    ) {

                                        Toast.makeText(
                                            context,
                                            t.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                    }
                }
        }

        // =====================
        // DELETE GOAL
        // =====================

        holder.binding.btnDeleteGoal.setOnClickListener {

            AlertDialog.Builder(
                holder.itemView.context
            )
                .setTitle("Delete Goal")
                .setMessage(
                    "Are you sure you want to delete this goal?"
                )
                .setPositiveButton(
                    "Delete"
                ) { _, _ ->

                    RetrofitClient.api
                        .deleteGoal(item.id!!)
                        .enqueue(
                            object :
                                Callback<Void> {

                                override fun onResponse(
                                    call: Call<Void>,
                                    response: Response<Void>
                                ) {

                                    if (response.isSuccessful) {

                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Goal deleted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        reload()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<Void>,
                                    t: Throwable
                                ) {

                                    Toast.makeText(
                                        holder.itemView.context,
                                        t.localizedMessage
                                            ?: "Network error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                }
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .show()
        }
    }

    override fun getItemCount(): Int {

        return list.size
    }

    private fun formatMoney(
        amount: Double
    ): String {

        return "${
            NumberFormat
                .getNumberInstance(
                    Locale("vi", "VN")
                )
                .format(amount)
        } đ"
    }
}