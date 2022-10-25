package com.example.financetracker

import android.graphics.Color
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.ListFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.financetracker.model.Transaction
import com.example.financetracker.transactions.read.TransactionListsFragmentDirections
import java.text.ParseException
import java.text.SimpleDateFormat

class TransactionAdapter: RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    var dateFormat : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    private var transactions = emptyList<Transaction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.transaction_layout, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() : Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transactions[position]
        holder.bind(item)
    }

    inner class ViewHolder(val v: View): RecyclerView.ViewHolder(v) {
        private val nameView = v.findViewById<TextView>(R.id.name)
        private val dateView = v.findViewById<TextView>(R.id.date)
        private val categoryView = v.findViewById<TextView>(R.id.category)
        private val amountView = v.findViewById<TextView>(R.id.amount)

        fun bind(item: Transaction) {
            nameView.text = item.name
            try {
                dateView.text = dateFormat.format(item.date)
            } catch (e: ParseException)
            {
                e.printStackTrace()
                dateView.text = "Unidentified"
            }
            categoryView.text = item.category

            // determine type of transaction through amount
            if (item.amount > 0) {
                amountView.text = "$${item.amount.toString()}"
                amountView.setTextColor(ResourcesCompat.getColor(v.resources, R.color.green, null))
            } else {
                amountView.text = "-$${(-(item.amount)).toString()}"
                amountView.setTextColor(ResourcesCompat.getColor(v.resources, R.color.red, null))
            }

            // click a transaction to open the update screen
            v.findViewById<LinearLayout>(R.id.transaction_layout).setOnClickListener {
                val action = TransactionListsFragmentDirections.actionTransactionListsFragmentToUpdateTransactionFragment(item)
                v.findNavController().navigate(action)
            }
        }
    }

    fun setData(transactions: List<Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged()
    }

    fun calculateTotal() : Triple<Float, Float, Float> {
        var expense = 0f
        var income = 0f
        transactions.forEach {
            if (it.amount < 0) {
                expense += it.amount
            }
            else {
                income += it.amount
            }
        }
        return Triple(-expense, income, income + expense)
    }
}