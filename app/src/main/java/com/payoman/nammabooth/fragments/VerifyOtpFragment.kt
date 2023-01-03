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
import com.payoman.nammabooth.api.GetOtpInterface
import com.payoman.nammabooth.api.GetVoterListInterface
import com.payoman.nammabooth.api.VerifyOtpInterface
import com.payoman.nammabooth.dataStore.IsLoggedIn
import com.payoman.nammabooth.dataStore.PartNoPreference
import com.payoman.nammabooth.dataStore.TokenPreference
import com.payoman.nammabooth.databinding.FragmentVerifyOtpBinding
import com.payoman.nammabooth.dataStore.UserPreference
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.VerifyOtpResponse
import com.payoman.nammabooth.models.Voter
import com.payoman.nammabooth.models.VoterResponse
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.viewmodels.OtpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
                binding.resendOtpButton.text = getString(R.string.resend_otp)
                otpTimer.cancel()
            }

        }
        binding.resendOtpButton.isEnabled = false
        runBlocking(Dispatchers.IO) {
            userPreference =
                AppUtils.DataStorePreference.getDataFromDataStorePreference(otpViewModel.provideDataStoreInstance())
                    .first()
        }
        binding.desc.append(" ${userPreference!!.phoneNumber}")
        otpTimer.start()
        binding.loadingProgressBar.hide()
        binding.verifyOtpButton.setOnClickListener {
            binding.loadingProgressBar.show()
            binding.verifyOtpButton.isEnabled = false
            binding.resendOtpButton.isEnabled = false
            if (binding.otpEditText.text.isNullOrBlank() || binding.otpEditText.text!!.length == 4) {
                val retrofit = AppUtils.RetrofitClient.getInstance()
                val verifyOtpInterface = retrofit.create(VerifyOtpInterface::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    verifyOtpInterface.verifyOtp(
                        userPreference!!.phoneNumber,
                        binding.otpEditText.text.toString()
                    ).enqueue(
                        object : Callback<VerifyOtpResponse> {
                            override fun onResponse(
                                call: Call<VerifyOtpResponse>,
                                response: Response<VerifyOtpResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val verifyOtpResponse = response.body()
                                    if (verifyOtpResponse != null) {
                                        if (verifyOtpResponse.token != null) {
                                            runBlocking {
                                                AppUtils.DataStorePreference.saveTokenToDataStorePreference(
                                                    TokenPreference(verifyOtpResponse.token.toString()),
                                                    otpViewModel.provideDataStoreInstance()
                                                )
                                                AppUtils.DataStorePreference.savePartNosToDataStorePreference(
                                                    PartNoPreference(verifyOtpResponse.partNos.toString()),
                                                    otpViewModel.provideDataStoreInstance()
                                                )
                                            }
                                            Toast.makeText(
                                                activity,
                                                "Otp verification success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            binding.downloadText.visibility = View.VISIBLE
                                            val getVoterListInterface =
                                                retrofit.create(GetVoterListInterface::class.java)
                                            runBlocking {
                                                getVoterListInterface.getVoterList(
                                                    AppUtils.DataStorePreference.getPartNosFromDataStorePreference(otpViewModel.provideDataStoreInstance()).first().partNos,
                                                    AppUtils.DataStorePreference.getTokenFromDataStorePreference(
                                                        otpViewModel.provideDataStoreInstance()
                                                    ).first().token
                                                )
                                                    .enqueue(object : Callback<VoterResponse> {
                                                        override fun onResponse(
                                                            call: Call<VoterResponse>,
                                                            response: Response<VoterResponse>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                val voterData = response.body()
                                                                if (voterData != null) {
                                                                    if (voterData.status.equals("SUCCESS")) {
                                                                        if (voterData.size!!.toInt() > 0) {
                                                                            binding.downloadText.text =
                                                                                getString(R.string.download_complete)
                                                                            val voterList = voterData.data
                                                                            if (voterList != null) {
                                                                                runBlocking {
                                                                                    otpViewModel.insertVoterList(
                                                                                        voterList
                                                                                    )
                                                                                }
                                                                                runBlocking {
                                                                                    AppUtils.DataStorePreference.saveIsLoggedInToDataStorePreference(
                                                                                        IsLoggedIn(
                                                                                            true
                                                                                        ),
                                                                                        otpViewModel.provideDataStoreInstance()
                                                                                    )
                                                                                }
                                                                                binding.downloadText.visibility =
                                                                                    View.GONE
                                                                                findNavController().navigate(
                                                                                    VerifyOtpFragmentDirections.actionVerifyOtpFragmentToHomeFragment()
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                } else {
                                                                    runBlocking {
                                                                        AppUtils.DataStorePreference.saveIsLoggedInToDataStorePreference(
                                                                            IsLoggedIn(
                                                                                true
                                                                            ),
                                                                            otpViewModel.provideDataStoreInstance()
                                                                        )
                                                                    }
                                                                    binding.downloadText.visibility =
                                                                        View.GONE
                                                                    findNavController().navigate(
                                                                        VerifyOtpFragmentDirections.actionVerifyOtpFragmentToHomeFragment()
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: Call<VoterResponse>,
                                                            t: Throwable
                                                        ) {
                                                            Toast.makeText(
                                                                activity,
                                                                "Downloading voter's list failed",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }

                                                    })

                                            }
                                            binding.loadingProgressBar.hide()
                                        }
                                    }
                                } else {
                                    binding.loadingProgressBar.hide()
                                    binding.verifyOtpButton.isEnabled = true
                                    binding.resendOtpButton.isEnabled = true
                                    Toast.makeText(
                                        activity,
                                        "Invalid OTP",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                                Toast.makeText(
                                    activity,
                                    "Otp verification failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    )
                }
            }
        }

        binding.resendOtpButton.setOnClickListener {
            binding.loadingProgressBar.show()
            binding.verifyOtpButton.isEnabled = false
            binding.resendOtpButton.isEnabled = false
            val retrofit = AppUtils.RetrofitClient.getInstance()
            val getOtpInterface = retrofit.create(GetOtpInterface::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                getOtpInterface.getOtp(userPreference!!.phoneNumber)
                    .enqueue(object : Callback<DefaultResponse> {
                        override fun onResponse(
                            call: Call<DefaultResponse>,
                            response: Response<DefaultResponse>
                        ) {
                            if (response.isSuccessful) {
                                binding.loadingProgressBar.hide()
                                binding.verifyOtpButton.isEnabled = true
                                binding.resendOtpButton.isEnabled = false
                                otpTimer.start()
                                Toast.makeText(activity, "OTP generated", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                            binding.loadingProgressBar.hide()
                            Toast.makeText(activity, "OTP generation failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }
    }
}


