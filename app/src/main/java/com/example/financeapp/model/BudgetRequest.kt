
package com.example.financeapp.model

data class BudgetRequest(

    val userId: Long,

    val categoryId: Long,

    val month: Int,

    val year: Int,

    val amount: Double,

    val carryOver: Boolean
)
