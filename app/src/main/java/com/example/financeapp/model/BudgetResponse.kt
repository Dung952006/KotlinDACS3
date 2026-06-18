package com.example.financeapp.model

data class BudgetResponse(

    val categoryName: String,

    val amount: Double,

    val spent: Double,

    val remaining: Double,

    val percent: Int,

    val month: Int,

    val year: Int
)