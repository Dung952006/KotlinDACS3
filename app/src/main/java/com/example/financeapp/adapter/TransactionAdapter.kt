package com.example.financeapp.adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp.EditTransactionActivity
import com.example.financeapp.R
import com.example.financeapp.model.Transaction

class TransactionAdapter(
    private val transactionList: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val txtIcon: TextView =
            itemView.findViewById(R.id.txtIcon)

        val txtTitle: TextView =
            itemView.findViewById(R.id.txtTitle)

        val txtCategory: TextView =
            itemView.findViewById(R.id.txtCategory)

        val txtAmount: TextView =
            itemView.findViewById(R.id.txtAmount)

        val txtDate: TextView =
            itemView.findViewById(R.id.txtDate)

        val cardTransaction: CardView =
            itemView.findViewById(R.id.cardTransaction)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)

        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int
    ) {
        val transaction = transactionList[position]

        // ICON + màu nền theo category
        val categoryName = transaction.categoryName ?: ""
        holder.txtIcon.text = getIcon(categoryName, transaction.type)
        holder.txtIcon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(getIconBackground(categoryName, transaction.type)))

        // TITLE
        holder.txtTitle.text = transaction.title

        // CATEGORY NAME
        holder.txtCategory.text = categoryName.ifEmpty { "No Category" }

        // DATE
        holder.txtDate.text = transaction.date

        // AMOUNT + COLOR
        val formattedAmount = String.format("%,.0f", transaction.amount)

        if (transaction.type == "INCOME") {
            holder.txtAmount.text = "+$formattedAmount đ"
            holder.txtAmount.setTextColor(Color.parseColor("#00C48C"))
        } else {
            holder.txtAmount.text = "-$formattedAmount đ"
            holder.txtAmount.setTextColor(Color.parseColor("#FF647C"))
        }

        // CLICK ITEM
        holder.cardTransaction.setOnClickListener {
            val intent = Intent(
                holder.itemView.context,
                EditTransactionActivity::class.java
            )
            intent.putExtra("id", transaction.id ?: 0)
            intent.putExtra("title", transaction.title)
            intent.putExtra("amount", transaction.amount)
            intent.putExtra("userId", transaction.userId)
            intent.putExtra("categoryId", transaction.categoryId)
            intent.putExtra("type", transaction.type)
            intent.putExtra("date", transaction.date)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    // ----------------------------------------------------------------
    // ICON theo tên category (không phân biệt hoa thường)
    // ----------------------------------------------------------------
    private fun getIcon(categoryName: String, type: String): String {
        return when (categoryName.trim().lowercase()) {
            "ăn uống", "food", "ăn", "uống"         -> "🍔"
            "di chuyển", "transport", "xe", "xăng"   -> "🚌"
            "lương", "salary"                         -> "💼"
            "freelance", "thu nhập thêm"              -> "💻"
            "giải trí", "entertainment", "vui chơi"  -> "🎮"
            "mua sắm", "shopping"                     -> "🛒"
            "y tế", "health", "sức khỏe"             -> "🏥"
            "giáo dục", "education", "học phí"       -> "📚"
            "tiết kiệm", "saving"                     -> "🏦"
            "nhà ở", "rent", "thuê nhà"              -> "🏠"
            "điện nước", "utilities", "hóa đơn"      -> "💡"
            "du lịch", "travel"                       -> "✈️"
            "thể thao", "sport", "gym"                -> "⚽"
            "quà tặng", "gift"                        -> "🎁"
            else -> if (type == "INCOME") "💰" else "💸"
        }
    }

    // ----------------------------------------------------------------
    // Màu nền ô icon theo category
    // ----------------------------------------------------------------
    private fun getIconBackground(categoryName: String, type: String): String {
        return when (categoryName.trim().lowercase()) {
            "ăn uống", "food", "ăn", "uống"         -> "#FFF0E6" // cam nhạt
            "di chuyển", "transport", "xe", "xăng"   -> "#E6F0FF" // xanh dương nhạt
            "lương", "salary"                         -> "#E6FFE6" // xanh lá nhạt
            "freelance", "thu nhập thêm"              -> "#E8F5E9" // xanh mint nhạt
            "giải trí", "entertainment", "vui chơi"  -> "#F0E6FF" // tím nhạt
            "mua sắm", "shopping"                     -> "#FFF0F0" // đỏ nhạt
            "y tế", "health", "sức khỏe"             -> "#E6FFF5" // xanh mint
            "giáo dục", "education", "học phí"       -> "#FFF9E6" // vàng nhạt
            "tiết kiệm", "saving"                     -> "#E6F0FF" // xanh nhạt
            "nhà ở", "rent", "thuê nhà"              -> "#FFF3E6" // cam nhạt
            "điện nước", "utilities", "hóa đơn"      -> "#FFFDE6" // vàng nhạt
            "du lịch", "travel"                       -> "#E6F7FF" // xanh trời nhạt
            "thể thao", "sport", "gym"                -> "#E8FFE6" // xanh lá nhạt
            "quà tặng", "gift"                        -> "#FFE6F0" // hồng nhạt
            else -> if (type == "INCOME") "#E6FFE6" else "#F0EEF9"
        }
    }
}