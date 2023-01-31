package com.payoman.nammabooth.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout.LayoutParams
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.api.GetVoterListInterface
import com.payoman.nammabooth.dataStore.IsLoggedIn
import com.payoman.nammabooth.dataStore.PartNoPreference
import com.payoman.nammabooth.dataStore.UserPreference
import com.payoman.nammabooth.database.User
import com.payoman.nammabooth.databinding.FragmentProfileSettingBinding
import com.payoman.nammabooth.models.VoterResponse
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.utils.Constants
import com.payoman.nammabooth.viewmodels.DataStorePreferenceViewModel
import com.payoman.nammabooth.viewmodels.OtpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class ProfileFragment: Fragment() {

    private lateinit var binding: FragmentProfileSettingBinding

    private val dataStorePreferenceViewModel: DataStorePreferenceViewModel by activityViewModels()
    private var partNoPreference: PartNoPreference? = null
    private var userPreference: UserPreference? = null
    private val otpViewModel: OtpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        runBlocking {
            partNoPreference = AppUtils.DataStorePreference.getPartNosFromDataStorePreference(dataStorePreferenceViewModel.provideDataStoreInstance()).first()
            userPreference = AppUtils.DataStorePreference.getDataFromDataStorePreference(dataStorePreferenceViewModel.provideDataStoreInstance()).first()
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_setting, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            appVersionText.text = Constants.BUILD_VERSION
            agentNameTextView.text = partNoPreference!!.boothAgentName
            phoneNumberTextView.text = String.format(Locale.ENGLISH, "%s-%s", "+91", userPreference!!.phoneNumber)
            refreshVotersCard.setOnClickListener {
                val alertDialog = AlertDialog.Builder(requireActivity())
                alertDialog.setTitle("Are you sure?")
                alertDialog.setCancelable(false)
                alertDialog.setMessage("You want to refresh voter list.")
                alertDialog.setPositiveButton("Yes") { di, _ ->
                    val dialog = Dialog(requireContext(), android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
                    dialog.setCancelable(false)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.loading_layout)
                    di.dismiss()
                    dialog.show()
                    runBlocking(Dispatchers.IO) {
                        deleteAndRefreshVoterData(dialog.findViewById(R.id.downloadText), dialog)
                    }
                }
                alertDialog.setNegativeButton("No") { di, _ ->
                    di.dismiss()
                }
                alertDialog.show()
            }
            logoutCard.setOnClickListener {
                val alertDialog = AlertDialog.Builder(requireActivity())
                alertDialog.setTitle("Are you sure?")
                alertDialog.setMessage("You want to logout.")
                alertDialog.setCancelable(false)
                alertDialog.setPositiveButton("Yes") { dialog, _ ->
                    runBlocking(Dispatchers.IO) {
                        otpViewModel.deleteAllData()
                        AppUtils.DataStorePreference.saveIsLoggedInToDataStorePreference(
                            IsLoggedIn(false),
                            dataStorePreferenceViewModel.provideDataStoreInstance())
                    }
                    dialog.dismiss()
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToOtpFragment())
                }
                alertDialog.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }
    }

    private fun deleteAndRefreshVoterData(text: MaterialTextView, dialog: Dialog) {
        setTextToDownloadText(getString(R.string.downloading_voter_s_list_please_wait), text)
        val getVoterListInterface = AppUtils.RetrofitClient.getInstance().create(GetVoterListInterface::class.java)
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
                                    setTextToDownloadText(getString(R.string.download_complete), text)
                                    val voterList = voterData.data
                                    if (voterList != null) {
                                        runBlocking(Dispatchers.IO) {
                                            otpViewModel.deleteAllVoters()
                                        }
                                        runBlocking(Dispatchers.IO) {
                                            otpViewModel.insertVoterList(voterList)
                                        }
                                        dialog.dismiss()
                                        Toast.makeText(requireContext(), "Voter list refreshed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            setTextToDownloadText("No voter's data found. Proceeding!", text)
                            dialog.dismiss()
                        }
                    }
                }

                override fun onFailure(call: Call<VoterResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Downloading voter's list failed", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    t.printStackTrace()
                }
            })
        }
    }

    private fun setTextToDownloadText(data: String, textView: MaterialTextView) {
        CoroutineScope(Dispatchers.Main).launch {
            textView.text = data
        }
    }
}