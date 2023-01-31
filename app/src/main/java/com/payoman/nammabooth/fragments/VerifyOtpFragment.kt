package com.payoman.nammabooth.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.payoman.nammabooth.R
import com.payoman.nammabooth.api.GetCandidateDetails
import com.payoman.nammabooth.api.GetOtpInterface
import com.payoman.nammabooth.api.GetVoterListInterface
import com.payoman.nammabooth.api.VerifyOtpInterface
import com.payoman.nammabooth.dataStore.IsLoggedIn
import com.payoman.nammabooth.dataStore.PartNoPreference
import com.payoman.nammabooth.dataStore.TokenPreference
import com.payoman.nammabooth.databinding.FragmentVerifyOtpBinding
import com.payoman.nammabooth.dataStore.UserPreference
import com.payoman.nammabooth.database.Candidate
import com.payoman.nammabooth.models.*
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.viewmodels.OtpViewModel
import io.realm.RealmList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import java.util.*

class VerifyOtpFragment : Fragment() {

    lateinit var binding: FragmentVerifyOtpBinding
    private lateinit var otpTimer: CountDownTimer
    private var userPreference: UserPreference? = null
    private val otpViewModel: OtpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_otp, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        otpTimer = object : CountDownTimer(15000, 1000) {
            override fun onTick(p0: Long) {
                binding.resendOtpButton.text =
                    String.format(Locale.ENGLISH, "%d%s", p0 / 1000, "sec")
            }

            override fun onFinish() {
                binding.resendOtpButton.isEnabled = true
                binding.resendOtpButton.text = requireActivity().getString(R.string.resend_otp)
                otpTimer.cancel()
            }

        }
        binding.resendOtpButton.isEnabled = false
        runBlocking(Dispatchers.IO) {
            userPreference = AppUtils.DataStorePreference.getDataFromDataStorePreference(otpViewModel.provideDataStoreInstance()).first()
        }
        binding.desc.append(" ${userPreference!!.phoneNumber}")
        otpTimer.start()
        binding.loadingProgressBar.hide()

        binding.verifyOtpButton.setOnClickListener {
            val retrofit = AppUtils.RetrofitClient.getInstance()
            binding.apply {
                loadingProgressBar.show()
                verifyOtpButton.isEnabled = false
                resendOtpButton.isEnabled = false
                otpEditText.isEnabled = false
                downloadText.visibility = View.VISIBLE
                setTextToDownloadText(getString(R.string.verifying_otp))
                if (otpEditText.text.isNullOrBlank() || otpEditText.text!!.length == 4) {
                    CoroutineScope(Dispatchers.IO).launch {
                        verifyOtp(retrofit)
                    }
                }
            }
        }

