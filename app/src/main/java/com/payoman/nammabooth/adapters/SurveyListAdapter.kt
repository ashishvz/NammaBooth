package com.payoman.nammabooth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.SurveyCardBinding
import com.payoman.nammabooth.models.WhatsappMessage
import java.util.*

class SurveyListAdapter(private val context: Context, private val whatsappMessageList: MutableList<WhatsappMessage>): RecyclerView.Adapter<SurveyListAdapter.ViewHolder>() {

    private lateinit var binding: SurveyCardBinding

    inner class ViewHolder: RecyclerView.ViewHolder(binding.root) {
        fun setData(whatsappMessage: WhatsappMessage) {
            binding.apply {
                questionText.text = whatsappMessage.message_content
                var optionString = ""
                for(option in whatsappMessage.message_options) {
                    optionString = String.format(Locale.ENGLISH, "%s %s", optionString, option)
                }
                optionsText.text = optionString
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.survey_card, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.setData(whatsappMessageList[position])
    }

    override fun getItemCount(): Int {
        return whatsappMessageList.size
    }
}