package com.payoman.nammabooth.adapters

import android.content.Context
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.SummaryCardBinding
import com.payoman.nammabooth.interfaces.OnSurveyClickListener
import com.payoman.nammabooth.models.InAppSurvey

class FinalSummaryCardAdapter(
    private val context: Context,
    private val colorList: MutableList<Int>,
    private val inAppSurvey: InAppSurvey,
    private val onSurveyClickListener: OnSurveyClickListener
) : RecyclerView.Adapter<FinalSummaryCardAdapter.ViewHolder>() {
    private lateinit var binding: SummaryCardBinding
    private var selectedPosition = -1
    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(color: Int) {
            binding.apply {
                card.setCardBackgroundColor(color)
                tick.visibility = View.GONE
                if (selectedPosition > -1) {
                    if (inAppSurvey.options[selectedPosition] == inAppSurvey.selected)
                        tick.visibility = View.VISIBLE
                    else
                        tick.visibility = View.GONE
                }
                card.setOnClickListener {
                    inAppSurvey.selected = inAppSurvey.options[adapterPosition]
                    onSurveyClickListener.onClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.summary_card, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(colorList[position])
    }

    override fun getItemCount(): Int {
       return colorList.size
    }

    fun setSelected(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
}