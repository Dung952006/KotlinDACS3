package com.example.financeapp.model

data class SavingGoal(

    val id: Long? = null,

    val userId: Long,

    val goalName: String,

    val targetAmount: Double,

    val currentAmount: Double,

    val deadline: String
)