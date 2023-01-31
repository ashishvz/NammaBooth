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
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import com.payoman.nammabooth.repository.UpdatePhoneNumberRepository
import com.payoman.nammabooth.repository.VoterRepository
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.viewmodels.UpdatePhoneNumberViewModel
import com.payoman.nammabooth.viewmodels.VoterListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.util.*

class UpdatePhoneNumberFragment : Fragment(), OnVoterCardClickListener {

    private lateinit var binding: FragmentVoterListBinding
    private val updatePhoneNumberViewModel: UpdatePhoneNumberViewModel by activityViewModels()
    private val voterList: MutableList<Voter> = mutableListOf()
    private var updatePhoneAdapter: UpdatePhoneAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            loadingProgressBar.show()
            updatePhoneAdapter = UpdatePhoneAdapter(voterList, requireActivity(), this@UpdatePhoneNumberFragment)
            voterRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            voterRecyclerView.setHasFixedSize(true)
            voterRecyclerView.adapter = updatePhoneAdapter

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
        updatePhoneNumberViewModel.voterList.observe(viewLifecycleOwner) { result ->
            voterList.clear()
            if (result.isEmpty()) {
                binding.noVoters.visibility = View.VISIBLE
            } else {
                voterList.addAll(result)
                binding.noVoters.visibility = View.GONE
            }
            binding.loadingProgressBar.hide()
            updatePhoneAdapter!!.updateVoterList(voterList.toMutableList())
        }
    }

    private fun getAllVoterList() {
        runBlocking(Dispatchers.IO) {
            updatePhoneNumberViewModel.getVoterList()
        }
    }

    private fun getFilteredVoterList(query: String) {
        runBlocking(Dispatchers.IO) {
            updatePhoneNumberViewModel.searchVoters(query)
        }
    }

    override fun OnClick(voter: Voter) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.update_phone_bottom_sheet)
        val voterDetail = dialog.findViewById<MaterialTextView>(R.id.voterDetail)
        val voterNumber = dialog.findViewById<MaterialTextView>(R.id.voterPhoneNumber)
        val updatePhoneNumberButton = dialog.findViewById<MaterialButton>(R.id.updatePhoneNumberButton)
        val loadingProgressBar = dialog.findViewById<ContentLoadingProgressBar>(R.id.loadingProgressBar)
        loadingProgressBar!!.hide()
        voterDetail!!.text = String.format(Locale.ENGLISH, "%s-%s", voter.voterNameEn, voter.voterId)
        voterNumber!!.text = if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
         updatePhoneNumberButton!!.setOnClickListener {
             val uDialog = Dialog(requireContext())
             uDialog.setContentView(R.layout.update_phone_number)
             val phoneNumberEditText =
                 uDialog.findViewById<TextInputEditText>(R.id.phoneNumberEditText)
             val updatePhoneButton = uDialog.findViewById<MaterialButton>(R.id.updatePhoneButton)
             val cancelButton = uDialog.findViewById<MaterialButton>(R.id.cancelButton)
             val loadingProgressbar = uDialog.findViewById<ContentLoadingProgressBar>(R.id.loadingProgressBar)
             loadingProgressbar.hide()
             phoneNumberEditText.setText(voter.mobileNo ?: "0")
             updatePhoneButton.setOnClickListener {
                 loadingProgressbar.show()
                 if (phoneNumberEditText.text != null && phoneNumberEditText.text!!.length > 9) {
                     if (phoneNumberEditText.text.toString().trim() == voter.mobileNo.toString().trim()) {
                         Toast.makeText(requireContext(), "Same phone number as old. Cannot update!", Toast.LENGTH_SHORT).show()
                         return@setOnClickListener
                     } else {
                         if (AppUtils.InternetCheckUtils.isConnected()) {
                             val updatePhoneNumberList = mutableListOf<UpdatePhoneNumber>()
                             updatePhoneNumberList.add(UpdatePhoneNumber(voter.voterId, phoneNumberEditText.text.toString().trim(), null, null))
                             val response = runBlocking(Dispatchers.IO) {
                                 updatePhoneNumberViewModel.postUpdatePhoneNumber(updatePhoneNumberList).body()
                             }
                             if (response != null) {
                                 if (response.status.equals("SUCCESS")) {
                                     runBlocking(Dispatchers.IO) {
                                         updatePhoneNumberViewModel.updatePhoneNumber(voter.voterId.toString(), phoneNumberEditText.text.toString().trim())
                                     }
                                     Toast.makeText(requireContext(), "Phone number updated :)", Toast.LENGTH_SHORT).show()
                                 } else
                                     Toast.makeText(requireContext(), "Failed to update phone number :(", Toast.LENGTH_SHORT).show()
                             } else {
                                 Log.d(tag, "Response is null!")
                             }
                         } else {
                             runBlocking(Dispatchers.IO) {
                                 updatePhoneNumberViewModel.insertUpdatePhoneNumber(UpdatePhoneNumber(voter.voterId, phoneNumberEditText.text.toString().trim(), null, null))
                                 updatePhoneNumberViewModel.updatePhoneNumber(voter.voterId.toString(), phoneNumberEditText.text.toString().trim())
                             }
                             getAllVoterList()
                             Toast.makeText(requireContext(), "Phone number updated :)", Toast.LENGTH_SHORT).show()
                         }
                         loadingProgressbar.hide()
                         dialog.dismiss()
                         uDialog.dismiss()
                     }
                 }
             }
             cancelButton.setOnClickListener {
                 uDialog.dismiss()
             }
             uDialog.show()
         }
        dialog.show()
    }
}