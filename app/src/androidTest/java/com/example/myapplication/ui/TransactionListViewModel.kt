package com.anureet.expensemanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.anureet.expensemanager.data.MonthlyTransactions
import com.anureet.expensemanager.data.Transaction
import com.anureet.expensemanager.data.TransactionListRepository

class TransactionListViewModel(application: Application): AndroidViewModel(application) {
    private val repo : TransactionListRepository= TransactionListRepository(application)

    val transactions: LiveData<List<Transaction>>
    get() = repo.getTransactions()

    val month: LiveData<List<MonthlyTransactions>>
    get() = repo.getMonth()

    val expense: LiveData<Float>
    get() = repo.getAmount()

    val cash: LiveData<Float>
        get() = repo.getCashAmount()
    val credit: LiveData<Float>
        get() = repo.getCreditAmount()
    val bank: LiveData<Float>
        get() = repo.getBankAmount()
}