        binding.resendOtpButton.setOnClickListener {
            binding.apply {
                val retrofit = AppUtils.RetrofitClient.getInstance()
                loadingProgressBar
                verifyOtpButton.isEnabled = false
                resendOtpButton.isEnabled = false
                otpEditText.isEnabled = false
                val getOtpInterface = retrofit.create(GetOtpInterface::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    getOtpInterface.getOtp(userPreference!!.phoneNumber)
                        .enqueue(object : Callback<DefaultResponse> {
                            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                                if (response.isSuccessful) {
                                    loadingProgressBar.hide()
                                    verifyOtpButton.isEnabled = true
                                    resendOtpButton.isEnabled = false
                                    otpEditText.isEnabled = true
                                    setTextToDownloadText("Otp resent to your phone!")
                                    otpTimer.start()
                                }
                            }

                            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                loadingProgressBar.hide()
                                setTextToDownloadText("OTP generation failed!")
                                t.printStackTrace()
                            }
                        })
                }
            }
        }
    }

    private suspend fun verifyOtp(retrofit: Retrofit) {
        val verifyOtpInterface = retrofit.create(VerifyOtpInterface::class.java)
        verifyOtpInterface.verifyOtp(userPreference!!.phoneNumber, binding.otpEditText.text.toString(), null)
            .enqueue(object : Callback<VerifyOtpResponse> {
                override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
                    if (response.isSuccessful) {
                        val verifyOtpResponse = response.body()
                        if (verifyOtpResponse != null) {
                            if (verifyOtpResponse.token != null) {
                                runBlocking {
                                    AppUtils.DataStorePreference.saveTokenToDataStorePreference(TokenPreference(verifyOtpResponse.token.toString()), otpViewModel.provideDataStoreInstance())
                                    AppUtils.DataStorePreference.savePartNosToDataStorePreference(PartNoPreference(
                                            verifyOtpResponse.partNos.toString(),
                                            verifyOtpResponse.assemblyConstituencyNumber.toString(),
                                            verifyOtpResponse.boothAgentName.toString()), otpViewModel.provideDataStoreInstance())
                                }
                                setTextToDownloadText(getString(R.string.otp_verified))
                                otpTimer.cancel()
                                getVotersListAndPopulate(retrofit)
                            }
                        } else
                            setTextToDownloadText("OTP verification failed")
                    } else {
                        binding.loadingProgressBar.hide()
                        binding.verifyOtpButton.isEnabled = true
                        binding.resendOtpButton.isEnabled = true
                        binding.otpEditText.isEnabled = true
                        setTextToDownloadText("Invalid OTP. Please try again!")
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                    setTextToDownloadText("OTP verification failed. Please try again!")
                    binding.loadingProgressBar.hide()
                    binding.verifyOtpButton.isEnabled = true
                    binding.resendOtpButton.isEnabled = true
                    binding.otpEditText.isEnabled = true
                    t.printStackTrace()
                }
            }
        )
    }

    private fun getVotersListAndPopulate(retrofit: Retrofit) {
        setTextToDownloadText(getString(R.string.downloading_voter_s_list_please_wait))
        val getVoterListInterface =  retrofit.create(GetVoterListInterface::class.java)
        runBlocking {
            getVoterListInterface.getVoterList(
                AppUtils.DataStorePreference.getPartNosFromDataStorePreference(otpViewModel.provideDataStoreInstance()).first().partNos,
                AppUtils.DataStorePreference.getTokenFromDataStorePreference(otpViewModel.provideDataStoreInstance()).first().token
            ).enqueue(object : Callback<VoterResponse> {
                override fun onResponse(call: Call<VoterResponse>, response: Response<VoterResponse>) {
                    if (response.isSuccessful) {
                        val voterData = response.body()
                        if (voterData != null) {
                            if (voterData.status.equals("SUCCESS")) {
                                if (voterData.size!!.toInt() > 0) {
                                    setTextToDownloadText(getString(R.string.download_complete))
                                    val voterList = voterData.data
                                    if (voterList != null) {
                                        runBlocking(Dispatchers.IO) {
                                            otpViewModel.insertVoterList(voterList)
                                        }
                                        setTextToDownloadText("Voter's list saved")
                                        CoroutineScope(Dispatchers.IO).launch {
                                            getCandidateDetails(retrofit)
                                        }
                                    }
                                }
                            }
                        } else {
                            setTextToDownloadText("No voter's data found. Proceeding!")
                            CoroutineScope(Dispatchers.IO).launch {
                                getCandidateDetails(retrofit)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<VoterResponse>, t: Throwable) {
                    setTextToDownloadText("Downloading voter's list failed")
                    t.printStackTrace()
                }
            })
        }
    }

    private fun getCandidateDetails(retrofit: Retrofit) {
        setTextToDownloadText(getString(R.string.downloading_candidate_details))
        val getCandidateDetails = retrofit.create(GetCandidateDetails::class.java)
        runBlocking {
            val preference = AppUtils.DataStorePreference.getPartNosFromDataStorePreference(otpViewModel.provideDataStoreInstance()).first()
            getCandidateDetails.getCandidateDetails(
                preference.constituencyNumber,
                "1",
                AppUtils.DataStorePreference.getTokenFromDataStorePreference(otpViewModel.provideDataStoreInstance()).first().token
            ).enqueue(
                object : Callback<CandidateResponse> {
                    override fun onResponse(call: Call<CandidateResponse>, response: Response<CandidateResponse>) {
                        if (response.isSuccessful) {
                            val candidateResponse = response.body()
                            if (candidateResponse != null) {
                                setTextToDownloadText("Saving the data!")
                                runBlocking(Dispatchers.IO) {
                                    otpViewModel.insertCandidate(candidateResponse.data)
                                }
                                setTextToDownloadText("Data saved!")
                                runBlocking {
                                    AppUtils.DataStorePreference.saveIsLoggedInToDataStorePreference(
                                        IsLoggedIn(true),
                                        otpViewModel.provideDataStoreInstance())
                                }
                                findNavController().navigate(VerifyOtpFragmentDirections.actionVerifyOtpFragmentToHomeFragment())
                            } else {
                                setTextToDownloadText("Failed in fetching candidate details")
                            }
                        } else
                            setTextToDownloadText("Candidate API failed")
                    }

                    override fun onFailure(call: Call<CandidateResponse>, t: Throwable) {
                        setTextToDownloadText("Candidate API failed")
                        t.printStackTrace()
                    }
                })
        }
    }

    private fun setTextToDownloadText(data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.downloadText.text = data
        }
    }
}


