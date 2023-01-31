package com.payoman.nammabooth.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.NewsletterLayoutBinding
import com.payoman.nammabooth.interfaces.OnClickListener

class CandidateNewsLetterAdapter(
    private val context: Context,
    private val urls: MutableList<String>,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<CandidateNewsLetterAdapter.ViewHolder>() {

    lateinit var binding: NewsletterLayoutBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(url: String) {
            binding.apply {
                newsLetterURLText.text = url
                newsLetterURLText.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                newsLetterURLText.setOnClickListener {
                    onClickListener.onClick(url)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.newsletter_layout, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(urls[position])
    }

    override fun getItemCount(): Int {
        return urls.size
    }
}