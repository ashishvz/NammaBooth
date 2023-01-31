package com.payoman.nammabooth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.GenealogyAdapter
import com.payoman.nammabooth.databinding.FragmentGenealogyBinding
import com.payoman.nammabooth.viewmodels.GenealogyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*

class GenealogyFragment : Fragment() {

    private lateinit var binding: FragmentGenealogyBinding
    private lateinit var genealogyAdapter: GenealogyAdapter
    private val genealogyViewModel: GenealogyViewModel by activityViewModels()
    private val args: GenealogyFragmentArgs by navArgs()
    private lateinit var voterIdArgs: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_genealogy, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            voterIdArgs = args.voterId
            gridTop.visibility = View.INVISIBLE
            voterLoadingProgressBar.show()
            genealogyRecycler.layoutManager = LinearLayoutManager(requireContext())
            genealogyAdapter = GenealogyAdapter(requireContext(), mutableListOf())
            genealogyRecycler.adapter = genealogyAdapter
            runBlocking(Dispatchers.IO) {
                genealogyViewModel.getVoter(voterIdArgs)
            }
            setObservers()
        }
    }

    private fun setObservers() {
        genealogyViewModel.voter.observe(viewLifecycleOwner) { voter ->
            if (voter != null) {
                binding.apply {
                    voterId.text = voter.voterId ?: "NA"
                    voterName.text = String.format(Locale.ENGLISH, "%s(%s)", voter.voterNameEn ?: "NA", voter.voterNameKan ?: "NA")
                    voterMobileNumber.text = if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
                    voterHouseNo.text = voter.houseNo
                    gridTop.visibility = View.VISIBLE
                    voterLoadingProgressBar.hide()
                    loadingProgressBar.show()
                }
                runBlocking(Dispatchers.IO) {
                    genealogyViewModel.getFamilyOfVoter(voter.houseNo!!, voter.voterId!!)
                }
            }
        }
        genealogyViewModel.familyList.observe(viewLifecycleOwner) {
            binding.apply {
                if (it.size > 0) {
                    noVoters.visibility = View.GONE
                    genealogyAdapter.updateData(it)
                } else {
                    genealogyAdapter.clearData()
                    noVoters.visibility = View.VISIBLE
                }
                loadingProgressBar.hide()
            }
        }

    }
}