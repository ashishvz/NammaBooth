package com.payoman.nammabooth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.database.Urls
import com.payoman.nammabooth.databinding.UrlLayoutBinding
import com.payoman.nammabooth.interfaces.OnClickListener
import com.payoman.nammabooth.models.CandidateUrl

class CandidateUrlAdapter(
    private val candidateUrl: MutableList<CandidateUrl>,
    private val context: Context,
    private val onClickListener: OnClickListener
): RecyclerView.Adapter<CandidateUrlAdapter.ViewHolder>() {
    private lateinit var binding: UrlLayoutBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root){
        fun setData(candidateUrl: CandidateUrl) {
            binding.apply {
                urlImage.setImageResource(candidateUrl.imgRes)
                urlCard.setOnClickListener {
                    onClickListener.onClick(candidateUrl.url)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.url_layout, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(candidateUrl[position])
    }

    override fun getItemCount(): Int {
        return candidateUrl.size
    }
}