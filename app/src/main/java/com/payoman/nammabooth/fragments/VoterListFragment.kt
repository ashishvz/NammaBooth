package com.payoman.nammabooth.fragments

import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.FileProvider
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
import com.payoman.nammabooth.adapters.VoterListAdapter
import com.payoman.nammabooth.api.UpdatePhoneNumberInterface
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.databinding.FragmentVoterListBinding
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.viewmodels.VoterListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.*


class VoterListFragment : Fragment(), OnVoterCardClickListener {

    lateinit var binding: FragmentVoterListBinding
    private val voterListViewModel: VoterListViewModel by activityViewModels()
    private val voterList: MutableList<Voter> = mutableListOf()
    private var voterListAdapter: VoterListAdapter? = null
    private lateinit var progressDialog: Dialog

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
            voterListAdapter = VoterListAdapter(voterList, requireActivity(), this@VoterListFragment)
            voterRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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
        voterListViewModel.sendSlip.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            if (it.status == "PROCESSING") {
                Toast.makeText(requireContext(), "Slip sent successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to send slip!", Toast.LENGTH_SHORT).show()
            }
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
        dialog.setContentView(R.layout.voter_detail_bottom_sheet)
        val voterDetail = dialog.findViewById<MaterialTextView>(R.id.voterDetail)
        val voterNumber = dialog.findViewById<MaterialTextView>(R.id.voterPhoneNumber)
        val familyTreeButton = dialog.findViewById<MaterialButton>(R.id.familyTree)
        val sendSlipButton = dialog.findViewById<MaterialButton>(R.id.sendSlip)
        val loadingProgressBar =
            dialog.findViewById<ContentLoadingProgressBar>(R.id.loadingProgressBar)
        loadingProgressBar!!.hide()
        voterDetail!!.text =
            String.format(Locale.ENGLISH, "%s-%s", voter.voterNameEn, voter.voterId)
        voterNumber!!.text = if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
        familyTreeButton?.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(VoterListFragmentDirections.actionVoterListFragmentToGenealogyFragment(voter.voterId.toString()))
        }
        sendSlipButton!!.setOnClickListener {
            if (!voter.mobileNo.isNullOrBlank() && voter.mobileNo!!.length > 9) {
                progressDialog = Dialog(requireContext())
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                progressDialog.setContentView(R.layout.progress_dialog)
                progressDialog.setCancelable(false)
                progressDialog.show()
                sendSlipButton.isEnabled = false
                //updatePhoneNumberButton.isEnabled = false
                val inflater = LayoutInflater.from(requireContext())
                val view = inflater.inflate(R.layout.voter_slip_layout, null)
                val voterName = view.findViewById<MaterialTextView>(R.id.voterName)
                val partNo = view.findViewById<MaterialTextView>(R.id.voterPartNo)
                val voterNo = view.findViewById<MaterialTextView>(R.id.voterNo)
                val voterBooth = view.findViewById<MaterialTextView>(R.id.voterBooth)
                val votingDate = view.findViewById<MaterialTextView>(R.id.votingDate)
                val voteCast = view.findViewById<MaterialTextView>(R.id.voteCasting)
                val candidateSerialNo = view.findViewById<MaterialTextView>(R.id.candidateSerialNo)
                voterName.text = String.format(
                    Locale.ENGLISH,
                    "%s %s %s",
                    "Dear,",
                    voter.voterNameEn,
                    "Your details of voting"
                )
                partNo.text = String.format(Locale.ENGLISH, "%s: %s", "Part No", voter.partNo)
                voterNo.text = String.format(Locale.ENGLISH, "%s: %s", "Voter No", "62")
                voterBooth.text =
                    String.format(Locale.ENGLISH, "%s: %s", "Booth", voter.poolingStation)

                val displayMetrics = DisplayMetrics()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requireContext().display?.getRealMetrics(displayMetrics)
                    displayMetrics.densityDpi
                } else {
                    requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
                }
                view.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
                    ),
                    View.MeasureSpec.makeMeasureSpec(
                        displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
                    )
                )
                view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
                val bitmap = Bitmap.createBitmap(
                    view.measuredWidth,
                    view.measuredHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                view.draw(canvas)
                Bitmap.createScaledBitmap(bitmap, 1500, 2000, true)
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(1500, 2000, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                page.canvas.drawBitmap(bitmap, 0F, 0F, null)
                pdfDocument.finishPage(page)
                val filePath = File(requireContext().getExternalFilesDir(null), "slip.pdf")
                pdfDocument.writeTo(FileOutputStream(filePath))
                pdfDocument.close()
                sendSlip(voter.mobileNo!!, filePath)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Invalid phone number", Toast.LENGTH_SHORT).show()
                sendSlipButton.isEnabled = true
               // updatePhoneNumberButton.isEnabled = true
            }
        }
        dialog.show()
    }

    private fun sendSlip(phoneNumber: String, pdfFile: File) {
        runBlocking(Dispatchers.IO) {
            val requestFile = pdfFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("document", pdfFile.name, requestFile)
            voterListViewModel.sendSlip(phoneNumber, body)
        }
    }

    private fun sendPDFToWhatsapp(phoneNumber: String, pdfFile: File) {
        val intent = Intent(Intent.ACTION_SEND)
        if (AppUtils.WhatsApp.whatsappInstalledOrNot("com.whatsapp", requireContext())) {
            intent.setPackage("com.whatsapp")
            intent.type = "application/pdf"
            intent.putExtra("jid", "91$phoneNumber@s.whatsapp.net")
            val pdfUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                pdfFile
            )
            intent.putExtra(Intent.EXTRA_STREAM, pdfUri)
            startActivity(intent)
        } else {
            Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT)
                .show();
        }
    }
}