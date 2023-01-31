package com.payoman.nammabooth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.databinding.VoterCardBinding
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import java.util.*
import kotlin.math.expm1

class UpdatePhoneAdapter(
    private val voterList: MutableList<Voter>,
    private val context: Context,
    private val onVoterCardClickListener: OnVoterCardClickListener
) : RecyclerView.Adapter<UpdatePhoneAdapter.ViewHolder>() {

    lateinit var binding: VoterCardBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(voter: Voter) {
            binding.apply {
                voterId.text = voter.voterId ?: "NA"
                voterName.text = String.format(Locale.ENGLISH, "%s(%s)", voter.voterNameEn ?: "NA", voter.voterNameKan ?: "NA")
                voterMobileNumber.text = if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
                voterSectionName.visibility = View.GONE
                sectionNameText.visibility = View.GONE
                expandImageView.visibility = View.GONE
                gridBottom.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.voter_card,
            parent,
            false
        )
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(voterList[position])
        binding.voterCard.setOnClickListener {
            onVoterCardClickListener.OnClick(voterList[position])
        }
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return voterList.size
    }

    fun updateVoterList(newVoterList: MutableList<Voter>) {
        voterList.clear()
        voterList.addAll(newVoterList)
        notifyDataSetChanged()
    }
}