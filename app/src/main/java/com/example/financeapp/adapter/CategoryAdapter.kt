package com.example.financeapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp.R
import com.example.financeapp.model.Category

class CategoryAdapter(
    private val categoryList: List<Category>,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val txtCategoryName: TextView =
            itemView.findViewById(R.id.txtCategoryName)

        val txtCategoryType: TextView =
            itemView.findViewById(R.id.txtCategoryType)

        val btnDelete: Button =
            itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_category,
                parent,
                false
            )

        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {

        val category =
            categoryList[position]

        holder.txtCategoryName.text =
            category.name

        holder.txtCategoryType.text =
            category.type

        holder.btnDelete.setOnClickListener {

            onDelete(category)
        }
    }

    override fun getItemCount(): Int {

        return categoryList.size
    }
}