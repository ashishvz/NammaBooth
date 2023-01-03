package com.payoman.nammabooth.fragments

import android.os.Bundle
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
import com.payoman.nammabooth.databinding.FragmentOtpBinding
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.dataStore.UserPreference
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.viewmodels.OtpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OtpFragment : Fragment() {

    lateinit var binding: FragmentOtpBinding
    private val otpViewModel : OtpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingProgressBar.hide()
        binding.getOtpButton.setOnClickListener {
            if (binding.phoneNumberEditText.text.isNullOrBlank() || binding.phoneNumberEditText.text!!.length < 9) {
                binding.phoneNumberEditText.error = "Invalid phone number"
                return@setOnClickListener
            }
            binding.getOtpButton.isEnabled = false
            binding.phoneNumberEditText.isEnabled = false
            binding.loadingProgressBar.show()
            val retrofit = AppUtils.RetrofitClient.getInstance()
            val getOtpInterface = retrofit.create(GetOtpInterface::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                getOtpInterface.getOtp(binding.phoneNumberEditText.text.toString())
                    .enqueue(object : Callback<DefaultResponse> {
                        override fun onResponse(
                            call: Call<DefaultResponse>,
                            response: Response<DefaultResponse>
                        ) {
                            val defaultResponse = response.body()
                            if (defaultResponse != null) {
                                if (defaultResponse.status.equals("SUCCESS")) {
                                    runBlocking(Dispatchers.IO) {
                                        AppUtils.DataStorePreference.saveDataToDataStorePreference(
                                            UserPreference(binding.phoneNumberEditText.text.toString()),
                                            otpViewModel.provideDataStoreInstance()
                                        )
                                    }
                                }
                                findNavController().navigate(OtpFragmentDirections.actionOtpFragmentToVerifyOtpFragment())
                            } else if (response.errorBody() != null) {
                                val responseBody = Gson().fromJson(response.errorBody()!!.charStream(), DefaultResponse::class.java)
                                Toast.makeText(
                                    activity,
                                    responseBody.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            binding.loadingProgressBar.hide()
                            binding.phoneNumberEditText.isEnabled = true
                            binding.getOtpButton.isEnabled = true
                        }

                        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                            binding.loadingProgressBar.hide()
                            binding.phoneNumberEditText.isEnabled = true
                            binding.getOtpButton.isEnabled = true
                            Toast.makeText(activity, "OTP generation failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }
    }
}