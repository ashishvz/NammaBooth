package com.payoman.nammabooth.fragments

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.transition.TransitionInflater
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.CandidateNewsLetterAdapter
import com.payoman.nammabooth.adapters.CandidateUrlAdapter
import com.payoman.nammabooth.database.Candidate
import com.payoman.nammabooth.databinding.FragmentCandidateBinding
import com.payoman.nammabooth.interfaces.OnClickListener
import com.payoman.nammabooth.models.CandidateUrl
import com.payoman.nammabooth.viewmodels.CandidateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CandidateFragment: Fragment(), OnClickListener {

    private lateinit var binding: FragmentCandidateBinding
    private var newLetterList = mutableListOf<String>()
    private val candidateViewModel: CandidateViewModel by activityViewModels()
    private lateinit var adapter: CandidateNewsLetterAdapter
    private lateinit var candidateUrlAdapter: CandidateUrlAdapter
    private lateinit var candidate: Candidate
    private lateinit var candidateUrl: MutableList<CandidateUrl>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_candidate, container, false)
        TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            manifestoURLText.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            manifestoCard.setOnClickListener {
                openURL(manifestoURLText.text.toString())
            }
            candidateUrl = mutableListOf()
            runBlocking(Dispatchers.IO) {
                candidate = candidateViewModel.getCandidate()[0]
                if (candidate.newsLetterUrls != null) {
                    val newsLetter = candidate.newsLetterUrls!!.toMutableList()
                    newLetterList.addAll(newsLetter)
                } else
                    newLetterList.addAll(mutableListOf())

                with(candidate.urls) {
                    if (this != null) {
                        if (this.facebook != null)
                            candidateUrl.add(CandidateUrl(R.drawable.facebook, this.facebook.toString()))

                        if (this.youtube != null)
                            candidateUrl.add(CandidateUrl(R.drawable.youtube, this.youtube.toString()))

                        if (this.twitter != null)
                            candidateUrl.add(CandidateUrl(R.drawable.twitter, this.twitter.toString()))

                        if (this.Instagram != null)
                            candidateUrl.add(CandidateUrl(R.drawable.instagram, this.Instagram.toString()))
                    }
                }
            }
            if (newLetterList.size > 0) {
                adapter = CandidateNewsLetterAdapter(requireContext(), newLetterList, this@CandidateFragment)
                newsLetterRecycler.layoutManager = LinearLayoutManager(requireContext())
                newsLetterRecycler.adapter = adapter
            }

            if (candidateUrl.size > 0) {
                candidateUrlAdapter = CandidateUrlAdapter(candidateUrl, requireContext(), this@CandidateFragment)
                candidateUrlRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                candidateUrlRecycler.adapter = candidateUrlAdapter
            }
        }
    }

    private fun openURL(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onClick(url: String) {
        openURL(url)
    }

}