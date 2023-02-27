package com.payoman.nammabooth.adapters
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.SurveyCardLayoutBinding
import com.payoman.nammabooth.interfaces.OnSurveyClickListener
import com.payoman.nammabooth.models.InAppSurvey

class InAppSurveyAdapter(
    private val context: Context,
    private val inAppSurveyList: MutableList<InAppSurvey>
) : RecyclerView.Adapter<InAppSurveyAdapter.ViewHolder>(), OnSurveyClickListener {
    private lateinit var binding: SurveyCardLayoutBinding
    private lateinit var summaryAdapter: FinalSummaryCardAdapter
    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(inAppSurvey: InAppSurvey) {
            binding.apply {
                questionTextView.text = inAppSurvey.question
                for (option in inAppSurvey.options) {
                    val radioButton = RadioButton(context)
                    radioButton.id = View.generateViewId()
                    radioButton.text = option
                    radioButton.buttonTintList = context.getColorStateList(R.color.primary_dark)
                    radioGroup.addView(radioButton)
                }
                radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                    inAppSurvey.selected =
                        radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString()
                }
            }
        }

        fun setDataForFinalSummary(inAppSurvey: InAppSurvey) {
            binding.apply {
                summaryRecycler.visibility = View.VISIBLE
                questionTextView.text = inAppSurvey.question
                summaryRecycler.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                summaryRecycler.setHasFixedSize(false)
                summaryAdapter = FinalSummaryCardAdapter(
                    context,
                    mutableListOf(
                        context.getColor(R.color.blue),
                        context.getColor(R.color.selected_green),
                        context.getColor(R.color.primary_yellow)
                    ),
                    inAppSurvey,
                    this@InAppSurveyAdapter
                )
                summaryRecycler.adapter = summaryAdapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.survey_card_layout,
            parent,
            false
        )
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*  if (position == inAppSurveyList.size - 1)
              holder.setDataForFinalSummary(inAppSurveyList[position])
          else*/
        holder.setData(inAppSurveyList[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return inAppSurveyList.size
    }

    fun addData(inAppSurveyListData: MutableList<InAppSurvey>) {
        inAppSurveyList.clear()
        inAppSurveyList.addAll(inAppSurveyListData)
        notifyDataSetChanged()
    }

    fun getData(): MutableList<InAppSurvey> {
        return inAppSurveyList
    }

    override fun onClick(position: Int) {
        summaryAdapter.setSelected(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}