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
import com.payoman.nammabooth.interfaces.OnVoterDetailClick
import java.util.*

class VoterSearchListAdapter(
    private val voterList: MutableList<Voter>,
    private val context: Context,
    private val onVoterCardClickListener: OnVoterCardClickListener,
    private val onVoterDetailClick: OnVoterDetailClick
) : RecyclerView.Adapter<VoterSearchListAdapter.ViewHolder>() {

    lateinit var binding: VoterCardBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(voter: Voter) {
            binding.apply {
                voterSLNO.text = voter.sno
                voterId.text = voter.voterId ?: "NA"
                voterName.text = String.format(
                    Locale.ENGLISH,
                    "%s(%s)",
                    voter.voterNameEn ?: "NA",
                    voter.voterNameKan ?: "NA"
                )
                voterPartNoName.text = String.format(
                    Locale.ENGLISH,
                    "%s",
                    voter.partNo
                )
                voterMobileNumber.text =
                    if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
                voterAge.text = String.format(Locale.ENGLISH, "%d %s", voter.age, "years")
                voterGender.text = voter.sex
                voterHouseNo.text = voter.houseNoEn
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

    override fun getItemCount(): Int {
        return voterList.size
    }

    fun updateVoterList(newVoterList: MutableList<Voter>) {
        voterList.clear()
        voterList.addAll(newVoterList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: VoterSearchListAdapter.ViewHolder, position: Int) {
        holder.setData(voterList[position])
        binding.voterCard.setOnClickListener {
            onVoterCardClickListener.OnClick(voterList[position])
        }
        binding.expandImageView.setOnClickListener {
            onVoterDetailClick.onClick(voterList[position])
        }
        holder.setIsRecyclable(false)
    }

    fun clearData() {
        voterList.clear()
        notifyDataSetChanged()
    }
}