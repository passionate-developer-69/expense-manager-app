package com.anureet.expensemanager.ui

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anureet.expensemanager.R
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.set_balance_info.view.*
import org.eazegraph.lib.models.PieModel


class HomeFragment : Fragment() {

    private lateinit var viewModel: TransactionListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(TransactionListViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        setNetBalance(sharedPreferences,editor)
        refreshInfo(sharedPreferences,editor)
        super.onResume()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val toolbar : MaterialToolbar = requireActivity().findViewById(R.id.topAppBarid)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_person_48)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }
        appBar()

        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        setNetBalance(sharedPreferences, editor)

        updatePieChart()

        set_balance_infoid.setOnClickListener {
            showDialog()
        }

        with(transaction_listid){
            layoutManager = LinearLayoutManager(activity)
            adapter = TransactionAdapter {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
                        it
                    )
                )
            }
        }

        add_transactioni.setOnClickListener{
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
                    0
                )
            )
        }

        with(monthly_card_listid){
            layoutManager = LinearLayoutManager(activity)
            adapter = MonthlyCardAdapter({
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToMonthlyExpenseFragment(
                        it
                    )
                )
            },requireContext())
        }

        viewModel.month.observe(viewLifecycleOwner, Observer{
            (monthly_card_listid.adapter as MonthlyCardAdapter).submitList(it)
        })

        viewModel.transactions.observe(viewLifecycleOwner, Observer{
            (transaction_listid.adapter as TransactionAdapter).submitList(it)
        })
        refreshInfo(sharedPreferences, editor)

    }

    private fun refreshInfo(sharedPreferences: SharedPreferences, editor: SharedPreferences.Editor) {
        var cash = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
        var credit = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
        var bank = sharedPreferences.getFloat(getString(R.string.CASH), 0f)


        viewModel.cash.observe(viewLifecycleOwner, Observer {
            if (cash != 0f && it != null) {
                cash = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
                cash += it
                editor.putFloat(getString(R.string.CASH), cash)
                cash_amountid.text = cash.toString()
            }
        })
        viewModel.credit.observe(viewLifecycleOwner, Observer {
            if (credit != 0f && it != null) {
                credit = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
                credit += it
                editor.putFloat(getString(R.string.CREDIT), credit)
                credit_amountid.text = credit.toString()
            }
        })
        viewModel.bank.observe(viewLifecycleOwner, Observer {
            if (bank != 0f && it != null) {
                bank = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
                Log.d("TAG", "BANK: " + it)
                Log.d("TAG", "BANK S:" + bank)
                bank += it
                editor.putFloat(getString(R.string.BANK), bank)
                debit_amountid.text = bank.toString()
            }
        })
        updatePieChart()
    }

    private fun setNetBalance(sharedPreferences: SharedPreferences, editor: SharedPreferences.Editor) {
        // Getting data to set up name and monthly budget in the home screen
        var yearlyBudget = sharedPreferences.getFloat(getString(R.string.YearlyBudget),0f)

        net_balanceid.text = yearlyBudget.toString()
        viewModel.expense.observe(viewLifecycleOwner, Observer{
            if(it!=null){
                yearlyBudget = sharedPreferences.getFloat(getString(R.string.YearlyBudget),0f)
                yearlyBudget += it
                editor.putFloat(getString(R.string.YearlyBudget),yearlyBudget)
                net_balanceid.text = yearlyBudget.toString()
            }
        })
    }

    private fun appBar() {
        topAppBarid.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.calendarid -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCalendarFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showDialog() {

        val dialog = LayoutInflater.from(requireContext()).inflate(R.layout.set_balance_info, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(dialog)
            .setTitle("Set Details")
        val  mAlertDialog = mBuilder.show()

        checkValues(dialog)

        dialog.set_infoid.setOnClickListener {
            mAlertDialog.dismiss()
            val cashAmount = dialog.Cashid.text.toString()
            val bankAmount = dialog.Bankid.text.toString()
            setBalanceInfo(cashAmount.toFloat(),bankAmount.toFloat(),dialog)
        }
        dialog.cancelid.setOnClickListener { mAlertDialog.dismiss() }
        mAlertDialog.show()

    }

    private fun checkValues(dialog: View) {
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)

        var yearlyBudget = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)*12


        val boardingTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                val bankAmount = dialog.Bankid.text.toString()
                val cashAmount = dialog.Cashid.text.toString()

                if(cashAmount.isEmpty() || bankAmount.isEmpty()){
                    dialog.set_infoid.isEnabled = false
                }else if(!cashAmount.isEmpty() && cashAmount.toFloat() > yearlyBudget){
                    dialog.set_infoid.isEnabled = false
                    dialog.Cashid.error = "Greater than net balance available"
                }else if(!bankAmount.isEmpty() && bankAmount.toFloat()>yearlyBudget || cashAmount.toFloat()+bankAmount.toFloat() > yearlyBudget){
                    dialog.set_infoid.isEnabled = false
                    dialog.Bankid.error = "Greater than net balance available"
                }
                else{
                    dialog.Cashid.error = null
                    dialog.set_infoid.isEnabled = true
                    val creditAmount = yearlyBudget - (cashAmount.toFloat() + bankAmount.toFloat())
                    dialog.Creditid.setText(creditAmount.toString())
                }

            }
        }
        dialog.Cashid.addTextChangedListener(boardingTextWatcher)
        dialog.Bankid.addTextChangedListener(boardingTextWatcher)

    }
    private fun setBalanceInfo(cashAmount: Float, bankAmount: Float, dialog: View) {
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)

        var yearlyBudget = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)*12
        val creditAmount = (yearlyBudget) - (cashAmount + bankAmount)
        dialog.Creditid.setText(creditAmount.toString())

        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putFloat(getString(R.string.CASH),cashAmount)
        editor.putFloat(getString(R.string.BANK),bankAmount)
        editor.putFloat(getString(R.string.CREDIT),creditAmount)
        editor.putBoolean(getString(R.string.FLAG),true)
        editor.apply()

        updatePieChart()
        refreshFragment()
    }

    fun updatePieChart(){

        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        var mcash = sharedPreferences.getFloat("CashAmount",0f)
        var mbank = sharedPreferences.getFloat("BankAmount",0f)
        var mcredit = sharedPreferences.getFloat("CreditAmount",0f)

        cash_amountid.text = ""+mcash
        debit_amountid.text = ""+mbank
        credit_amountid.text = ""+mcredit

        var cash = (mcash / (mcash+mbank+mcredit))*100
        var credit = (mcredit / (mcash+mbank+mcredit))*100
        var bank = (mbank / (mcash+mbank+mcredit))*100

        piechartid.clearChart()
        piechartid?.addPieSlice(
            PieModel("Cash", cash, Color.parseColor("#FFA726"))
        )
        piechartid?.addPieSlice(
            PieModel("Credit", credit, Color.parseColor("#66BB6A"))
        )
        piechartid?.addPieSlice(
            PieModel("Bank", bank, Color.parseColor("#EF5350"))
        )

        piechartid?.startAnimation();

    }
    @Suppress("DEPRECATION")
    fun refreshFragment() {
        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }



}