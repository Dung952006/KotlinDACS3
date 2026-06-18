
package com.example.financeapp.model

data class Transaction(

    val id: Long?,

    val title: String,

    val amount: Double,

    val type: String,

    val date: String,

    val userId: Long,

    val categoryId: Long,

    val categoryName: String? = null
)
