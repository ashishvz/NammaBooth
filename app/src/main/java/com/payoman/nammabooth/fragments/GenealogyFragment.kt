package com.payoman.nammabooth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.GenealogyAdapter
import com.payoman.nammabooth.databinding.FragmentGenealogyBinding
import com.payoman.nammabooth.viewmodels.GenealogyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            setObservers()
            genealogyRecycler.layoutManager = LinearLayoutManager(requireContext())
            genealogyAdapter = GenealogyAdapter(requireContext(), mutableListOf())
            genealogyRecycler.adapter = genealogyAdapter
            lifecycleScope.launch(Dispatchers.IO) {
                genealogyViewModel.getVoter(voterIdArgs)
            }
        }
    }

    private fun setObservers() {
        genealogyViewModel.voter.observe(viewLifecycleOwner) { voter ->
            if (voter != null) {
                binding.apply {
                    voterId.text = voter.voterId ?: "NA"
                    voterName.text = String.format(Locale.ENGLISH, "%s(%s)", voter.voterNameEn ?: "NA", voter.voterNameKan ?: "NA")
                    voterMobileNumber.text = if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
                    voterHouseNo.text = if (!voter.houseNo.isNullOrEmpty()) voter.houseNo else if (!voter.houseNoEn.isNullOrEmpty()) voter.houseNoEn else "-"
                    gridTop.visibility = View.VISIBLE
                    voterLoadingProgressBar.hide()
                    loadingProgressBar.show()
                }
                lifecycleScope.launch(Dispatchers.IO) {
                    if (!voter.houseNo.isNullOrEmpty())
                        genealogyViewModel.getFamilyOfVoter(voter.houseNo!!, voter.voterId!!)
                    else if (!voter.houseNoEn.isNullOrEmpty())
                        genealogyViewModel.getFamilyOfVoter(voter.houseNoEn!!, voter.voterId!!)
                    else
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "House.No not found!", Toast.LENGTH_SHORT).show()
                            binding.loadingProgressBar.hide()
                        }
                }
            }
        }
        genealogyViewModel.familyList.observe(viewLifecycleOwner) {
            binding.apply {
                loadingProgressBar.hide()
                if (it != null) {
                    if (it.size > 0) {
                        noVoters.visibility = View.GONE
                        genealogyAdapter.updateData(it)
                    } else {
                        genealogyAdapter.clearData()
                        noVoters.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        genealogyViewModel.clearData()
    }
}