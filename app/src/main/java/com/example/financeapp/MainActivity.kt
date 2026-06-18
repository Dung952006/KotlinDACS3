
package com.example.financeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.financeapp.databinding.ActivityMainBinding
import com.example.financeapp.fragment.AIFragment
import com.example.financeapp.fragment.BudgetFragment
import com.example.financeapp.fragment.DashboardFragment
import com.example.financeapp.fragment.SavingGoalFragment
import com.example.financeapp.fragment.TransactionFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        replaceFragment(DashboardFragment())

        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_dashboard -> {
                    replaceFragment(DashboardFragment())
                }

                R.id.nav_transaction -> {
                    replaceFragment(TransactionFragment())
                }

                R.id.nav_budget -> {
                    replaceFragment(BudgetFragment())
                }

                R.id.nav_saving -> {
                    replaceFragment(SavingGoalFragment())
                }

                R.id.nav_ai -> {
                    replaceFragment(AIFragment())
                }
            }

            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}
