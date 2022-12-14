package com.anureet.expensemanager.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anureet.expensemanager.R
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.fragment_monthly_expense.*


class MonthlyExpenseFragment : Fragment() {
    private lateinit var viewModel: MonthlyTransactionListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MonthlyTransactionListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_monthly_expense, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val toolbar : MaterialToolbar = requireActivity().findViewById(R.id.monthAppBarid)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        setMonthlyBalance()

        val monthYear = MonthlyExpenseFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setMonthYear(monthYear)

        with(monthly_transaction_listid){
            layoutManager = LinearLayoutManager(activity)
            adapter = TransactionAdapter {
                findNavController().navigate(
                    MonthlyExpenseFragmentDirections.actionMonthlyExpenseFragmentToAddTransactionFragment(
                        it
                    )
                )
            }
        }

        viewModel.transactionByMonth.observe(viewLifecycleOwner, Observer{
            (monthly_transaction_listid.adapter as TransactionAdapter).submitList(it)
        })

        add_transactioni.setOnClickListener {
            findNavController().navigate(MonthlyExpenseFragmentDirections.actionMonthlyExpenseFragmentToAddTransactionFragment(0))
        }


    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun setMonthlyBalance(){
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        var monthlyBalance = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

        viewModel.sumByMonth.observe(viewLifecycleOwner, Observer {
            var monthBalance = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)
            if(it!=null) {
                monthBalance += it
                month_balanceid.text = monthBalance.toString()
                updateProgressBar(sharedPreferences, monthBalance)
                amount_savedid.text = (monthBalance).toString()
                amount_spentid.text = (it*-1).toString()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateProgressBar(sharedPreferences: SharedPreferences, balance: Float) {
        var monthBalance = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)

        var progress = 100-(balance/monthBalance)*100

        if(progress>100){
            pbid.progress = 100
        }else {
            pbid.progress = progress.toInt()
        }
    }
}