package com.example.financeapp.fragment

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.example.financeapp.api.RetrofitClient
import com.example.financeapp.databinding.FragmentDashboardBinding
import com.example.financeapp.model.MonthlySummaryResponse
import com.example.financeapp.model.PieChartResponse
import com.example.financeapp.model.SummaryResponse
import android.content.Intent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.financeapp.ProfileActivity
import java.util.Calendar

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.imgProfile.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProfileActivity::class.java)
            )
        }


        setupYearSpinner()
        loadSummary()
        loadPieChart()
        loadBarChart()

        return binding.root
    }

    private fun setupYearSpinner() {

        val years = arrayListOf("2024", "2025", "2026", "2027")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            years
        )

        binding.spinnerYear.adapter = adapter
        binding.spinnerYear.setSelection(2) // default 2026

        binding.spinnerYear.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    loadBarChart()
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }

    private fun loadSummary() {

        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("MyApp", android.content.Context.MODE_PRIVATE)

        val userId = sharedPreferences.getLong("userId", 0)

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        RetrofitClient.api.getSummary(userId, month, year)
            .enqueue(object : Callback<SummaryResponse> {

                override fun onResponse(
                    call: Call<SummaryResponse>,
                    response: Response<SummaryResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val summary = response.body()!!
                        val balance = summary.income - summary.expense

                        binding.txtIncome.text = "+ ${String.format("%,.0f", summary.income)} đ"
                        binding.txtExpense.text = "- ${String.format("%,.0f", summary.expense)} đ"
                        binding.txtBalance.text = "${String.format("%,.0f", balance)} đ"
                    }
                }

                override fun onFailure(call: Call<SummaryResponse>, t: Throwable) {}
            })
    }

    private fun loadBarChart() {

        val sharedPreferences = requireActivity()
            .getSharedPreferences("MyApp", AppCompatActivity.MODE_PRIVATE)

        val userId = sharedPreferences.getLong("userId", 0)

        // FIX: bỏ tham số year, chỉ truyền userId khớp backend
        RetrofitClient.api.getMonthlySummary(userId)
            .enqueue(object : Callback<List<MonthlySummaryResponse>> {

                override fun onResponse(
                    call: Call<List<MonthlySummaryResponse>>,
                    response: Response<List<MonthlySummaryResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val list = response.body()!!

                        val incomeEntries = ArrayList<BarEntry>()
                        val expenseEntries = ArrayList<BarEntry>()

                        for (i in 0 until minOf(5, list.size)) {
                            incomeEntries.add(BarEntry(i.toFloat(), list[i].income.toFloat()))
                            expenseEntries.add(BarEntry(i.toFloat(), list[i].expense.toFloat()))
                        }

                        // đồng bộ màu với design system
                        val incomeDataSet = BarDataSet(incomeEntries, "Income")
                        incomeDataSet.color = Color.parseColor("#00C48C")
                        incomeDataSet.valueTextColor = Color.parseColor("#1A1A2E")
                        incomeDataSet.valueTextSize = 10f

                        val expenseDataSet = BarDataSet(expenseEntries, "Expense")
                        expenseDataSet.color = Color.parseColor("#FF647C")
                        expenseDataSet.valueTextColor = Color.parseColor("#1A1A2E")
                        expenseDataSet.valueTextSize = 10f

                        val data = BarData(incomeDataSet, expenseDataSet)

                        val groupSpace = 0.3f
                        val barSpace = 0.05f
                        val barWidth = 0.3f
                        data.barWidth = barWidth

                        binding.barChart.data = data
                        binding.barChart.groupBars(0f, groupSpace, barSpace)

                        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May")

                        val xAxis = binding.barChart.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(months)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.granularity = 1f
                        xAxis.setCenterAxisLabels(true)
                        xAxis.axisMinimum = 0f
                        xAxis.axisMaximum = 0f + binding.barChart.barData
                            .getGroupWidth(groupSpace, barSpace) * months.size
                        xAxis.textColor = Color.parseColor("#6B7280")
                        xAxis.setDrawGridLines(false)

                        binding.barChart.axisLeft.textColor = Color.parseColor("#6B7280")
                        binding.barChart.axisLeft.gridColor = Color.parseColor("#E8E6F8")
                        binding.barChart.axisRight.isEnabled = false
                        binding.barChart.description.isEnabled = false
                        binding.barChart.legend.textColor = Color.parseColor("#1A1A2E")
                        binding.barChart.setBackgroundColor(Color.WHITE)
                        binding.barChart.animateY(1000)
                        binding.barChart.invalidate()
                    }
                }

                override fun onFailure(call: Call<List<MonthlySummaryResponse>>, t: Throwable) {}
            })
    }

    private fun loadPieChart() {

        val sharedPreferences = requireActivity()
            .getSharedPreferences("MyApp", AppCompatActivity.MODE_PRIVATE)

        val userId = sharedPreferences.getLong("userId", 0)

        RetrofitClient.api.getPieSummary(userId)
            .enqueue(object : Callback<List<PieChartResponse>> {

                override fun onResponse(
                    call: Call<List<PieChartResponse>>,
                    response: Response<List<PieChartResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val list = response.body()!!

                        val entries = ArrayList<PieEntry>()
                        for (item in list) {
                            entries.add(PieEntry(item.total.toFloat(), item.name))
                        }

                        val dataSet = PieDataSet(entries, "Expense Categories")

                        // đồng bộ màu với design system
                        dataSet.colors = listOf(
                            Color.parseColor("#6C5CE7"),
                            Color.parseColor("#00C48C"),
                            Color.parseColor("#FF647C"),
                            Color.parseColor("#A29BFE"),
                            Color.parseColor("#FFA940"),
                            Color.parseColor("#74B9FF"),
                            Color.parseColor("#55EFC4"),
                            Color.parseColor("#FD79A8")
                        )

                        dataSet.valueTextSize = 13f
                        dataSet.valueTextColor = Color.WHITE
                        dataSet.sliceSpace = 3f

                        val data = PieData(dataSet)

                        binding.pieChart.data = data
                        binding.pieChart.description.isEnabled = false
                        binding.pieChart.centerText = "Expenses"
                        binding.pieChart.setCenterTextSize(14f)
                        binding.pieChart.setCenterTextColor(Color.parseColor("#1A1A2E"))
                        binding.pieChart.setHoleColor(Color.WHITE)
                        binding.pieChart.legend.textColor = Color.parseColor("#1A1A2E")
                        binding.pieChart.animateY(1000)
                        binding.pieChart.invalidate()
                    }
                }

                override fun onFailure(call: Call<List<PieChartResponse>>, t: Throwable) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
 