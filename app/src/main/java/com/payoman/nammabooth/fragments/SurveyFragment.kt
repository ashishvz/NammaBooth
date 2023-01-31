package com.payoman.nammabooth.fragments

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.UpdatePhoneAdapter
import com.payoman.nammabooth.adapters.VoterListAdapter
import com.payoman.nammabooth.database.UpdatePhoneNumber
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.databinding.FragmentVoterListBinding
import com.payoman.nammabooth.dialogs.SurveyListDialog
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.viewmodels.VoterListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.util.*

class SurveyFragment : Fragment(), OnVoterCardClickListener {

    private lateinit var binding: FragmentVoterListBinding
    private val voterListViewModel: VoterListViewModel by activityViewModels()
    private val voterList: MutableList<Voter> = mutableListOf()
    private var voterListAdapter: VoterListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_voter_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            loadingProgressBar.show()
            voterListAdapter = VoterListAdapter(voterList, requireActivity(), this@SurveyFragment)
            voterRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            voterRecyclerView.setHasFixedSize(true)
            voterRecyclerView.adapter = voterListAdapter

            searchChipButton.setOnClickListener {
                loadingProgressBar.show()
                if (!searchEditText.text.isNullOrBlank())
                    getFilteredVoterList(searchEditText.text.toString())
                else
                    getAllVoterList()
            }

            clearSearch.setOnClickListener {
                searchEditText.text!!.clear()
                loadingProgressBar.show()
                getAllVoterList()
            }

            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchChipButton.performClick()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            searchEditText.doOnTextChanged { _, _, _, count ->
                if (count == 0) {
                    clearSearch.performClick()
                }
            }
        }
        setObservers()
        binding.loadingProgressBar.show()
        getAllVoterList()
    }

    private fun setObservers() {
        voterListViewModel.voterList.observe(viewLifecycleOwner) { result ->
            voterList.clear()
            if (result.isEmpty()) {
                binding.noVoters.visibility = View.VISIBLE
            } else {
                voterList.addAll(result)
                binding.noVoters.visibility = View.GONE
            }
            binding.loadingProgressBar.hide()
            voterListAdapter!!.updateVoterList(voterList.toMutableList())
        }
    }

    private fun getAllVoterList() {
        runBlocking(Dispatchers.IO) {
            voterListViewModel.getVoterList()
        }
    }

    private fun getFilteredVoterList(query: String) {
        runBlocking(Dispatchers.IO) {
            voterListViewModel.searchVoters(query)
        }
    }

    override fun OnClick(voter: Voter) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.voter_survey_bottom_sheet)
        val voterDetail = dialog.findViewById<MaterialTextView>(R.id.voterDetail)
        val voterNumber = dialog.findViewById<MaterialTextView>(R.id.voterPhoneNumber)
        val initiateSurveyButton = dialog.findViewById<MaterialButton>(R.id.initiateSurveyButton)
        val loadingProgressBar = dialog.findViewById<ContentLoadingProgressBar>(R.id.loadingProgressBar)
        loadingProgressBar!!.hide()
        voterDetail!!.text = String.format(Locale.ENGLISH, "%s-%s", voter.voterNameEn, voter.voterId)
        voterNumber!!.text = if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
        initiateSurveyButton?.setOnClickListener {
            if (voter.mobileNo!!.isEmpty()) {
                Toast.makeText(requireContext(), "Voter number not found:)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialog.dismiss()
            findNavController().navigate(SurveyFragmentDirections.actionSurveyFragmentToSurveyListFragment(voter.mobileNo!!.toLong()))
        }
        dialog.show()
    }
}