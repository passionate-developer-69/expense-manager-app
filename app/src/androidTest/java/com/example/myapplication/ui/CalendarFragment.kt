package com.anureet.expensemanager.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.anureet.expensemanager.R
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.DateFormat
import java.util.*


class CalendarFragment : Fragment() {

    private lateinit var viewModel: MonthlyTransactionListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MonthlyTransactionListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val toolbar : MaterialToolbar = requireActivity().findViewById(R.id.calendarAppBarid)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val calendar = Calendar.getInstance()

        calendarViewid.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            var m = month+1

            var date:String = "$year-$m-$dayOfMonth"
            if(month<10)
                date = "$year-0$m-$dayOfMonth"

            viewModel.setDate(date)

            Log.d("TAG","yearmonth:"+date)
            val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
            val formattedDate = dateFormatter.format(calendar.time)

            textViewid.text = formattedDate
            with(calendar_listid){
                layoutManager = LinearLayoutManager(activity)
                adapter = CalendarAdapter {

                }

            }

            viewModel.amountByMonth.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
                (calendar_listid.adapter as CalendarAdapter).submitList(it)
            })
        }

    }
}