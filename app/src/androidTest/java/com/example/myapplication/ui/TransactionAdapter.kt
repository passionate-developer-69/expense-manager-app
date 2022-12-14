package com.anureet.expensemanager.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anureet.expensemanager.R
import com.anureet.expensemanager.data.Transaction
import com.anureet.expensemanager.data.Type
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.list_item.transaction_dateid

class TransactionAdapter(private val listener: (Long) -> Unit):
    ListAdapter<Transaction, TransactionAdapter.ViewHolder>(
        DiffCallback()
    ){

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val itemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder (override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        init{
            itemView.setOnClickListener{
                listener.invoke(getItem(adapterPosition).id)
            }
        }

        fun bind(transaction: Transaction){
            with(transaction){

                transaction_modeid.text = transaction.transaction_type
                when (transaction.transaction_type) {
                    "Cash" -> {
                        transaction_type_viewid.setBackgroundResource(R.color.cash)
                    }
                    "Credit" -> {
                        transaction_type_viewid.setBackgroundResource(R.color.credit)
                    }
                    "Bank" -> {
                        transaction_type_viewid.setBackgroundResource(R.color.bank)
                    }
                }

                transaction_namei.text = transaction.name
                if(transaction.income_expense.equals(Type.EXPENSE.toString())) {
                    transaction_amountid.text =  ""+ transaction.amount
                    transaction_amountid.setTextColor(Color.RED)
                }else{
                    transaction_amountid.text = "+" + transaction.amount
                    transaction_amountid.setTextColor(Color.GREEN)
                }
                transaction_dateid.text = ""+transaction.date
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}