package com.example.financeapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.financeapp.databinding.ItemBudgetBinding
import com.example.financeapp.model.BudgetResponse

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class BudgetAdapter(

    private val list: List<BudgetResponse>

) : RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {

    inner class ViewHolder(

        val binding: ItemBudgetBinding

    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemBudgetBinding.inflate(

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

        val symbols =
            DecimalFormatSymbols(Locale.US)

        symbols.groupingSeparator = '.'

        val formatter =
            DecimalFormat("#,###", symbols)

        val spentText =
            formatter.format(item.spent) + " đ"

        val budgetText =
            formatter.format(item.amount) + " đ"

        holder.binding.txtCategory.text =
            item.categoryName

        holder.binding.txtMonth.text =
            "Month ${item.month}/${item.year}"

        holder.binding.txtBudget.text =
            "Budget: $budgetText"

        holder.binding.txtSpent.text =
            "Spent: $spentText"

        // =========================
        // PERCENT
        // =========================

        val percent =
            item.percent.coerceAtMost(100)

        holder.binding.txtPercent.text =
            "${item.percent}%"

        holder.binding.progressBudget.progress =
            percent

        // =========================
        // PROGRESS COLOR
        // =========================

        when {

            item.percent < 70 -> {

                holder.binding.progressBudget.progressTintList =
                    android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#4CAF50")
                    )
            }

            item.percent <= 100 -> {

                holder.binding.progressBudget.progressTintList =
                    android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#FF9800")
                    )
            }

            else -> {

                holder.binding.progressBudget.progressTintList =
                    android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#F44336")
                    )
            }
        }

        // =========================
        // REMAINING
        // =========================

        if (item.remaining >= 0) {

            val remainingText =
                formatter.format(item.remaining) + " đ"

            holder.binding.txtRemaining.text =
                "Remaining: $remainingText"

            holder.binding.txtRemaining.setTextColor(
                Color.parseColor("#4CAF50")
            )

        } else {

            val overText =
                formatter.format(
                    kotlin.math.abs(item.remaining)
                ) + " đ"

            holder.binding.txtRemaining.text =
                "⚠ Over Budget: $overText"

            holder.binding.txtRemaining.setTextColor(
                Color.RED
            )
        }
    }

    override fun getItemCount(): Int {

        return list.size
    }
}