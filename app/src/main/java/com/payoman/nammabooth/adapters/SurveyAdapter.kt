package com.payoman.nammabooth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.SurveyCardBinding
import com.payoman.nammabooth.interfaces.OnSurveyClickListener
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import com.payoman.nammabooth.models.Survey
import com.payoman.nammabooth.models.WhatsappMessage

class SurveyAdapter(
    private val context: Context,
    private val surveyList: MutableList<Survey>,
    private val onSurveyClickListener: OnSurveyClickListener
) :
    RecyclerView.Adapter<SurveyAdapter.ViewHolder>() {
    private var rowIndex = -1
    private lateinit var binding: SurveyCardBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(survey: Survey) {
            binding.apply {
                question.text = survey.whatsappMessage.message_content
                options.text =
                    survey.whatsappMessage.message_options.joinToString(separator = ",") {
                        it
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.survey_card,
            parent,
            false
        )
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(surveyList[position])
        holder.setIsRecyclable(false)
        binding.surveyCard.setOnClickListener {
            rowIndex = holder.adapterPosition
            onSurveyClickListener.onClick(holder.adapterPosition)
            notifyDataSetChanged()
        }
        binding.selectedLayout.visibility = if (rowIndex == position) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return surveyList.size
    }

    fun updateData(newSurveyList: MutableList<Survey>) {

    }
}