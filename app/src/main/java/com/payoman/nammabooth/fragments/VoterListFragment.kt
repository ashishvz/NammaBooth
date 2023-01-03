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
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.FileProvider
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
        binding.loadingProgressBar.show()
        binding.voterRecyclerView.setHasFixedSize(true)
        voterListAdapter = VoterListAdapter(voterList, requireActivity(), this@VoterListFragment)
        binding.voterRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        runBlocking(Dispatchers.IO) {
            voterList.addAll(voterListViewModel.getVoterList())
        }
        if (voterList.size > 0)
            voterListAdapter!!.updateVoterList(voterList.toMutableList())
        else
            binding.noVoters.visibility = View.VISIBLE
        binding.voterRecyclerView.adapter = voterListAdapter
        binding.loadingProgressBar.hide()


        /*binding.searchChipButton.setOnClickListener {

        }*/

        binding.searchChipButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.noVoters.visibility = View.GONE
                binding.loadingProgressBar.show()
                if (!binding.searchEditText.text.isNullOrBlank()) {
                    runBlocking(Dispatchers.IO) {
                        voterList.clear()
                        voterList.addAll(voterListViewModel.searchVoters(binding.searchEditText.text.toString()))
                    }
                } else
                    runBlocking(Dispatchers.IO) {
                        voterList.clear()
                        voterList.addAll(voterListViewModel.getVoterList())
                    }
                binding.loadingProgressBar.hide()
                if (voterList.isEmpty())
                    binding.noVoters.visibility = View.VISIBLE
                voterListAdapter!!.updateVoterList(voterList.toMutableList())
            }
        })


        binding.clearSearch.setOnClickListener {
            binding.searchEditText.text!!.clear()
            binding.loadingProgressBar.show()
            runBlocking(Dispatchers.IO) {
                voterList.clear()
                voterList.addAll(voterListViewModel.getVoterList())
            }
            binding.loadingProgressBar.hide()
            voterListAdapter!!.updateVoterList(voterList.toMutableList())
        }


        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.searchChipButton.performClick()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.searchEditText.doOnTextChanged { _, _, _, count ->
            if (count == 0) {
                binding.clearSearch.performClick()
            }
        }
    }

    override fun OnClick(voter: Voter) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.voter_detail_bottom_sheet)
        val voterDetail = dialog.findViewById<MaterialTextView>(R.id.voterDetail)
        val voterNumber = dialog.findViewById<MaterialTextView>(R.id.voterPhoneNumber)
        val updatePhoneNumberButton = dialog.findViewById<MaterialButton>(R.id.editPhone)
        val sendSlipButton = dialog.findViewById<MaterialButton>(R.id.sendSlip)
        val loadingProgressBar =
            dialog.findViewById<ContentLoadingProgressBar>(R.id.loadingProgressBar)
        loadingProgressBar!!.hide()
        voterDetail!!.text =
            String.format(Locale.ENGLISH, "%s-%s", voter.voterNameEn, voter.voterId)
        voterNumber!!.text = if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
        updatePhoneNumberButton!!.setOnClickListener {
            val uDialog = Dialog(requireContext())
            uDialog.setContentView(R.layout.update_phone_number)
            val phoneNumberEditText =
                uDialog.findViewById<TextInputEditText>(R.id.phoneNumberEditText)
            val updatePhoneButton = uDialog.findViewById<MaterialButton>(R.id.updatePhoneButton)
            val cancelButton = uDialog.findViewById<MaterialButton>(R.id.cancelButton)
            val loadingProgressbar =
                uDialog.findViewById<ContentLoadingProgressBar>(R.id.loadingProgressBar)
            loadingProgressbar.hide()
            phoneNumberEditText.setText(voter.mobileNo ?: "0")
            updatePhoneButton.setOnClickListener {
                if (phoneNumberEditText.text != null && phoneNumberEditText.text!!.length > 9) {
                    loadingProgressbar.show()
                    phoneNumberEditText.isEnabled = false
                    updatePhoneButton.isEnabled = false
                    cancelButton.isEnabled = false
                    runBlocking {
                        voterListViewModel.updatePhoneNumber(
                            voter.voterId!!,
                            phoneNumberEditText.text.toString()
                        )
                    }
                    val retrofit = AppUtils.RetrofitClient.getInstance()
                    val updatePhoneNumberInterface =
                        retrofit.create(UpdatePhoneNumberInterface::class.java)
                    runBlocking {
                        updatePhoneNumberInterface.updatePhoneNumber(
                            voter.voterId!!,
                            phoneNumberEditText.text.toString(),
                            AppUtils.DataStorePreference.getTokenFromDataStorePreference(
                                voterListViewModel.provideDataStoreInstance()
                            ).first().token
                        ).enqueue(object : Callback<DefaultResponse> {
                            override fun onResponse(
                                call: Call<DefaultResponse>,
                                response: Response<DefaultResponse>
                            ) {
                                if (response.isSuccessful) {
                                    uDialog.dismiss()
                                    runBlocking(Dispatchers.IO) {
                                        voterList.clear()
                                        voterList.addAll(voterListViewModel.getVoterList())
                                    }
                                    if (voterList.size > 0) {
                                        binding.noVoters.visibility = View.GONE
                                        voterListAdapter!!.updateVoterList(voterList.toMutableList())
                                        binding.voterRecyclerView.adapter = voterListAdapter
                                    } else {
                                        binding.noVoters.visibility = View.VISIBLE
                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "Phone Number updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        response.body()!!.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to update phone number",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        })
                    }
                }
            }
            cancelButton.setOnClickListener {
                uDialog.dismiss()
            }
            uDialog.show()
        }
        sendSlipButton!!.setOnClickListener {
            if (!voter.mobileNo.isNullOrBlank() && voter.mobileNo!!.length > 9) {
                loadingProgressBar.show()
                sendSlipButton.isEnabled = false
                updatePhoneNumberButton.isEnabled = false
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
                sendPDFToWhatsapp(voter.mobileNo!!, filePath)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Invalid phone number", Toast.LENGTH_SHORT).show()
                sendSlipButton.isEnabled = true
                updatePhoneNumberButton.isEnabled = true
            }
        }
        dialog.show()
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