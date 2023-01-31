package com.payoman.nammabooth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.SliderLayoutBinding
import com.payoman.nammabooth.models.ImageSliderItems

class ImageSliderAdapter(
    private val sliderList: MutableList<ImageSliderItems>,
    private val context: Context
) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    lateinit var binding: SliderLayoutBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(sliderItems: ImageSliderItems) {
            binding.apply {
                if (sliderItems.remoteImage != null)
                    Glide.with(context).load(sliderItems.remoteImage).error(AppCompatResources.getDrawable(context, R.drawable.banner1)).into(binding.sliderImageView)
                else
                    Glide.with(context).load(sliderItems.localImage).error(AppCompatResources.getDrawable(context, R.drawable.banner1)).into(binding.sliderImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.slider_layout, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(sliderList[position])
    }

    override fun getItemCount(): Int {
        return sliderList.size
    }
}