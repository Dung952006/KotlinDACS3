
package com.example.financeapp.api

import com.example.financeapp.model.BudgetRequest
import com.example.financeapp.model.BudgetResponse
import com.example.financeapp.model.Category
import com.example.financeapp.model.ChatRequest
import com.example.financeapp.model.ChatResponse
import com.example.financeapp.model.MonthlySummaryResponse
import com.example.financeapp.model.PieChartResponse
import com.example.financeapp.model.SavingGoal
import com.example.financeapp.model.SummaryResponse
import com.example.financeapp.model.Transaction
import com.example.financeapp.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // =========================
    // AUTH
    // =========================

    @POST("api/auth/register")
    fun register(
        @Body user: User
    ): Call<User>

    @POST("api/auth/login")
    fun login(
        @Body user: User
    ): Call<User>

    // =========================
    // TRANSACTION
    // =========================

    @GET("transactions")
    fun getTransactions(
        @Query("userId") userId: Long
    ): Call<List<Transaction>>

    @POST("transactions")
    fun addTransaction(
        @Body transaction: Transaction
    ): Call<Transaction>

    @PUT("transactions/{id}")
    fun updateTransaction(

        @Path("id")
        id: Long,

        @Body
        transaction: Transaction

    ): Call<Transaction>

    @DELETE("transactions/{id}")
    fun deleteTransaction(

        @Path("id")
        id: Long

    ): Call<Void>

    // =========================
    // CATEGORY
    // =========================

    @GET("categories")
    fun getCategories(
        @Query("userId") userId: Long
    ): Call<List<Category>>

    @POST("categories")
    fun addCategory(
        @Body category: Category
    ): Call<Category>

    @DELETE("categories/{id}")
    fun deleteCategory(
        @Path("id") id: Long
    ): Call<Void>

    // =========================
    // DASHBOARD
    // =========================

    @GET("stats/summary")
    fun getSummary(

        @Query("userId")
        userId: Long,

        @Query("month")
        month: Int,

        @Query("year")
        year: Int

    ): Call<SummaryResponse>

    @GET("transactions/monthly-summary/{userId}")
    fun getMonthlySummary(
        @Path("userId") userId: Long
    ): Call<List<MonthlySummaryResponse>>

    @GET("transactions/pie-summary/{userId}")
    fun getPieSummary(

        @Path("userId")
        userId: Long

    ): Call<List<PieChartResponse>>

    // =========================
    // BUDGET
    // =========================

    @GET("budgets/{userId}/{month}/{year}")
    fun getBudgets(

        @Path("userId")
        userId: Long,

        @Path("month")
        month: Int,

        @Path("year")
        year: Int

    ): Call<List<BudgetResponse>>

    @POST("budgets")
    fun saveBudget(

        @Body
        budget: BudgetRequest

    ): Call<Void>

    // =========================
// SAVING GOAL
// =========================

    @GET("saving-goals/{userId}")
    fun getSavingGoals(
        @Path("userId") userId: Long
    ): Call<List<SavingGoal>>

    @POST("saving-goals")
    fun createSavingGoal(
        @Body goal: SavingGoal
    ): Call<SavingGoal>

    @PUT("saving-goals/add-money/{id}")
    fun addMoneyToGoal(
        @Path("id") id: Long,
        @Query("amount") amount: Double
    ): Call<SavingGoal>



    @DELETE("saving-goals/{id}")
    fun deleteGoal(
        @Path("id") id: Long
    ): Call<Void>

    @POST("api/ai/chat")
    fun chatAI(
        @Query("userId") userId: Long,
        @Body request: ChatRequest
    ): Call<ChatResponse>
}
