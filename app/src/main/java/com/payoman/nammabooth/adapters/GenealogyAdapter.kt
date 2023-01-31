package com.payoman.nammabooth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.databinding.FamilyLayoutBinding
import java.util.*

class GenealogyAdapter(
    private val context: Context,
    private val voterList: MutableList<Voter>
): RecyclerView.Adapter<GenealogyAdapter.ViewHolder>() {

    private lateinit var binding: FamilyLayoutBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(voter: Voter) {
            binding.apply {
                voterId.text = voter.voterId ?: "NA"
                voterName.text = String.format(Locale.ENGLISH, "%s(%s)", voter.voterNameEn ?: "NA", voter.voterNameKan ?: "NA")
                voterMobileNumber.text = if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.family_layout, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(voterList[position])
    }

    override fun getItemCount(): Int {
        return voterList.size
    }

    fun clearData() {
        voterList.clear()
        notifyDataSetChanged()
    }

    fun updateData(voters: MutableList<Voter>) {
        voterList.clear()
        voterList.addAll(voters)
        notifyDataSetChanged()
    }
}