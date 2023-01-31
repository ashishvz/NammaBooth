package com.payoman.nammabooth.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.ImageSliderAdapter
import com.payoman.nammabooth.database.Candidate
import com.payoman.nammabooth.databinding.FragmentHomeBinding
import com.payoman.nammabooth.models.ImageSliderItems
import com.payoman.nammabooth.viewmodels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var sliderTimer: CountDownTimer
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var candidate: Candidate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runBlocking(Dispatchers.IO) {
            candidate = homeViewModel.getCandidateDetails()[0]
        }
        val sliderList = mutableListOf<ImageSliderItems>()
        for (bannerUrl in candidate.bannerUrls!!)
            sliderList.add(ImageSliderItems(null, bannerUrl))
        sliderTimer = object : CountDownTimer(25000, 1000) {
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                val currentItem = binding.viewPager.currentItem
                var nextItem = currentItem + 1
                if (nextItem >= imageSliderAdapter.itemCount)
                    nextItem = 0
                binding.viewPager.setCurrentItem(nextItem, true)
                start()
            }

        }
        binding.apply {
            imageSliderAdapter = ImageSliderAdapter(sliderList, requireActivity())
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPager.adapter = imageSliderAdapter
            voterListCard.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToVoterListFragment())
            }
            profileCard.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
            }
            updateVoterPhoneCard.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUpdatePhoneNumberFragment())
            }
            surveyCard.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSurveyFragment())
            }
            Glide.with(requireContext()).load(candidate.candidateImageUrl).placeholder(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_candidate
                )
            ).error(AppCompatResources.getDrawable(requireContext(), R.drawable.candidate))
                .into(candidateImageView)
            candidateName.text = candidate.candidateName
            candidateDesc.text = String.format(Locale.ENGLISH, "%s, %s", candidate.designation, candidate.constituencyName)
            candidateImageContainer.setOnClickListener {
                val extras = FragmentNavigatorExtras(
                    candidateImageContainer  to "candidateImageCard"
                )
                findNavController().navigate(R.id.action_homeFragment_to_candidateFragment, null, null, extras)
            }
        }
        sliderTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        sliderTimer.cancel()
    }
}